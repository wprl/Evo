/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.selection;

import assignment2a.Population;
import assignment2a.Solution;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jagwio
 */
public class KTournament implements ISelection {
    /**
     * Private method to perform a k-tournament
     * @param k number of solutions in tournament
     * @param length number of survivors
     * @return a list of solutions that won the tournament
     */
    private List<Solution> kTournament(int k, int length) {

        List<Solution> result = new ArrayList<Solution>();

        while (result.size() < length) {
            // pick k unique individuals
            List<Solution> kIndividuals = new ArrayList<Solution>();
            List<Solution> availableIndividuals = new ArrayList<Solution>(this.population);
            for (int i = 0; i < k; i++) {
                int randomIndex = rng.nextInt(availableIndividuals.size()); // popping off a stack would be easier for concurrency
                kIndividuals.add(availableIndividuals.get(randomIndex));
                availableIndividuals.remove(randomIndex);
            }

            // find the best out of those k individuals
            Solution best = null;
            for (Solution s : kIndividuals) {
                if (best == null || this.pareto.getFitnessFor(best) < this.pareto.getFitnessFor(s)) {
                    best = s;
                }
            }

            // add the best to the pool
            result.add(best);
        }

        return result;
    }

    @Override
    public void initialize(Population , int ) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Solution> getSelected() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
