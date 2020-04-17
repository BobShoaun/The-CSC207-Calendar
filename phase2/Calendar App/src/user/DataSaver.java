package user;

import alert.Alert;
import alert.AlertCollection;
import dates.CalendarGenerator;
import event.*;
import exceptions.InvalidDateException;
import memotag.Memo;
import memotag.Tag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for serializing data into the file system, and deserializing data to be loaded into memory
 */
public class DataSaver {

    private final String basePath;

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

    public void deleteFile(String path) {
        File file = new File(basePath + path);
        file.delete();
    }

    public void deleteDirectory(String path) throws IOException {
        Files.walk(Paths.get(basePath + path))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    /**
     * Load a CalendarGenerator from its getString output
     *
     * @param cgStr String input
     * @return CalendarGenerator
     */
    public CalendarGenerator loadCalendarGenerator(String cgStr) {
        String[] information = cgStr.split("\n");
        GregorianCalendar startTime = new GregorianCalendar();
        startTime.setTimeInMillis(Long.parseLong(information[0].trim()));

        GregorianCalendar endTime = new GregorianCalendar();
        if(!information[1].equals(""))
            endTime.setTimeInMillis(Long.parseLong(information[1].trim()));
        else
            endTime = null;

        List<Duration> periods = new ArrayList<>();
        if (information.length > 2) {
            periods = Arrays.stream(information[2].split(" "))
                    .map(s -> Duration.ofSeconds(Long.parseLong(s))).collect(Collectors.toList());
        }
        List<GregorianCalendar> ignoreList = new ArrayList<>();
        if (information.length > 3) {
            String[] ignoreMillisStr = information[3].split(" ");
            List<GregorianCalendar> ignored = new ArrayList<>();
            for (String s : ignoreMillisStr) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTimeInMillis(Long.parseLong(s));
                ignored.add(gc);
            }
            ignoreList = ignored;
        }

        return new CalendarGenerator(startTime, periods, endTime, ignoreList);

    }

    /**
     * Load a Calendar from its files
     *
     * @param calendarName Name of Calendar
     * @return Loaded Calendar
     */
    public Calendar loadCalendar(String calendarName) {
        DataSaver calendarDataSaver = new DataSaver(basePath + calendarName);
        ArrayList<Memo> memos = new ArrayList<>();
        ArrayList<Tag> tags;
        ArrayList<EventCollection> eventCollections = new ArrayList<>();

        loadEvents(calendarName, eventCollections);

        memos = loadMemos(calendarName, memos);

        tags = loadTags(calendarName);

        loadSeries(calendarName, eventCollections);

        return new Calendar(calendarName, eventCollections, memos, tags, calendarDataSaver);
    }

    /**
     * load Series from file
     *
     * @param calendarName     calendar name which these series belongs to
     * @param eventCollections the eventCollection to save to
     */
    private void loadSeries(String calendarName, ArrayList<EventCollection> eventCollections) {
        String[] filenames = getFileNamesInDirectory(calendarName + "/series/");
        if (filenames != null) {
            for (String seriesName :
                    filenames) {
                try {
                    Series newSeries = new Series(seriesName, null, new CalendarGenerator(null, null, null));

                    newSeries.setRepeatingEvents(loadRepeatingEvents(loadScannerFromFile(calendarName + "/series/" + seriesName + "/SubSeries.txt")));

                    newSeries.setEvents(loadEventsFromFile(calendarName + "/series/" + seriesName + "/Manual Events/"));
                    newSeries.setPostponedEvents(loadEventsFromFile(calendarName + "/series/" + seriesName + "/Manual Events/postponed/"));

                    eventCollections.add(newSeries);
                } catch (IOException | InvalidDateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private ArrayList<Tag> loadTags(String calendarName) {
        ArrayList<Tag> tags = new ArrayList<>();
        try {
            Scanner scanner = loadScannerFromFile(calendarName + "/tags.txt");
            while (scanner.hasNext()) {
                String tagData = scanner.nextLine();
                String[] parts = tagData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>();
                if (parts.length == 2) {
                    idStrings = new ArrayList<>(Arrays.asList(parts[1].split("[|]+")));
                }
                tags.add(new Tag(parts[0], idStrings));
            }
        } catch (FileNotFoundException ignored) {

        }
        return tags;
    }

    /**
     * Load AlertCollections from files.
     *
     * @return Loaded AlertCollections.
     */
    public List<AlertCollection> loadAlertCollections() {
        ArrayList<AlertCollection> alertCollections = new ArrayList<>();
        //Load existing alert collection series
        File[] files = getFilesInDirectory("/alerts/");
        if (files != null) {
            for (File file :
                    files) {
                String name = file.getName();
                name = name.replaceAll(".txt", "");
                AlertCollection alertCollection = loadAlertCollection(name);
                alertCollections.add(alertCollection);
            }
        }
        return alertCollections;
    }

    private ArrayList<Memo> loadMemos(String calendarName, ArrayList<Memo> memos) {
        try {
            memos = new ArrayList<>();
            Scanner scanner = loadScannerFromFile(calendarName + "/memos.txt");
            while (scanner.hasNext()) {
                String memoData = scanner.nextLine();
                String[] parts = memoData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>();
                if (parts.length == 3) {
                    idStrings = new ArrayList<>(Arrays.asList(parts[2].split("[|]+")));
                }
                memos.add(new Memo(parts[1], parts[0], idStrings));
            }
        } catch (FileNotFoundException ignored) {

        }
        return memos;
    }

    private void loadEvents(String calendarName, ArrayList<EventCollection> eventCollections) {
        EventCollection manualEvents;
        manualEvents = new EventCollection(loadEventsFromFile(calendarName + "/events/"));
        manualEvents.setPostponedEvents(loadEventsFromFile(calendarName + "/events/postponed/"));
        eventCollections.add(manualEvents);
    }

    /**
     * Save everything in the calendar
     *
     * @param calendar Calendar to be saved
     */
    public void saveCalendar(Calendar calendar) {
        saveTags(calendar.getTagManager());
        saveMemos(calendar.getMemoManager());
        saveEvents(calendar.getEventManager());
    }

    /**
     * Save the tags to files
     *
     * @param tagManager TagManager to save
     */
    public void saveTags(TagManager tagManager) {
        StringBuilder tagsData = new StringBuilder();
        for (Tag tag :
                tagManager.getTags()) {
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

    /**
     * Save Memos to files
     *
     * @param memoManager MemoManager to save
     */
    public void saveMemos(MemoManager memoManager) {
        StringBuilder memoData = new StringBuilder();
        for (Memo memo :
                memoManager.getMemos()) {
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
    }

    /**
     * Save EventCollections to file
     *
     * @param eventManager EventManager to save
     */
    public void saveEvents(EventManager eventManager) {
        try {
            deleteDirectory("events/");
        } catch (IOException e) {
        }
        saveEventsToFile("events/", eventManager.getManualEventCollection().getEvents());
        saveEventsToFile("events/postponed/", eventManager.getManualEventCollection().getPostponedEvents());

        saveSeries(eventManager);

    }

    /**
     * Save a series to file
     *
     * @param eventManager Event Manager
     */
    public void saveSeries(EventManager eventManager) {
        for (Series series : eventManager.getSeries()) {
            try {
                deleteDirectory("series/" + series.getName());
            } catch (IOException e) {
                System.out.println("No such file");
            }
            saveEventsToFile("series/" + series.getName() + "/Manual Events/", series.getManualEvents());
            saveEventsToFile("series/" + series.getName() + "/Manual Events/postponed/", series.getPostponedEvents());
            try {
                saveRepeatingEvents(series, series.getRepeatingEvents());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveEventsToFile(String path, List<Event> events) {
        for (Event e : events) {
            try {
                saveToFile(path + e.getId() + ".txt", e.getString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<Event> loadEventsFromFile(String path) {
        List<Event> loadedEvents = new ArrayList<>();
        File[] data = getFilesInDirectory(path);
        for (File f : data) {
            String id = f.getName();
            id = id.replaceAll(".txt", "");
            if (id.equals("postponed")) {
                continue;
            }
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

    private void saveRepeatingEvents(Series series, List<RepeatingEvent> events) throws IOException {
        StringBuilder ret = new StringBuilder();
        for (RepeatingEvent event : events) {
            ret.append(event.getString()).append("\n");
        }
        saveToFile("series/" + series.getName() + "/SubSeries.txt", ret.toString());
    }

    private List<RepeatingEvent> loadRepeatingEvents(Scanner events) throws InvalidDateException {
        List<RepeatingEvent> ret = new ArrayList<>();
        while (events.hasNext()) {
            String eventData = events.nextLine();
            String[] parts = eventData.split("§");
            Event baseEvent = stringsToEvent(parts);
            CalendarGenerator CG = loadCalendarGenerator(parts[4].replaceAll("\\|", "\n"));
            ret.add(new RepeatingEvent(baseEvent, CG));
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

    /**
     * Save an AlertCollection to file.
     *
     * @param ac AlertCollection to save
     */
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
     * Load an AlertCollection from a file.
     *
     * @param eventId The ID of the event for which the Alerts are being loaded
     */
    public AlertCollection loadAlertCollection(String eventId) {
        AlertCollection ac = new AlertCollection(eventId, this);

        List<String> strings = null;
        try {
            strings = loadStringsFromFile("/alerts/" + eventId + ".txt");
        } catch (NoSuchFileException e) {
            System.out.println("Alert file does not exist for " + eventId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ac.setEventTime(IDManager.parseEventId(eventId));

        if (strings == null)
            return ac;

        if (strings.size() > 2) {
            String[] manTimes = strings.get(2).trim().split(" ");
            for (String timeStr : manTimes) {
                ac.getManAlerts().add(new Alert(eventId, timeStr));
            }
        }

        StringBuilder cgStr = new StringBuilder();
        for (int i = 3; i < strings.size(); i++) {
            cgStr.append(strings.get(i)).append("\n");
        }
        if (!cgStr.toString().equals("")) {
            cgStr.deleteCharAt(cgStr.length() - 1);
            ac.setCalGen(loadCalendarGenerator(cgStr.toString()));
        }

        return ac;
    }
}