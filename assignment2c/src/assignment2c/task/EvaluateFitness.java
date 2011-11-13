/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private List<Outcome> history = Outcome.createRandomHistory(config.getHistoryLength());
    private Solution solution;
    private List<Solution> opponents;

    public EvaluateFitness(Solution solution, List<Solution> pool) {
        this.solution = solution;
        this.opponents = new ArrayList<Solution>(config.getNumberOfPlaymates());

        while (this.opponents.size() < config.getNumberOfPlaymates()) {
            Solution possible = pool.get(config.getRng().nextInt(pool.size()));
            if (!this.opponents.contains(possible)) {
                opponents.add(possible);
            }
        }
    }

    private Outcome updateHistory(boolean prisonerCooperates, boolean opponentCooperates) {
        Outcome outcome = new Outcome(prisonerCooperates, opponentCooperates);
        this.history.add(0, outcome); // add newest element
        this.history.remove(this.history.size() - 1); // remove oldest element
        return outcome;
    }

    @Override
    public Double call() {

        // this method might be made better by caching the results so that if two
        // solutions are paired more than once, the fitness evaluation does not
        // need to be performed again, but it seems like the overhead would not
        // be worth it in this case, because it complicates conurrency and the
        // fitness formula does not take much time to calculate

        double sumFitness = 0.0;

        for (Solution opponent : opponents) {

            // first play some games to prime the history
            for (int g = 0; g < 2 * this.history.size(); g++) {
                boolean prisonerCooperates = this.solution.getTree().getValue(this.history);
                boolean opponentCooperates = this.history.get(0).getPrisonerCooperates(); // opponent mimics prisoner's last move (tit-for-tat)
                this.updateHistory(prisonerCooperates, opponentCooperates);
            }

            // now, play for keeps
            int sumPayoffs = 0;

            for (int g = 0; g < config.getNumberOfGames(); g++) {
                boolean prisonerCooperates = this.solution.getTree().getValue(this.history);
                boolean opponentCooperates = opponent.getTree().getValue(this.history);

                Outcome outcome = this.updateHistory(prisonerCooperates, opponentCooperates);
                sumPayoffs += outcome.getPayoff();
            }

            // calulate fitness score regardless of parsminoy pressure
            double rawFitness = (double) sumPayoffs / (double) config.getNumberOfGames();
            
            sumFitness += rawFitness;
        }

        // find the average fitness across all opponents
        double avgFitness = sumFitness / opponents.size();

        // apply parsimony pressure before returning
        return avgFitness - (config.getParsimonyPressure() * (double) solution.getTree().getTreeDepth());
    }
}
