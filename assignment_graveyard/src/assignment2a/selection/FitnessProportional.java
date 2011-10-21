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
public class FitnessProportional {
    /**
     * Generate off spring using fitness proportional parent selection
     * @param matingMethod string denoting whether to use uniform or n-point x-over
     * @param numOffspring the number of offspring produced in mating step
     * @param mutationRate probability a gene will be mutated
     * @param nPoints if using n-point crossover, n points to use
     */
    public List<Solution> select( Population p, int n, double totalFitness) {

        // stochastic universal sampling algorithm (p. 63)
        // first, generate the array of probabilities to enter mating pool
        double[] a = new double[p.size()];
        a[0] = this.pareto.getFitnessFor(p.get(0)) / totalFitness;

        for (int i = 1; i < p.size(); i++) {
            double fitness = this.pareto.getFitnessFor(p.get(i));
            double probabilityOfParenthood = fitness / totalFitness;
            a[i] = a[i - 1] + probabilityOfParenthood;
        }

        List<Solution> matingPool = new ArrayList<Solution>();
        int currentMember = 0;
        int i = 0;
        double r = (rng.nextDouble() % 1) / (double) n; // random value between 0 and 1 / #offspring (lambda)
        while (currentMember < n) {
            while (r <= a[i]) {
                matingPool.add(p.get(i));
                r = r + (1.0 / n);
                currentMember++;
            }
            i++;
        }

        return matingPool;
    }
}
