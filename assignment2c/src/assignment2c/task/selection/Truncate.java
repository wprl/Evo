/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task.selection;

import assignment2c.FitnessComparator;
import assignment2c.Solution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class Truncate extends SelectTree<Solution> {

    @Override
    public List<Solution> select(int numberToSelect, List<Solution> pool) {
        if (numberToSelect < 1 || numberToSelect > pool.size() - 1) {
            throw new IllegalArgumentException("Pool size must be greater than 1 and less than the pool size.");
        }

        List<Solution> sortedPool = new ArrayList<Solution>(pool);
        Collections.sort(sortedPool, new FitnessComparator()); // sort by low to high fitness
        
        return sortedPool.subList(0, numberToSelect);
    }
}
