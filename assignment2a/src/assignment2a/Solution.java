package assignment2a;

import assignment2a.program.Tree;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class used to manage log file
 * @author William Riley-Land
 */
public class Solution {

    private FileWriter out;

    /**
     * Instantiate a new log and open the associated file
     * @param path
     */
    public Solution(String path) {
        this.open(path);
    }

    /**
     * Open a file for output (will overwrite existing files)
     * @param path
     */
    private void open(String path) {
        File file = new File(path);

        try {
            file.createNewFile();
            out = new FileWriter(file);
        } catch (IOException e) {
            System.out.println("ERROR: couldn't open solution file.");
            System.exit(1);
        }
    }

    /**
     * Close the log file
     */
    public void close() {
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR: Couldn't close solution file.");
            System.exit(1);
        }
    }

    /**
     * Write a string to the log file
     * @param s the string to write
     */
    public void write(Tree<Boolean> tree) {
        try {
            out.write(tree.toString());
            System.out.print(tree.toString());
        } catch (IOException e) {
            System.out.println("ERROR: failed to write to log.");
            System.exit(1);
        }
    }
}
