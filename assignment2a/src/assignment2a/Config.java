package assignment2a;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is a class that reads and stores all the values from the configuration file
 * @author William Riley-Land
 */
public class Config {

    private String logPath;
    private String solutionPath;
    private int numGenerations;
    private long rngSeed;
    private int historyLength;
    private int maxTreeDepth;
    private int numberOfGames;

    /**
     * Insantiate a new configuration object and read in values from given path
     * @param path path to config file
     */
    public Config(String path) {
        this.read(path);
    }

    /**
     * Reads config values from given path
     * @param path path to config file
     */
    private void read(String path) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(path);
        } catch (Exception e) {
            System.out.println("ERROR: Counldn't read config file " + path);
            System.exit(1);
        }

        NodeList params = doc.getElementsByTagName("config");

        // This loops through all <config> tags, checks there IDs, and sets options accordingly
        for (int i = 0; i < params.getLength(); i++) {
            Node param = params.item(i);
            NamedNodeMap attributes = param.getAttributes();

            String id = attributes.getNamedItem("id").getNodeValue();
            if (id.compareTo("log-file") == 0) {
                logPath = attributes.getNamedItem("path").getNodeValue();
                System.out.println("Log path: " + logPath);
            } else if (id.compareTo("solution-file") == 0) {
                solutionPath = attributes.getNamedItem("path").getNodeValue();
                System.out.println("Solution path: " + solutionPath);
            } else if (id.compareTo("program-tree") == 0) {
                maxTreeDepth = Integer.parseInt(attributes.getNamedItem("max-depth").getNodeValue());
                System.out.println("Max program tree depth: " + maxTreeDepth);
            } else if (id.compareTo("fitness") == 0) {
                historyLength = Integer.parseInt(attributes.getNamedItem("history-length").getNodeValue());
                numberOfGames = Integer.parseInt(attributes.getNamedItem("number-of-games").getNodeValue());
                System.out.println("History length: " + historyLength);
                System.out.println("Number of games: " + numberOfGames);
            } else if (id.compareTo("termination") == 0) {
                numGenerations = Integer.parseInt(attributes.getNamedItem("max-iterations").getNodeValue());
                System.out.println("Number of generations: " + numGenerations);
            } else if (id.compareTo("rng") == 0) {
                String seedValue = attributes.getNamedItem("seed").getNodeValue();
                if ("TIME".compareTo(seedValue) == 0) {
                    rngSeed = System.currentTimeMillis();
                    System.out.println("RNG seeded with TIME: " + System.currentTimeMillis());
                } else {
                    rngSeed = Long.parseLong(seedValue);
                    System.out.println("RNG seeded with: " + seedValue);
                }
            } else {
                System.out.println("ERROR: Unknown configuration option specified: " + id);
                System.exit(1);
            }
        }
    }

    public String getLogPath() {
        return logPath;
    }

    public int getNumGenerations() {
        return numGenerations;
    }

    public long getRngSeed() {
        return rngSeed;
    }

    public String getSolutionPath() {
        return solutionPath;
    }

    public int getHistoryLength() {
        return historyLength;
    }

    public int getMaxTreeDepth() {
        return maxTreeDepth;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }
}
