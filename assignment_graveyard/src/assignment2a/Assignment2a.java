package assignment2a;

import java.io.File;
import java.io.IOException;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        handleOptions(args); // check for --config option
        Config config = new Config(configPath);
        Log log = new Log(config.getLogPath());
        InputFile in = new InputFile(config.getInputPath());

        Solution best = null;
        Population bestPop = null;
        Random rng = new Random(config.getRngSeed());

        log.write("Result Log\n\n");

        for (int run = 0; run < config.getNumRuns(); run++) {
            log.write("Run " + run + "\n\n");
            Population pop = new Population(
                    config.getPopulationSize(),
                    in.getRouterCount(),
                    in.getNetwork(),
                    config.getPenaltyCoefficient(),
                    rng);

            int evals = config.getPopulationSize();
            double previousBestFitness = 0;
            int fitnessSameFor = 0;

            for (int generation = 0; generation < config.getNumIterations(); generation++) {
                // Check whether the best fitness for the population is the same as last eval
                Solution populationBest = pop.getBest();
                if (previousBestFitness == populationBest.getFitness()) {
                    fitnessSameFor++;
                } else {
                    fitnessSameFor = 0;
                }

                previousBestFitness = populationBest.getFitness();

                // Check whether there is a new best solution across all runs
                if (best == null || populationBest.getFitness() > best.getFitness()) {
                    best = populationBest;
                }

                log.write(evals + "\t" + pop.getAverageFitness() + "\t" + populationBest.getFitness() + "" + "\n\n");

                if (fitnessSameFor == config.getStopWhenNoChangeFor()) {
                    break; // done with this run
                }

                // do mating
                if (config.getParentSelectionMethod().compareTo("fitness-proportional") == 0) {
                    pop.generateOffspringFitnessProportional(config.getRecombinationMethod(), config.getOffspringSize(), config.getMutationRate(), config.getnCrossoverPoints());
                } else {
                    pop.generateOffspringKTournaments(config.getRecombinationMethod(), config.getOffspringSize(), config.getkParentTournaments(), config.getMutationRate(), config.getnCrossoverPoints());
                }

                // do survival
                if (config.getSurvivalSelectionMethod().compareTo("truncation") == 0) {
                    pop.survivalTruncation();
                } else {
                    pop.survivalKTournament(config.getkSurvivalTournaments());
                }

                evals += config.getOffspringSize(); // update eval count now that lambda children have been made
            }

            // Check whether there is a new best final average
            if (bestPop == null || pop.getAverageFitness() > bestPop.getAverageFitness()) {
                bestPop = pop;
            }
        }

        log.close();

        bestPop.getFirstFront().writeToFile(config.getSolutionPath(), in.getPathCount());
    }

    /**
     * Private method used to handle command line arguments
     * @param args command line arguments
     */
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
}
