package assignment2a;

import assignment2a.program.AndNode;
import assignment2a.program.Tree;
import assignment2a.task.EvaluateFitness;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * 
 * Command line options are:
 *     --config [file] (specifies the configuration file to use)
 * 
 * @author William P. Riley-Land
 */
public class Assignment2a {

    private static String configPath = "./default.cfg"; // default.  override with command line --config
    private static Config config;
    private static Random rng;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        handleOptions(args); // check for --config option

        config = new Config(configPath);
        rng = new Random(config.getRngSeed());

        // generate all solutions by passing CreateRandTrees to exec pool
        // .. wait till done ..
        // evaluate all fitnesses using EvaluateFitness -> exec
        // .. wait ..
        // find best!
        // .. wait ..
        // !- cha-ching -!

        List<Tree<Boolean>> forest = null;

        try {
            forest = Tree.createRandomForest(config, rng);
        } catch (Exception ex) {
            System.out.println("Thread pool failed to finish when creating forest.");
            System.exit(1);
        }

        Map<Tree<Boolean>, Double> fitness = null;
        try {
            fitness = calculateFitness(forest);
        } catch (Exception ex) {
            System.out.println("Thread pool failed to finish when evaluating fitness.");
            System.exit(1);
        }
        Tree<Boolean> best = findBestAndLog(fitness);

        Solution s = new Solution(config.getSolutionPath());
        s.write(best);
        s.close();

        // -! cha-ching !-
    }

    private static ExecutorService createThreadPool() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
    }

    

    private static Map<Tree<Boolean>, Double> calculateFitness(List<Tree<Boolean>> forest) throws InterruptedException, ExecutionException {
        ExecutorService exec = createThreadPool();

        // evaluate fitness of each tree in forest
        List<EvaluateFitness> workers = new ArrayList<EvaluateFitness>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            List<Outcome> history = Outcome.createRandomHistory(rng, config.getHistoryLength());
            EvaluateFitness worker = new EvaluateFitness(forest.get(g), history, config.getNumberOfGames());
            workers.add(worker);
        }

        List<Future<Double>> futures = exec.invokeAll(workers);

        // harvest fitness values
        Map<Tree<Boolean>, Double> fitness = new HashMap<Tree<Boolean>, Double>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            Double score = futures.get(g).get();
            fitness.put(forest.get(g), score);
        }

        exec.shutdown();

        return fitness;
    }

    private static Tree<Boolean> findBestAndLog(Map<Tree<Boolean>, Double> fitness) {
        Log log = new Log(config.getLogPath());

        log.write("Result Log\n\n");

        // log each tree
        Tree<Boolean> best = null;
        double bestFitnessValue = 0.0;
        int g = 0;

        for (Tree<Boolean> solution : fitness.keySet()) {
            double fitnessValue = fitness.get(solution);
            if (bestFitnessValue < fitnessValue) {
                bestFitnessValue = fitnessValue;
                best = solution;
                log.write(g + "\t" + bestFitnessValue + "\n\n");
            }

            g++;
        }

        log.close();

        return best;
    }

    /**
     * Private method used to handle command line arguments
     * @param args command line arguments
     */
    private static void handleOptions(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<File> configOption = parser.accepts("config").withOptionalArg().ofType(File.class);
        OptionSet options = parser.parse(args);

        if (options.has(configOption)) {
            File configFile = options.valueOf(configOption);

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
}
