package assignment2c;

import assignment2c.task.EvaluateFitness;
import assignment2c.tree.OpponentNode;
import assignment2c.tree.Outcome;
import assignment2c.tree.Tree;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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
public class Assignment2c {

    private static String configPath = "./default.cfg"; // default.  override with command line --config
    private static Config config;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException, InstantiationException, IllegalAccessException {
        System.out.println("GENETIC PROGRAMMING -- ASSIGNMENT 2C");
        System.out.println("Number of processors/cores: " + Runtime.getRuntime().availableProcessors());

        handleOptions(args); // check for --config option

        config = new Config(configPath);

        Log log = new Log(config.getLogPath());
        log.write("Result Log\n\n");

        Solution experimentBest = null;

        for (int run = 0; run < config.getNumberOfRuns(); run++) {

            log.write("Run " + run + "\n\n");

            Population p = new Population(); // creates first generation
            Solution runBest = null;
            Solution lastGenerationBest = null;

            // fitness static for (lambda * co-evolutionary sample size) evals initially
            int fitnessStaticFor = config.getPopulationSize() * config.getCoevolutionarySampleSize();

            while (runBest == null || p.getTotalNumberOfEvaluations() < config.getNumberOfEvaluationsPerRun()) { // must run loop at least once

                if (runBest != null && fitnessStaticFor >= config.getStopIfFitnessStaticFor()) {
                    break;
                }

                if (runBest == null || p.getBest().getFitness() > runBest.getFitness()) {
                    runBest = p.getBest();
                }

                log.write(p.getTotalNumberOfEvaluations() + "\t" + p.getAverageFitness() + "\t" + p.getBest().getFitness() + "\n\n");

                lastGenerationBest = p.getBest();

                p = p.nextGeneration(config.getParentSelector(), config.getSurvivalSelector());

                if (lastGenerationBest.getFitness() == p.getBest().getFitness()) {
                    fitnessStaticFor += config.getNumberOfChildren() * config.getCoevolutionarySampleSize();
                } else {
                    fitnessStaticFor = 0;
                }
            }

            log.write(p.getTotalNumberOfEvaluations() + "\t" + p.getAverageFitness() + "\t" + p.getBest().getFitness() + "\n\n");

            if (experimentBest == null || runBest.getFitness() > experimentBest.getFitness()) {
                experimentBest = runBest;
            }

            // write un-avaraged fitness for run best vs. tit-for-tat
            Solution titForTat = new Solution(new OpponentNode(new ArrayList<Tree<Boolean>>(), 0, 0));
            EvaluateFitness worker = new EvaluateFitness(runBest, titForTat);
            double bestFitnessVsTitForTat = worker.call();

            log.write("ABSOLUTE FITNESS\n\n" + bestFitnessVsTitForTat + "\n\n");
        }

        config.getExec().shutdown();

        log.close();

        experimentBest.writeToPath(config.getSolutionPath());

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
