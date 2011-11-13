package assignment2c;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

        System.out.println("GENETIC PROGRAMMING ASSIGNMENT 2B");
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
            int fitnessStaticFor = config.getPopulationSize(); // fitness static for lambda evals initially

            while (p.getNumberOfEvaluations() < config.getNumberOfEvaluationsPerRun()) {

                if (fitnessStaticFor > config.getStopIfFitnessStaticFor()) {
                    break;
                }

                Solution generationBest = p.getBest();

                if (runBest == null || generationBest.getFitness() > runBest.getFitness()) {
                    runBest = generationBest;
                    fitnessStaticFor = 0;
                } else {
                    fitnessStaticFor += config.getNumberOfChildren();
                }

                log.write(p.getNumberOfEvaluations() + "\t" + p.getAverageFitness() + "\t" + p.getBest().getFitness() + "\n\n");

                p = p.nextGeneration(config.getParentSelector(), config.getSurvivalSelector());
            }

            log.write(p.getNumberOfEvaluations() + "\t" + p.getAverageFitness() + "\t" + p.getBest().getFitness() + "\n\n");

            if (experimentBest == null || runBest.getFitness() > experimentBest.getFitness()) {
                experimentBest = runBest;
            }
        }

        experimentBest.writeToPath(config.getSolutionPath());

        config.getExec().shutdown();
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
