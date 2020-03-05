import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class DataSaver {

    private String basePath;

    public DataSaver(String basePath){
        this.basePath = "./" + basePath + "/";
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
        try (PrintWriter out = new PrintWriter(path)) {
            for (String line : contents)
                out.println(line);
        } catch (FileNotFoundException e) {
            // file and/or directory doesn't exist
            File newFile = new File(path);
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
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(contents);
        } catch (FileNotFoundException e) {
            // file and/or directory doesnt exist
            File newFile = new File(path);
            newFile.getParentFile().mkdirs(); // create whole directory
                newFile.createNewFile();
                saveToFile(path, contents);
        }
    }

    public File[] getFilesInDirectory (String path) {
        return new File(path).listFiles();
    }


}
