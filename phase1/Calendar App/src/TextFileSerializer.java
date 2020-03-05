import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class TextFileSerializer {

    /**
     *
     * @param path
     * @return loaded text file stream
     */
    public Scanner loadScannerFromFile (String path) {
        try {
            return new Scanner (new File (path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param path
     * @return list of strings from file
     */
    public List<String> loadStringsFromFile (String path) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     *
     * @param path
     * @return entire text file as a string
     */
    public String loadStringFromFile (String path) {
        String data = null;
        try {
            data = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * Save to file
     * @param path
     * @param contents
     */
    public void saveToFile (String path, List<String> contents) {
        try (PrintWriter out = new PrintWriter(path)) {
            for (String line : contents)
                out.println(line);
        } catch (FileNotFoundException e) {
            // file and/or directory doesnt exist
            File newFile = new File(path);
            newFile.getParentFile().mkdirs(); // create whole directory
            try {
                newFile.createNewFile();
                saveToFile(path, contents);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        }
    }

    /**
     * Save to file
     * @param path
     * @param contents
     */
    public void saveToFile (String path, String contents) {
        try (FileWriter fileWriter = new FileWriter(path)) {
            fileWriter.write(contents);
        } catch (FileNotFoundException e) {
            // file and/or directory doesnt exist
            File newFile = new File(path);
            newFile.getParentFile().mkdirs(); // create whole directory
            try {
                newFile.createNewFile();
                saveToFile(path, contents);
            } catch (IOException ee) {
                ee.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File[] getFilesInDirectory (String path) {
        return new File(path).listFiles();
    }


}
