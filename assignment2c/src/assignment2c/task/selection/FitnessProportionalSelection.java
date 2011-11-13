/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task.selection;

import assignment2c.Config;
import assignment2c.Solution;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class FitnessProportionalSelection extends SelectTree<Solution> {

    private Config config = Config.getSingleton();

    @Override
    public List<Solution> select(int numberToSelect, List<Solution> pool) {
        double totalFitness = 0.0;

        for(Solution s : pool) {
            totalFitness += s.getFitness();
        }

        // stochastic universal sampling algorithm (p. 63)
        // first, generate the array of probabilities to enter mating pool
        double[] a = new double[pool.size()];
        a[0] = pool.get(0).getFitness() / totalFitness;

        for (int i = 1; i < pool.size(); i++) {
            double fitness = pool.get(i).getFitness();
            double probabilityOfParenthood = fitness / totalFitness;
            a[i] = a[i - 1] + probabilityOfParenthood;
        }

        List<Solution> matingPool = new ArrayList<Solution>(numberToSelect);
        int currentMember = 0;
        int i = 0;
        double r = (config.getRng().nextDouble() % 1) / (double) numberToSelect; // random value between 0 and 1 / #parents 
        while (currentMember < numberToSelect) {
            while (r <= a[i]) {
                matingPool.add(pool.get(i));
                r = r + (1.0 / numberToSelect);
                currentMember++;
            }
            i++;
        }

        return matingPool;
    }
}
