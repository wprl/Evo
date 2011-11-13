/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c;

import java.util.Comparator;

/**
 * Utility class used to compare two solutions based on fitness
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class FitnessComparator implements Comparator<Solution> {

    /**
     * compare two solutions based on fitness
     * @param a first solution
     * @param b other solution
     * @return a negative value if a's fitness is less than b's, 0 if they are equal, and a postive value otherwise
     */
    @Override
    public int compare(Solution a, Solution b) {
        return Double.compare(a.getFitness(), b.getFitness());
    }
}
