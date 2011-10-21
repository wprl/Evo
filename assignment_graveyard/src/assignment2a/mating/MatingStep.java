/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.mating;

import assignment2a.ParetoUtil;
import assignment2a.Solution;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author jagwio
 */
public class MatingStep implements Runnable {



    /**
     * Generate offspring using k-tournament
     * @param matingMethod string denoting whether to use uniform or n-point x-over
     * @param numOffspring the number of offspring produced in mating step
     * @param k number of solutions in tournament
     * @param mutationRate probability a gene will be mutated
     * @param nPoints if using n-point crossover, n points to use
     */
    public void generateOffspringKTournaments(String matingMethod, int numOffspring, int k, double mutationRate, int nPoints) {
        ExecutorService exec = Executors.newFixedThreadPool(N_THREADS);
        
        exec.

        exec.execute(new )

        List<Solution> matingPool = ;
        this.generateOffspring(matingMethod, matingPool, mutationRate, nPoints);
    }

    /**
     * Private method used for common mating algorithm pieces
     * @param matingMethod string denoting whether to use uniform or n-point x-over
     * @param matingPool solutions eligible for mating
     * @param mutationRate probability a gene will be mutated
     * @param nPoints if using n-point crossover, n points to use
     */
    private void generateOffspring(String matingMethod, List<Solution> matingPool, double mutationRate, int nPoints) {
        for (Solution parent1 : matingPool) {
            // for each parent, choose a random solution from the pool to mate with
            int randomIndex = this.rng.nextInt(matingPool.size());
            Solution parent2 = matingPool.get(randomIndex);

            // get the children, mutate them (possibly), and add them to the population
            Solution[] children;
            if (matingMethod.compareTo("uniform-crossover") == 0) {
                children = parent1.uniformCrossover(parent2);
            } else {
                children = parent1.nPointCrossover(parent2, nPoints);
            }

            children[0].mutate(mutationRate);
            children[1].mutate(mutationRate);

            this.population.add(children[0]);
            this.population.add(children[1]);
        }

        // calculate new Pareto fronts
        this.pareto = new ParetoUtil(this.population, 7); // TODO make omega share configurable?!
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
