package assignment2a;

import assignment2a.program.Tree;
import assignment2a.task.CreateRandomTree;
import assignment2a.task.EvaluateFitness;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

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
public class Assignment2a {

    private static String configPath = "./default.cfg"; // default.  override with command line --config
    private static Config config;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        handleOptions(args); // check for --config option
        config = new Config(configPath);

        Random rng = new Random(config.getRngSeed());


        // generate all solutions by passing CreateRandTrees to exec pool
        // .. wait till done ..
        // evaluate all fitnesses using EvaluateFitness -> exec
        // .. wait ..
        // find best!
        // .. wait ..
        // !- cha-ching -!

        List<Tree<Boolean>> forest = createForest();
        Map<Tree<Boolean>, Double> fitness = calculateFitness(forest);
        Tree<Boolean> best = findBestAndLog(fitness);

        writeToFile(best.toString());

        // -! cha-ching !-
    }

    private static List<Tree<Boolean>> createForest() { // TODO probably move this to a class implementing Runnable for later assignments
        // create the forest
        List<CreateRandomTree> workers = new ArrayList<CreateRandomTree>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            CreateRandomTree worker = new CreateRandomTree(rng, config.getMaxDepth());
            workers.add(worker);
            exec.run(worker); // TODO exec or guava // impl
        }

        // wait for all the workers to finish creating trees
        // ..

        // harvest forest values
        List<Tree<Boolean>> forest = new ArrayList<Tree<Boolean>>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            CreateRandomTree worker = workers.get(g);
            forest.add(worker.getTree());
        }

        return forest;
    }

    private static Map<Tree<Boolean>, Double> calculateFitness( List<Tree<Boolean>> forest) {
        // evaluate fitness of each tree in forest
        List<EvaluateFitness> workers = new ArrayList<EvaluateFitness>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            EvaluateFitness worker = new EvaluateFitness(forest.get(g));
            workers.add(worker);
            exec.run(worker); // TODO exec or guava // impl
        }

        // .. wait for all the workers to finish .. //

        // harvest fitness values
        Map<Tree<Boolean>, Double> fitness = new HashMap<Tree<Boolean>, Double>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            EvaluateFitness worker = workers.get(g);
            fitness.put(worker.getTree(), worker.getFitness());
        }

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
                log.write(g + "\t" + best + "\n\n");
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
