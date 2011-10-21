/*
 * TODO make nice source header
 */
package assignment1a;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This application performs a random search to find a good solution to the
 * which minimal configuration of disabled routers will disable all path on the
 * network.
 * 
 * Command line options are:
 *     --config [file] (specifies the configuration file to use)
 * 
 * @author William P. Riley-Land
 */
public class Assignment1a {

    private static String configPath = "./default.cfg";
    private static String inputPath;
    private static String logPath;
    private static String solutionPath;
    private static Integer numIterations;
    private static Long rngSeed;
    private static double penaltyCoefficient;
    private static int routerCount;
    private static int pathCount;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        handleOptions(args); // check for --config option
        readConfigFile();

        Path[] network = readInputFile();
        FileWriter log = openLog();

        Solution best = null;
        Random rng = new Random(rngSeed);

        int i;
        for (i = 0; i < numIterations; i++) {
            Solution possible = new Solution(routerCount, network, penaltyCoefficient);
            possible.randomize(rng);

            if (best == null || best.fitness() < possible.fitness()) {
                best = possible;
                writeToLog(log, i + 1, best.fitness());

            }
        }

        closeLog(log);
        writeSolutionFile(best, pathCount);
    }

    private static void handleOptions(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<File> config = parser.accepts("config").withOptionalArg().ofType(File.class);
        OptionSet options = parser.parse(args);

        if (options.has(config)) {
            File configFile = options.valueOf(config);

            if (configFile == null || !configFile.exists()) {
                System.out.println("ERROR: invalid config path specified.");
                System.exit(1);
            }

            try {
                configPath = configFile.getCanonicalPath();
            } catch (IOException e) {
                System.out.println("ERROR: could not convert relative path to canonical path.");
                System.exit(1);
            }
        }
    }

    private static void readConfigFile() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = null;
        
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(configPath);
        } catch (Exception e) {
            System.out.println("ERROR: Counldn't read config file " + configPath);
            System.exit(1);
        }

        NodeList params = doc.getElementsByTagName("config");

        // This loops through all <config> tags, checks there IDs, and sets options accordingly
        for (int i = 0; i < params.getLength(); i++) {
            Node param = params.item(i);
            NamedNodeMap attributes = param.getAttributes();

            String id = attributes.getNamedItem("id").getNodeValue();
            if (id.compareTo("input-file") == 0) {
                inputPath = attributes.getNamedItem("path").getNodeValue();
            } else if (id.compareTo("log-file") == 0) {
                logPath = attributes.getNamedItem("path").getNodeValue();
            } else if (id.compareTo("solution-file") == 0) {
                solutionPath = attributes.getNamedItem("path").getNodeValue();
            } else if (id.compareTo("iterations") == 0) {
                numIterations = Integer.parseInt(attributes.getNamedItem("count").getNodeValue());
            } else if (id.compareTo("penalty") == 0) {
                penaltyCoefficient = Double.parseDouble(attributes.getNamedItem("coefficient").getNodeValue());
            } else if (id.compareTo("rng") == 0) {
                String seedValue = attributes.getNamedItem("seed").getNodeValue();
                if ("TIME".compareTo(seedValue) == 0) {
                    rngSeed = System.currentTimeMillis();
                } else {
                    rngSeed = Long.parseLong(seedValue);
                }
            } else {
                System.out.println("ERROR: Unknown configuration option specified.");
                System.exit(1);
            }
        }
    }

    private static Path[] readInputFile() {
        File file = new File(inputPath);

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: input file not found");
            System.exit(1);
        }

        scanner.nextLine(); // skip "Number of hosts/paths:"
        pathCount = Integer.parseInt(scanner.nextLine());
        scanner.nextLine(); // skip "Number of routers:"
        routerCount = Integer.parseInt(scanner.nextLine());
        scanner.nextLine(); // skip "Paths:"

        Path[] network = new Path[pathCount];

        int networkIndex = 0;
        while (scanner.hasNextLine()) {
            String[] elements = scanner.nextLine().split("[ ]"); // split the line into IDs separated by spaces
            int pathId = Integer.parseInt(elements[0]);
            int[] routerIds = new int[elements.length - 1];

            for (int i = 1; i < elements.length; i++) { // skip the path ID and convert the router IDs to ints
                routerIds[i - 1] = Integer.parseInt(elements[i]) - pathCount; // start router IDs from 0 internally
            }

            Path p = new Path(pathId, routerIds);
            network[networkIndex] = p;

            networkIndex++;
        }

        scanner.close();
        return network;

//        Path[] network = new Path[8];
//        pathCount = 8;
//        routerCount = 7;
//
//        network[0] = new Path(0, 0, 5);
//        network[1] = new Path(1, 0, 5);
//        network[2] = new Path(2, 0, 6);
//        network[3] = new Path(3, 0, 6);
//        network[4] = new Path(4, 1, 5);
//        network[5] = new Path(5, 2, 6);
//        network[6] = new Path(6, 3);
//        network[7] = new Path(7, 4);
//
//        return network;
    }

    private static FileWriter openLog() {
        File file = new File(logPath);

        FileWriter out = null;
        try {
            file.createNewFile();
            out = new FileWriter(file);
        } catch (IOException e) {
            System.out.println("ERROR: couldn't open log.");
            System.exit(1);
        }

        return out;
    }

    private static void closeLog(FileWriter log) {
        try {
            log.flush();
            log.close();
        } catch (IOException e) {
            System.out.println("ERROR: Couldn't close log file.");
            System.exit(1);
        }
    }

    private static void writeToLog(FileWriter log, int evals, double fitness) {
        String row = evals + "\t" + fitness + "\n";
        try {
            log.write(row);
        } catch (IOException e) {
            System.out.println("ERROR: failed to write to log.");
            System.exit(1);
        }
    }

    private static void writeSolutionFile(Solution solution, int routerOffset) {
        File file = new File(solutionPath);
        String solutionString = solution.toSolutionString(routerOffset);

        try {
            file.createNewFile();
            FileWriter out = new FileWriter(file);
            out.write(solutionString);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR: failed to write solution file.");
            System.exit(1);
        }
    }
}
