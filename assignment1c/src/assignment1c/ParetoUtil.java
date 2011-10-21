/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment1c;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jagwio
 */
public class ParetoUtil {

    private List<ParetoFront> fronts = new ArrayList<ParetoFront>();
    private Map<Solution, Double> rawFitness = new HashMap<Solution, Double>();
    private Map<Solution, Double> sharedFitness = new HashMap<Solution, Double>();
    private double shareDistance;

    public ParetoUtil(List<Solution> population, double shareDistance) {
        this.shareDistance = shareDistance;

        this.classify(population);
        this.calculateRawFitnesses();
        this.shareFitnesses();
    }

    /**
     * gets a front (0-based index)
     * @param i index of front
     * @return front at index i
     */
    public ParetoFront getFront(int i) {
        return this.fronts.get(i);
    }

    /**
     * Get the fitness for given solution based on Pareto fronts and fitness sharing
     * @param s the given solution
     * @return fitness based on Pareto fronts and fitness sharing
     */
    public double getFitnessFor(Solution s) {
        if (!this.sharedFitness.containsKey(s)) {
            System.out.println("Fitness was not found!!");
            System.exit(1);
        }
        return sharedFitness.get(s);
    }

    /**
     * Classify the population into Pareto fronts
     * @param population pop to be classified
     */
    private void classify(List<Solution> population) {

        boolean classified = false;

        List<Solution> consider = new ArrayList<Solution>(population);

        // loop until all solutions are assigned to a front
        while (consider.size() > 0) {
            // add all non-dominated solutions as a new front
            List<Solution> frontSolutions = nonDominatedSolutions(consider);
            ParetoFront front = new ParetoFront(frontSolutions);
            fronts.add(front);
            consider.removeAll(frontSolutions); // remove these from consideration for next front
        }
    }

    /**
     * Given some solutions, find all the non-dominated members
     * @param population the given solutions
     * @return only non-dominated members
     */
    private List<Solution> nonDominatedSolutions(List<Solution> population) {
        // find non-dominated that aren't part of previous front
        List<Solution> notDominated = new ArrayList<Solution>();

        for (Solution s1 : population) { // determine if s1 is dominated
            boolean dominated = false;
            
            for (Solution s2 : population) {
                if (doesDominate(s1, s2)) {
                    dominated = true;
                    break;
                }
            }

            if (!dominated) {
                notDominated.add(s1);
            }
        }

        return notDominated;
    }

    /**
     * Determines if s1 is dominated by s2
     * @param s1
     * @param s2
     * @return true if s2 dominates s1
     */
    private boolean doesDominate(Solution s1, Solution s2) {
        int deactivated1 = s1.deactivatedCount();
        int deactivated2 = s2.deactivatedCount();
        int uncut1 = s1.uncutPaths();
        int uncut2 = s2.uncutPaths();

        if (deactivated2 < deactivated1) {
            return true;
        } else if (uncut1 < uncut2) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * calculate the raw fitness for each front/solution (this is the number of
     * solutions in lower fronts.
     */
    private void calculateRawFitnesses() {
        // loop through fronts backwards and calculate raw fitness for each front
        double rawFitnessForFrontI = 0.0;

        for (int i = this.fronts.size() - 1; i >= 0; i--) {
            ParetoFront current = this.fronts.get(i);

            for (Solution s : current.getMembers()) {
                this.rawFitness.put(s, rawFitnessForFrontI);
            }

            // next fitness score is count of all previous fronts' members
            rawFitnessForFrontI += current.count();
        }
    }

    /**
     * Calculate the shared fitness of each solution in the population
     */
    private void shareFitnesses() {

        for (ParetoFront current : this.fronts) {
            for (Solution s1 : current.getMembers()) {

                double denominator = 0.0;

                for (Solution s2 : current.getMembers()) {
                    double distance = s1.distanceFrom(s2);
                    double sharing;

                    if (distance <= shareDistance) {
                        sharing = 1.0 - (distance / shareDistance); // alpha = 1
                    } else {
                        sharing = 0.0;
                    }

                    denominator += sharing;
                }

                double newFitness = this.rawFitness.get(s1) / denominator;
                this.sharedFitness.put(s1, newFitness);
            }
        }
    }
}
