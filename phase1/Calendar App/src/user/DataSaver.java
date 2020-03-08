package user;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

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
    public DataSaver(String basePath) {
        this.basePath = "./users/" + basePath + (!basePath.equals("") ? "/" : "");
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
        return new String(Files.readAllBytes(Paths.get(path)));
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


}
