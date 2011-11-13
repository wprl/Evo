/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task.selection;

import assignment2c.Config;
import assignment2c.FitnessComparator;
import assignment2c.Solution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class OverSelection extends SelectTree<Solution> {

    private Config config = Config.getSingleton();

    @Override
    public List<Solution> select(int numberToSelect, List<Solution> pool) {
        double x = config.getPercentInUpperTierForOverselection();

        List<Solution> sortedPool = new ArrayList<Solution>(pool);
        Collections.sort(sortedPool, new FitnessComparator()); // sort by low to high fitness

        int numberInUpper = (int) ((double) pool.size() * x); // x% are in top tier
        int numberInLower = pool.size() - numberInUpper; // (100 - x)% are in bottom tier

        List<Solution> lowerTier = sortedPool.subList(0, numberInLower);
        List<Solution> upperTier = sortedPool.subList(numberInLower, numberInUpper);

        int numberFromUpper = (int) ((double) pool.size() * 0.8); // 80% come from top tier
        int numberFromLower = pool.size() - numberFromUpper; // 20% from bottom tier

        List<Solution> parents = new ArrayList<Solution>(numberToSelect);
        for (int i = 0; i < numberFromUpper; i++) {
            parents.add(upperTier.get(config.getRng().nextInt(upperTier.size())));
        }
        for (int i = 0; i < numberFromLower; i++) {
            parents.add(lowerTier.get(config.getRng().nextInt(lowerTier.size())));
        }

        return parents;
    }
}
