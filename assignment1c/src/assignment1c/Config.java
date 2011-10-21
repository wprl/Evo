package assignment1c;

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

    private String inputPath;
    private String logPath;
    private String solutionPath;
    private int numIterations;
    private long rngSeed;
    private double penaltyCoefficient;
    private int routerCount;
    private int pathCount;
    private int stopWhenNoChangeFor;
    private int populationSize;
    private int offspringSize;
    private String parentSelectionMethod;
    private int kParentTournaments;
    private String recombinationMethod;
    private int nCrossoverPoints;
    private double mutationRate;
    private String survivalSelectionMethod;
    private int kSurvivalTournaments;
    private int numRuns;

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
            if (id.compareTo("input-file") == 0) {
                inputPath = attributes.getNamedItem("path").getNodeValue();
            } else if (id.compareTo("log-file") == 0) {
                logPath = attributes.getNamedItem("path").getNodeValue();
            } else if (id.compareTo("solution-file") == 0) {
                solutionPath = attributes.getNamedItem("path").getNodeValue();
            } else if (id.compareTo("runs") == 0) {
                numRuns = Integer.parseInt(attributes.getNamedItem("count").getNodeValue());
            } else if (id.compareTo("termination") == 0) {
                numIterations = Integer.parseInt(attributes.getNamedItem("max-iterations").getNodeValue());
                stopWhenNoChangeFor = Integer.parseInt(attributes.getNamedItem("no-change").getNodeValue());
            } else if (id.compareTo("penalty") == 0) {
                penaltyCoefficient = Double.parseDouble(attributes.getNamedItem("coefficient").getNodeValue());
            } else if (id.compareTo("population") == 0) {
                populationSize = Integer.parseInt(attributes.getNamedItem("size").getNodeValue());
                offspringSize = Integer.parseInt(attributes.getNamedItem("offspring").getNodeValue());
            } else if (id.compareTo("parent-selection") == 0) {
                parentSelectionMethod = attributes.getNamedItem("method").getNodeValue();
                if (parentSelectionMethod.compareTo("k-tournament-with-replace") == 0) {
                    kParentTournaments = Integer.parseInt(attributes.getNamedItem("k").getNodeValue());
                }
            } else if (id.compareTo("recombination") == 0) {
                recombinationMethod = attributes.getNamedItem("method").getNodeValue();
                if (recombinationMethod.compareTo("n-point-crossover") == 0) {
                    nCrossoverPoints = Integer.parseInt(attributes.getNamedItem("n-points").getNodeValue());
                }
            } else if (id.compareTo("mutation") == 0) {
                mutationRate = Double.parseDouble(attributes.getNamedItem("rate").getNodeValue());
            } else if (id.compareTo("survival-selection") == 0) {
                survivalSelectionMethod = attributes.getNamedItem("method").getNodeValue();
                if (survivalSelectionMethod.compareTo("k-tournament-no-replace") == 0) {
                    kSurvivalTournaments = Integer.parseInt(attributes.getNamedItem("k").getNodeValue());
                }
            } else if (id.compareTo("rng") == 0) {
                String seedValue = attributes.getNamedItem("seed").getNodeValue();
                if ("TIME".compareTo(seedValue) == 0) {
                    rngSeed = System.currentTimeMillis();
                } else {
                    rngSeed = Long.parseLong(seedValue);
                }
            } else {
                System.out.println("ERROR: Unknown configuration option specified: " + id);
                System.exit(1);
            }
        }
    }

    public String getInputPath() {
        return inputPath;
    }

    public int getkParentTournaments() {
        return kParentTournaments;
    }

    public int getkSurvivalTournaments() {
        return kSurvivalTournaments;
    }

    public String getLogPath() {
        return logPath;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public int getnCrossoverPoints() {
        return nCrossoverPoints;
    }

    public int getNumIterations() {
        return numIterations;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public int getOffspringSize() {
        return offspringSize;
    }

    public String getParentSelectionMethod() {
        return parentSelectionMethod;
    }

    public int getPathCount() {
        return pathCount;
    }

    public double getPenaltyCoefficient() {
        return penaltyCoefficient;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public String getRecombinationMethod() {
        return recombinationMethod;
    }

    public long getRngSeed() {
        return rngSeed;
    }

    public int getRouterCount() {
        return routerCount;
    }

    public String getSolutionPath() {
        return solutionPath;
    }

    public int getStopWhenNoChangeFor() {
        return stopWhenNoChangeFor;
    }

    public String getSurvivalSelectionMethod() {
        return survivalSelectionMethod;
    }
}
