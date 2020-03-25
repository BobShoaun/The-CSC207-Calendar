package user;

import entities.Alert;
import entities.AlertCollection;
import entities.EventCollection;
import exceptions.InvalidDateException;
import mt.Memo;
import mt.Tag;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Responsible for serializing data into the file system, and deserializing data to be loaded into memory
 * @auther Ng Bob Shoaun
 */
public class DataSaver {

    private String basePath;

    /**
     * Initializes DataSaver
     * @param basePath a base path relative to all path's passed into this DataSaver
     */
    public DataSaver (String basePath) {
        this.basePath = basePath + (!basePath.equals("") ? "/" : "");
    }

    /**
     *
     * @param path The path relative to the base directory of the user data
     * @return loaded text file stream
     */
    public Scanner loadScannerFromFile (String path) throws FileNotFoundException {
        return new Scanner (new File (basePath + path));
    }

    /**
     *
     * @param path The path relative to the base directory of the user data
     * @return list of strings from file
     */
    public List<String> loadStringsFromFile (String path) throws IOException {
        return Files.readAllLines(Paths.get(basePath + path), StandardCharsets.UTF_8);
    }

    /**
     *
     * @param path The path relative to the base directory of the user data
     * @return entire text file as a string
     */
    public String loadStringFromFile (String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(basePath + path)));
    }

    /**
     * Save to file
     * @param path The path relative to the base directory of the user data
     * @param contents Data to save
     */
    public void saveToFile (String path, List<String> contents) throws IOException {
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
     * @param path The path relative to the base directory of the user data
     * @param contents Data to save
     */
    public void saveToFile (String path, String contents) throws IOException {
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
     *
     * @param path The path relative to the base directory of the user data
     * @return the list of files in the given path
     */
    public File[] getFilesInDirectory (String path) {
        return new File(basePath + path).listFiles();
    }

    public void makeDirectory (String path) {
        new File(basePath + path).mkdirs();
    }

    public void deleteDirectory (String path) throws IOException {
        Files.walk(Paths.get(basePath + path))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }


    public Calendar loadCalendar(String calendarName){
        ArrayList<Memo> memos = new ArrayList<>();
        ArrayList<Tag> tags = new ArrayList<>();
        ArrayList<EventCollection> eventCollections = new ArrayList<>();
        ArrayList<AlertCollection> alertCollections = new ArrayList<>();
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
            while (scanner.hasNext()){
                String tagData = scanner.nextLine();
                String[] parts = tagData.split("[§]+");
                //Split ids
                List<String> idStrings = new ArrayList<>(Arrays.asList(parts[1].split("[|]+")));
                tags.add(new Tag(parts[0], idStrings));
            }
        } catch (FileNotFoundException ignored) {

        }
        //Load existing event collection series
        File[] files = getFilesInDirectory("/events");
        if(files != null){
            for (File file :
                    files) {
                String name = file.getName();
                name = name.replaceAll(".txt", "");
                //try {
                    //eventCollections.add(new EventCollection(name, this));
                    throw new Error(); //Tell Jasper to fix this once datasaver loads event collection and series
                //} catch (InvalidDateException e) {
                //    System.out.println("Failed to load events: " + name);
                //}
            }
        }
        //Load existing alert collection series
        files = getFilesInDirectory("/alerts/");
        if(files != null){
            for (File file :
                    files) {
                String name = file.getName();
                name = name.replaceAll(".txt", "");
                alertCollections.add(new AlertCollection(name, this));
            }
        }
        return new Calendar(calendarName, eventCollections, alertCollections, memos, tags, this);
    }


    public void SaveCalendar(Calendar calendar){
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

}