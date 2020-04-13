package user;

import dates.CalendarGenerator;
import entities.*;
import exceptions.InvalidDateException;
import mt.Memo;
import mt.Tag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

/**
 * Responsible for serializing data into the file system, and deserializing data to be loaded into memory
 *
 * @author Ng Bob Shoaun
 */
public class DataSaver {

    private String basePath;

    /**
     * Initializes DataSaver
     *
     * @param basePath a base path relative to all path's passed into this DataSaver
     */
    public DataSaver(String basePath) {
        this.basePath = basePath + (!basePath.equals("") ? "/" : "");
    }

    /**
     * @param path The path relative to the base directory of the user data
     * @return loaded text file stream
     */
    public Scanner loadScannerFromFile(String path) throws FileNotFoundException {
        if (path.charAt(0) == '/')
            path = path.substring(1);
        return new Scanner(new File(basePath + path));
    }

    /**
     * @param path The path relative to the base directory of the user data
     * @return list of strings from file
     */
    public List<String> loadStringsFromFile(String path) throws IOException {
        return Files.readAllLines(Paths.get(basePath + path), StandardCharsets.UTF_8);
    }

    /**
     * @param path The path relative to the base directory of the user data
     * @return entire text file as a string
     */
    public String loadStringFromFile(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(basePath + path)));
    }

    /**
     * Save to file
     *
     * @param path     The path relative to the base directory of the user data
     * @param contents Data to save
     */
    public void saveToFile(String path, List<String> contents) throws IOException {
        try (PrintWriter out = new PrintWriter(basePath + path)) {
            for (String line : contents)
                out.println(line);
        } catch (FileNotFoundException e) {
            // file and/or directory doesn't exist
            File newFile = new File(basePath + path);
            newFile.getParentFile().mkdirs(); // create whole directory
            newFile.createNewFile();
            saveToFile(path, contents);
        }
    }

    /**
     * Save to file
     *
     * @param path     The path relative to the base directory of the user data
     * @param contents Data to save
     */
    public void saveToFile(String path, String contents) throws IOException {
        try (FileWriter fileWriter = new FileWriter(basePath + path)) {
            fileWriter.write(contents);
        } catch (FileNotFoundException e) {
            // file and/or directory doesn't exist
            File newFile = new File(basePath + path);
            newFile.getParentFile().mkdirs(); // create whole directory
            newFile.createNewFile();
            saveToFile(path, contents);
        }
    }

    /**
     * @param path The path relative to the base directory of the user data
     * @return the list of files in the given path
     */
    public File[] getFilesInDirectory(String path) {
        File[] files = new File(basePath + path).listFiles();
        if (files == null) {
            return new File[0];
        }
        return files;
    }

    /**
     * @param path The path relative to the base directory of the user data
     * @return the list of filenames in the given path
     */
    public String[] getFileNamesInDirectory(String path) {
        return new File(basePath + path).list();
    }

    public void makeDirectory(String path) {
        new File(basePath + path).mkdirs();
    }

    public void deleteDirectory(String path) throws IOException {
        Files.walk(Paths.get(basePath + path))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }


    public Calendar loadCalendar(String calendarName) {
        DataSaver calendarDataSaver = new DataSaver(basePath + "/" + calendarName);
        ArrayList<Memo> memos = new ArrayList<>();
        ArrayList<Tag> tags = new ArrayList<>();
        EventCollection eventCollections;
        ArrayList<Series> series = new ArrayList<>();
        ArrayList<AlertCollection> alertCollections = new ArrayList<>();
        //load EC

        eventCollections = new EventCollection(ECLoadHelper("events/"));
        eventCollections.setPostponedEvents(ECLoadHelper("events/postponed/"));

        //load memos
        try {
            memos = new ArrayList<>();
            Scanner scanner = loadScannerFromFile("memos.txt");
            while (scanner.hasNext()) {
                String memoData = scanner.nextLine();
                String[] parts = memoData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>(Arrays.asList(parts[2].split("[|]+")));
                memos.add(new Memo(parts[0], parts[1], idStrings));
            }
        } catch (FileNotFoundException ignored) {

        }
        //load tags
        try {
            tags = new ArrayList<>();
            Scanner scanner = loadScannerFromFile("tags.txt");
            while (scanner.hasNext()) {
                String tagData = scanner.nextLine();
                String[] parts = tagData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>(Arrays.asList(parts[1].split("[|]+")));
                tags.add(new Tag(parts[0], idStrings));
            }
        } catch (FileNotFoundException ignored) {

        }
        //Load existing series
        String[] filenames = getFileNamesInDirectory("series/");
        if (filenames != null) {
            for (String seriesName :
                    filenames) {
                try {
                    String[] info = loadStringFromFile("series/" + seriesName + "/BaseEvent.txt").split("\\n");
                    Event baseEvent = stringsToEvent(info);

                    String CG = loadStringFromFile("series/" + seriesName + "/CalenderGenerator.txt");
                    CalendarGenerator newCG = new CalendarGenerator(CG);
                    GregorianCalendar newStart = newCG.getStartTime();
                    GregorianCalendar newEnd = newCG.getStartTime();
                    List<Duration> durs = newCG.getPeriods();

                    Series newSeries = new SeriesFactory().getSeries(seriesName, baseEvent, newStart, newEnd, durs);
                    newSeries.setSubSeries(loadSubSeries(loadScannerFromFile("series/" + seriesName + "/SubSeries.txt")));

                    newSeries.setEvents(ECLoadHelper("series/" + seriesName + "/"));
                    newSeries.setPostponedEvents(ECLoadHelper("series/" + seriesName + "/postponed/"));

                    series.add(newSeries);
                } catch (IOException | InvalidDateException e) {
                    e.printStackTrace();
                }


            }
        }
        //Load existing alert collection series
        File[] files = calendarDataSaver.getFilesInDirectory("/alerts/");
        if (files != null) {
            for (File file :
                    files) {
                String name = file.getName();
                name = name.replaceAll(".txt", "");
                AlertCollection alertCollection = loadAlertCollection(name);
            }
        }
        return new Calendar(calendarName, Collections.singletonList(eventCollections), alertCollections, memos, tags, calendarDataSaver);
    }



    public void SaveCalendar(Calendar calendar) {
        //save EventCollection
        ECSaveHelper("events/", calendar.getSingleEventCollection().getEvents());
        ECSaveHelper("events/postponed/", calendar.getSingleEventCollection().getPostponedEvents());

        //save Series
        for (Series series : calendar.getSeries()) {
            ECSaveHelper("series/" + series.getName() + "/", series.getManualEvents());
            ECSaveHelper("series/" + series.getName() + "/postponed/", series.getPostponedEvents());
            try {
                saveSubSeries(series,series.getSubSeries());
                saveToFile("series/" + series.getName() + "/CalenderGenerator.txt", series.getCalGen().getString());
                saveToFile("series/" + series.getName() + "/BaseEvent.txt", series.getBaseEvent().getString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // save memos
        StringBuilder memoData = new StringBuilder();
        for (Memo memo :
                calendar.getMemos()) {
            StringBuilder ids = new StringBuilder();
            for (String id :
                    memo.getEvents()) {
                ids.append(id).append("|");
            }
            memoData.append(memo.getText()).append("§").append(memo.getTitle()).append("§").append(ids.toString()).append(String.format("%n"));
        }
        try {
            saveToFile("memos.txt", memoData.toString());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        // save tags
        StringBuilder tagsData = new StringBuilder();
        for (Tag tag :
                calendar.getTags()) {
            StringBuilder ids = new StringBuilder();
            for (String id :
                    tag.getEvents()) {
                ids.append(id).append("|");
            }
            tagsData.append(tag.getText()).append("§").append(ids.toString()).append(String.format("%n"));
        }
        try {
            saveToFile("tags.txt", tagsData.toString());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void ECSaveHelper(String path, List<Event> events) {
        for (Event e : events) {
            try {
                saveToFile(path + e.getId() + ".txt", e.getString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<Event> ECLoadHelper(String path) { // TODO: make use of ParseEventID()
        List<Event> loadedEvents = new ArrayList<>();
        File[] data = getFilesInDirectory(path);
        for (File f : data) {
            String id = f.getName();
            id = id.replaceAll(".txt", "");
            try {
                String[] eventData = loadStringFromFile(path + id + ".txt").split("\\n");
                String name = eventData[1];
                GregorianCalendar start = new GregorianCalendar();
                GregorianCalendar end = new GregorianCalendar();
                start.setTimeInMillis(Long.parseLong(eventData[2]));
                end.setTimeInMillis(Long.parseLong(eventData[3]));
                loadedEvents.add(new Event(name, start, end));

            } catch (IOException | InvalidDateException e) {
                e.printStackTrace();
            }
        }
        return loadedEvents;
    }
    private void saveSubSeries(Series series, List<SubSeries> subs) throws IOException {
        StringBuilder ret = new StringBuilder();
        for (SubSeries sub:subs){
            ret.append(sub.getString()).append("\n");
        }
        saveToFile("series/" + series.getName() + "/SubSeries.txt", ret.toString());
    }
    private List<SubSeries> loadSubSeries(Scanner subs) throws InvalidDateException {
        List<SubSeries> ret = new ArrayList<>();
        while (subs.hasNext()) {
            String subData = subs.nextLine();
            String[] parts = subData.split("§");
            Event baseEvent = stringsToEvent(parts);
            CalendarGenerator CG = new CalendarGenerator(parts[4].replaceAll("\\|","\n"));
            ret.add(new SubSeries(baseEvent,CG));
        }
        return ret;
    }
    private Event stringsToEvent(String[] parts) throws InvalidDateException {
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        start.setTimeInMillis(Long.parseLong(parts[2]));
        end.setTimeInMillis(Long.parseLong(parts[3]));
        return new Event(parts[1], start, end);
    }

    public void saveAlertCollection(AlertCollection ac) {
        List<String> contents = Arrays.asList(ac.getString().split("\\n+"));
        try {
            saveToFile("alerts/" + ac.getEventId() + ".txt", contents);
        } catch (IOException e) {
            System.out.println("Error while saving AlertCollection " + ac.getEventId());
            e.printStackTrace();
        }
    }


    /**
     * Load the data into this AlertCollection.
     *
     * @param eventId The ID of the event for which the Alerts are being loaded
     */
    public AlertCollection loadAlertCollection(String eventId) {
        AlertCollection ac = new AlertCollection(eventId, this);

        List<String> strings = null;
        try {
            strings = loadStringsFromFile("/alerts/" + eventId + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ac.setEventTime(parseEventId(eventId));

        if (strings == null) throw new AssertionError();
        eventId = strings.get(0).trim();

        String[] manTimes = strings.get(1).trim().split("\\n+");
        for (String timeStr : manTimes) {
            ac.getManAlerts().add(new Alert(eventId, timeStr));
        }

        StringBuilder cgStr = new StringBuilder();
        for (int i = 3; i < strings.size(); i++) {
            cgStr.append(strings.get(i));
        }
        if (!cgStr.toString().equals(""))
            ac.setCalGen(new CalendarGenerator(cgStr.toString()));

        return ac;
    }

    public GregorianCalendar parseEventId(String eventId) {
        GregorianCalendar eventTime = new GregorianCalendar();
        StringBuilder time = new StringBuilder();
        try {
            String[] times = eventId.split("%");
            String[] times2 = new String[times.length - 1];
            System.arraycopy(times, 1, times2, 0, times.length - 1);
            for (String s : times2) {
                time.append(s).append(" ");
            }
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd kk mm ss z yyyy", Locale.ENGLISH);
        try {
            eventTime.setTime(df.parse(time.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return eventTime;
    }

}