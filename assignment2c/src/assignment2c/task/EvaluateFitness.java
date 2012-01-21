package assignment2c.task;

import assignment2c.Config;
import assignment2c.Solution;
import assignment2c.tree.Outcome;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author William Riley-Land
 */
public class EvaluateFitness implements Callable<Double> {

    private Config config = Config.getSingleton();
    private Solution prisoner;
    private List<Solution> opponents;

    // evaluates average fitness of random sample of opponentPool
    public EvaluateFitness(Solution prisoner, List<Solution> opponentPool) {
        this.prisoner = prisoner;
        this.opponents = new ArrayList<Solution>(config.getCoevolutionarySampleSize());

        // draw opponents from the pool till we have the number specified in config
        while (this.opponents.size() < config.getCoevolutionarySampleSize()) {
            Solution possible = opponentPool.get(config.getRng().nextInt(opponentPool.size()));
            if (!this.opponents.contains(possible)) {
                opponents.add(possible);
            }
        }
    }

    // evaluates un-averaged fitness (single opponent as for assignments 2a and 2b)
    public EvaluateFitness(Solution prisoner, Solution opponent) {
        this.prisoner = prisoner;
        this.opponents = new ArrayList<Solution>(1);
        this.opponents.add(opponent);
    }

    private Outcome updateHistory(List<Outcome> history, Solution opponent, boolean prisonerCooperates, boolean opponentCooperates) {
        Outcome outcome = new Outcome(this.prisoner, opponent, prisonerCooperates, opponentCooperates);
        history.add(0, outcome); // add newest element
        history.remove(history.size() - 1); // remove oldest element
        return outcome;
    }

    private int play(List<Outcome> history, Solution opponent, int numberOfGames) {
        int sumPayoffs = 0;

        for (int g = 0; g < numberOfGames; g++) {
            boolean prisonerCooperates = this.prisoner.getTree().getValue(history, this.prisoner);
            boolean opponentCooperates = opponent.getTree().getValue(history, opponent);
            Outcome outcome = this.updateHistory(history, opponent, prisonerCooperates, opponentCooperates);
            sumPayoffs += outcome.getPayoffFor(this.prisoner);
        }

        return sumPayoffs;
    }

    @Override
    public Double call() {

        // this method might be made better by caching the results so that if two
        // solutions are paired more than once, the fitness evaluation does not
        // need to be performed again, but it seems like the overhead would not
        // be worth it in this case: it would complicate conurrency and the
        // fitness function does not take much time to calculate anyway

        double sumFitness = 0.0;

        for (Solution opponent : opponents) {

            List<Outcome> history = Outcome.createRandomHistory(this.prisoner, opponent, config.getHistoryLength());

            // first play some games to prime the history
            this.play(history, opponent, 2 * history.size());

            // now, play for keeps
            int sumPayoffs = this.play(history, opponent, config.getNumberOfGames());

            // calulate fitness score regardless of parsminoy pressure
            double rawFitness = (double) sumPayoffs / (double) config.getNumberOfGames();

            sumFitness += rawFitness;
        }

        // find the average fitness across all opponents
        double avgFitness = sumFitness / opponents.size();

        // apply parsimony pressure before returning
        return avgFitness - (config.getParsimonyPressure() * (double) prisoner.getTree().getTreeDepth());
    }
}
