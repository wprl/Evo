package assignment2c;

import assignment2c.task.selection.FitnessProportionalSelection;
import assignment2c.task.selection.KTournament;
import assignment2c.task.selection.OverSelection;
import assignment2c.task.selection.SelectTree;
import assignment2c.task.selection.Truncate;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    public enum SurvivalStrategy {

        COMMA,
        PLUS
    }
    private String logPath;
    private String solutionPath;
    private long rngSeed;
    private int numberOfEvaluationsPerRun;
    private int stopIfFitnessStaticFor;
    private int historyLength;
    private int maxTreeDepth;
    private int numberOfGames;
    private int numberOfRuns;
    private int numberOfChildren;
    private int populationSize;
    private int kInTournament;
    private int coevolutionarySampleSize;
    private double percentInUpperTierForOverselection;
    private double parsimonyPressure;
    private double mutationRate;
    private boolean useHallOfFame;
    private SelectTree parentSelector;
    private SelectTree survivalSelector;
    private SurvivalStrategy survivalStrategy;
    private Random rng;
    private ExecutorService exec;
    private static Config singleton;

    /**
     * Insantiate a new configuration object and read in values from given path
     * @param path path to config file
     */
    public Config(String path) {
        this.read(path);
        exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }

    /**
     * Reads config values from given path
     * @param path path to config file
     */
    private void read(String path) {
        singleton = this;

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

        // this loops through all <config> tags, checks there IDs, and sets options accordingly
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
            } else if (id.compareTo("population") == 0) {
                populationSize = Integer.parseInt(attributes.getNamedItem("size").getNodeValue());
                numberOfChildren = Integer.parseInt(attributes.getNamedItem("offspring").getNodeValue());
                System.out.println("Population size: " + populationSize);
                System.out.println("Number of children: " + numberOfChildren);

                String strategy = attributes.getNamedItem("survival-strategy").getNodeValue();
                if (strategy.compareTo("comma") == 0) {
                    survivalStrategy = SurvivalStrategy.COMMA;
                } else if (strategy.compareTo("plus") == 0) {
                    survivalStrategy = SurvivalStrategy.PLUS;
                } else {
                    System.out.println("ERROR: Unknown survival strategy: " + strategy);
                    System.exit(1);
                }
            } else if (id.compareTo("program-tree") == 0) {
                maxTreeDepth = Integer.parseInt(attributes.getNamedItem("max-depth").getNodeValue());
                System.out.println("Max tree depth: " + maxTreeDepth);
            } else if (id.compareTo("fitness") == 0) {
                historyLength = Integer.parseInt(attributes.getNamedItem("history-length").getNodeValue());
                numberOfGames = Integer.parseInt(attributes.getNamedItem("number-of-games").getNodeValue());
                parsimonyPressure = Double.parseDouble(attributes.getNamedItem("parsimony-pressure").getNodeValue());
                coevolutionarySampleSize = Integer.parseInt(attributes.getNamedItem("coevolutionary-sample-size").getNodeValue());
                System.out.println("History length: " + historyLength);
                System.out.println("Number of games: " + numberOfGames);
                System.out.println("Parsimony pressure: " + parsimonyPressure);
                System.out.println("Co-evolutionary sample size: " + coevolutionarySampleSize);
            } else if (id.compareTo("mutation") == 0) {
                mutationRate = Double.parseDouble(attributes.getNamedItem("rate").getNodeValue());
                System.out.println("Mutation rate: " + mutationRate);
            } else if (id.compareTo("termination") == 0) {
                numberOfRuns = Integer.parseInt(attributes.getNamedItem("runs").getNodeValue());
                numberOfEvaluationsPerRun = Integer.parseInt(attributes.getNamedItem("max-evaluations").getNodeValue());
                stopIfFitnessStaticFor = Integer.parseInt(attributes.getNamedItem("fitness-static").getNodeValue());
                System.out.println("Number of runs: " + numberOfRuns);
                System.out.println("Number of generations: " + numberOfEvaluationsPerRun);
                System.out.println("Stop if fitness static for: " + stopIfFitnessStaticFor);
            } else if (id.compareTo("parent-selection") == 0) {
                String method = attributes.getNamedItem("method").getNodeValue();
                if (method.compareTo("fitness-proportional") == 0) {
                    parentSelector = new FitnessProportionalSelection();
                } else if (method.compareTo("over-selection") == 0) {
                    parentSelector = new OverSelection();
                    percentInUpperTierForOverselection = Double.parseDouble(attributes.getNamedItem("x").getNodeValue());
                } else {
                    System.out.println("ERROR: Unknown parent selection method: " + method);
                    System.exit(1);
                }
            } else if (id.compareTo("cycle-prevention") == 0) {
                useHallOfFame = Boolean.parseBoolean(attributes.getNamedItem("hall-of-fame").getNodeValue());
            } else if (id.compareTo("survival-selection") == 0) {
                String method = attributes.getNamedItem("method").getNodeValue();
                if (method.compareTo("k-tournament") == 0) {
                    survivalSelector = new KTournament();
                    kInTournament = Integer.parseInt(attributes.getNamedItem("k").getNodeValue());
                } else if (method.compareTo("truncation") == 0) {
                    survivalSelector = new Truncate();
                } else {
                    System.out.println("ERROR: Unknown survival selection method: " + method);
                    System.exit(1);
                }
            } else if (id.compareTo("rng") == 0) {
                String seedValue = attributes.getNamedItem("seed").getNodeValue();
                if ("TIME".compareTo(seedValue) == 0) {
                    rngSeed = System.currentTimeMillis();
                    System.out.println("RNG seeded with TIME: " + System.currentTimeMillis());
                } else {
                    rngSeed = Long.parseLong(seedValue);
                    System.out.println("RNG seeded with: " + seedValue);
                }
                this.rng = new Random(rngSeed);
            } else {
                System.out.println("ERROR: Unknown configuration option specified: " + id);
                System.exit(1);
            }
        }

        // sanity checks
        if (numberOfGames < (3 * historyLength)) {
            System.out.println("l > 3k");
            System.exit(1);
        }

        if (coevolutionarySampleSize < 1 || coevolutionarySampleSize > populationSize + numberOfChildren - 1) {
            System.out.println("(1 < co-evolutionary sample size < lambda + mu) NOT SATISIFED");
            System.exit(1);
        }
    }

    public String getLogPath() {
        return logPath;
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

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public int getNumberOfEvaluationsPerRun() {
        return numberOfEvaluationsPerRun;
    }

    public SelectTree getParentSelector() {
        return parentSelector;
    }

    public SelectTree getSurvivalSelector() {
        return survivalSelector;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getKInTournament() {
        return kInTournament;
    }

    public double getPercentInUpperTierForOverselection() {
        return percentInUpperTierForOverselection;
    }

    public double getParsimonyPressure() {
        return parsimonyPressure;
    }

    public int getStopIfFitnessStaticFor() {
        return stopIfFitnessStaticFor;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public ExecutorService getExec() {
        return exec;
    }

    public int getkInTournament() {
        return kInTournament;
    }

    public Random getRng() {
        return rng;
    }

    public static Config getSingleton() {
        return singleton;
    }

    public int getCoevolutionarySampleSize() {
        return coevolutionarySampleSize;
    }

    public SurvivalStrategy getSurvivalStrategy() {
        return survivalStrategy;
    }

    public boolean getUseHallOfFame() {
        return useHallOfFame;
    }
}
