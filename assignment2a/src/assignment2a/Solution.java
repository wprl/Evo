/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a;

import assignment2a.program.AndNode;
import assignment2a.program.NotNode;
import assignment2a.program.OpponentNode;
import assignment2a.program.OrNode;
import assignment2a.program.PrisonerNode;
import assignment2a.program.Tree;
import assignment2a.program.XorNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Class representing a single solution in the population
 * @author William P. Riley-Land 
 */
public class Solution {

    private Tree<Boolean> program;
    private Random rng;
    private Double fitness;

    public static Solution createRandom(int depth) {
    }

    private static Tree<Boolean> createRandomTree(int depth, int maxDepth) {
        // TODO do we need solution class, or just a bunch of runnables?

        // First pick what kind of node this will be
        int type;

        if (depth != maxDepth) {
            type = rng.nextInt(6);
        } else {
            type = rng.nextInt(2) + 4; // may only be terminal node
        }

        // Next, find children and return the node
        List<Tree<Boolean>> children = new ArrayList<Tree<Boolean>>(2); // 2 elements max
        switch (type) {
            case 0: // NOT
                Tree<Boolean> child = createRandomTree(depth + 1, maxDepth);
                children.add(child);
                break;
            case 1: // AND
            case 2: // OR
            case 3: // XOR
                Tree<Boolean> child1 = createRandomTree(depth + 1, maxDepth);
                Tree<Boolean> child2 = createRandomTree(depth + 1, maxDepth);
                children.add(child1);
                children.add(child2);
                break;
            case 4: // Opponent history value
            case 5: // Prisoner history value
                break;
            default:
                throw new IllegalStateException("RNG wrong");
        }

        // Finally, create and return the node
        switch (type) {
            case 0: // NOT
                return new NotNode(children);
            case 1: // AND
                return new AndNode(children);
            case 2: // OR
                return new OrNode(children);
            case 3: // XOR
                return new XorNode(children);
            case 4: // Opponent history value
                return new OpponentNode(children, opponentHistory);
            case 5: // Prisoner history value
                return new PrisonerNode(children, prisonerHistory);
            default:
                throw new IllegalStateException("very BAD");
        }
    }

    /**
     * Utility class used to compare two solutions based on fitness
     */
    public static class FitnessComparator implements Comparator<Solution> {

        /**
         * compare two solutions based on fitness
         * @param a first solution
         * @param b other solution
         * @return a negative value if a's fitness is less than b's, 0 if ther are equal, and a postive value otherwise
         */
        @Override
        public int compare(Solution a, Solution b) {
            return Double.compare(a.getFitness(), b.getFitness());
        }
    }
}
