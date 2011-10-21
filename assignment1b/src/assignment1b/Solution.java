/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment1b;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Class representing a single solution in the population
 * @author William P. Riley-Land 
 */
public class Solution {

    private Path[] network;
    private boolean[] genotype;
    private double penaltyCoefficient;
    private Random rng;
    private Double fitness;

    /**
     * Instantiate a new solution
     * @param numRouters the number of routers in the network
     * @param network an array of paths (routers and hosts)
     * @param penaltyCoefficient
     * @param rng "global" random number generator
     */
    public Solution(int numRouters, Path[] network, double penaltyCoefficient, Random rng) {
        this.genotype = new boolean[numRouters];
        this.network = network;
        this.penaltyCoefficient = penaltyCoefficient;
        this.rng = rng;
    }

    /**
     * Mutate this solution (in place)
     * @param mutationRate probability a gene will be mutated
     */
    public void mutate(double mutationRate) {
        if (mutationRate < 0 || mutationRate > 1) {
            throw new IllegalArgumentException("Mutation rate must be between 0 and 1 (inclusive).");
        }

        for (int i = 0; i < this.genotype.length; i++) {
            double randomValue = rng.nextDouble() % 1; // get fractional part
            if (randomValue <= mutationRate) {
                this.genotype[i] = !this.genotype[i];
            }
        }
    }

    /**
     * Mate this solution with another using uniform crossover
     * @param mate the other solution to mate with
     * @return an array of both children
     */
    public Solution[] uniformCrossover(Solution mate) {
        boolean[] childGenotype1 = new boolean[this.genotype.length];
        boolean[] childGenotype2 = new boolean[this.genotype.length];

        for (int i = 0; i < this.genotype.length; i++) {
            double randomValue = rng.nextDouble() % 1; // get fractional part
            if (randomValue > 0.5) {
                childGenotype1[i] = this.genotype[i];
                childGenotype2[i] = mate.genotype[i];
            } else {
                childGenotype1[i] = mate.genotype[i];
                childGenotype2[i] = this.genotype[i];
            }
        }

        Solution child1 = new Solution(this.genotype.length, this.network, this.penaltyCoefficient, this.rng);
        Solution child2 = new Solution(this.genotype.length, this.network, this.penaltyCoefficient, this.rng);

        child1.genotype = childGenotype1;
        child2.genotype = childGenotype2;

        return new Solution[]{child1, child2};
    }

    /**
     * Mate this solution with another using n-point crossover
     * @param mate the solution to mate with
     * @param n the number of crosover points
     * @return an array of both children
     */
    public Solution[] nPointCrossover(Solution mate, int n) {
        if (n < 1 || n > this.genotype.length - 1) {
            throw new IllegalArgumentException("n for n-points corssover must be within range 1 to " + (this.genotype.length - 1));
        }

        // first, pick n points (the points come after the index, so only 0 to #genes - 1)
        List<Integer> availableIndices = new ArrayList<Integer>();
        for (int i = 0; i < this.genotype.length - 1; i++) {
            availableIndices.add(i);
        }

        List<Integer> crossoverPoints = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            int chosenIndex = this.rng.nextInt(availableIndices.size()); // pick a random index in to the available indices list
            crossoverPoints.add(availableIndices.get(chosenIndex)); // add it to the crossover points list
            availableIndices.remove(chosenIndex); // remove it from the available indices to be chosen
        }

        Collections.sort(crossoverPoints); // order crossovers from low to high

        boolean[] childGenotype1 = new boolean[this.genotype.length]; // TODO think we only want one child
        boolean[] childGenotype2 = new boolean[this.genotype.length];

        boolean takeFromThisParent = true; // if we're taking genes for child1 from this or mate

        for (int i = 0; i < this.genotype.length; i++) {
            if (takeFromThisParent) {
                childGenotype1[i] = this.genotype[i];
                childGenotype2[i] = mate.genotype[i];
            } else {
                childGenotype1[i] = mate.genotype[i];
                childGenotype2[i] = this.genotype[i];
            }

            // if there is a crossover points after this index, switch who children get next gene(s) from
            if (crossoverPoints.contains(i)) {
                takeFromThisParent = !takeFromThisParent;
            }
        }

        Solution child1 = new Solution(this.genotype.length, this.network, this.penaltyCoefficient, this.rng);
        Solution child2 = new Solution(this.genotype.length, this.network, this.penaltyCoefficient, this.rng);

        child1.genotype = childGenotype1;
        child2.genotype = childGenotype2;

        return new Solution[]{child1, child2};
    }

    /**
     * The number of deactivated routers
     * @return deactivated number
     */
    public int deactivatedCount() {
        int deactivatedCount = 0;

        for (boolean b : this.genotype) {
            if (!b) {
                deactivatedCount++;
            }
        }

        return deactivatedCount;
    }

    /**
     * Calculate the number of uncut paths in the network
     * @return uncut paths
     */
    public int uncutPaths() {
        int uncutPaths = 0;

        for (Path p : this.network) {
            boolean atLeastOneDisabled = false;
            int[] routersInPath = p.getRouters();

            for (int i = 0; i < routersInPath.length; i++) {
                int routerId = routersInPath[i];
                boolean routerIsDisabled = !this.genotype[routerId];
                if (routerIsDisabled) {
                    atLeastOneDisabled = true;
                    break;
                }
            }

            if (!atLeastOneDisabled) {
                uncutPaths++;
            }
        }

        return uncutPaths;
    }

    /**
     * Get the fitness of this solution
     * Does not update if solution is changed (need to instantiate a new one)
     * @return fitness
     */
    public double getFitness() {
        if (this.fitness == null) {
            this.fitness = 1.0 + (double) (this.genotype.length -  this.deactivatedCount()) - (this.penaltyCoefficient * (double) this.uncutPaths());;
            //this.fitness = (1.0 / (1.0 + this.deactivatedCount())) - (this.penaltyCoefficient * (double) this.uncutPaths());
        }

        // TODO probably could use numPaths - disconnectedPaths (simplifies to connectedPaths) (probably 1 + preveious to avoid zero val)

        return this.fitness;
    }

    /**
     * This method randomizes the genotype using the following algorithm:
     * 1) Generate an integer in the solution space i.e. 0 to (2 ^ numRouters) - 1
     *      This integer represents (in binary) a collection of enabled and
     *      disabled routers according to whether the integer has a 1 or 0 at bit i
     * 2) The individual bits of the integer are determined by ANDing the random integer
     *      with an appropriate integer value, e.g. for 8 routers, the first value
     *      of currentBit will be 10000000, the next will be 01000000, etc.
     * 3) Bit shift currentBit rightwards (move to next bit)
     * 4) Repeat steps 2 and 3 until all bits (routers) are assigned a random state
     */
    public void randomize() {
        int numRouters = this.genotype.length;
        BigInteger randomInt = new BigInteger(numRouters, this.rng); // make a random int with range 0 to (2 ^ numRouters) - 1

        BigInteger zero = new BigInteger("0");
        BigInteger currentBit = new BigInteger("2").pow(numRouters - 1); // used for ANDing 

        for (int i = 0; i < this.genotype.length; i++) {

            BigInteger bit = randomInt.and(currentBit);

            // if the result of the AND is 0, the bit in position i is not set (router is disabled
            // if the result is > 0, the bit in position i is 1 (it will be a power of 2 corresponding to the bit position)
            if (bit.equals(zero)) {
                this.genotype[i] = false;
            } else {
                this.genotype[i] = true;
            }

            currentBit = currentBit.shiftRight(1); // move to next bit
        }
    }

    /**
     * Convert to bit string representation
     * @return a bit string representing routers enabled/disabled
     */
    @Override
    public String toString() {
        // outputs a binary string representing this solution
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < this.genotype.length; i++) {
            if (this.genotype[i]) {
                s.append("1");
            } else {
                s.append("0");
            }
        }

        return s.toString();
    }

    /**
     * Convert to solution string appropriate for assignment deliverables
     * @param routerIdOffset starting id for routers (IDs are 0-based internally)
     * @return a tab delimeted string of disabled routers
     */
    public String toSolutionString(int routerIdOffset) {
        // outputs a tab delimited list of deactivated routers
        // the router offset is used to convert the routers back from 0-based index
        //     to the number from the input file.
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < this.genotype.length; i++) {
            if (!this.genotype[i]) {
                s.append(i + routerIdOffset);
                s.append("\t");
            }
        }

        s.deleteCharAt(s.length() - 1); // remove last tab
        return s.toString();
    }

    /**
     * Write this solution to a file in format appropriate for assignment deliverables
     * @param path location to write file
     * @param routerIdOffset starting id for routers (IDs are 0-based internally)
     */
    public void writeToFile(String path, int routerIdOffset) {
        File file = new File(path);
        String solutionString = this.toSolutionString(routerIdOffset);

        try {
            file.createNewFile();
            FileWriter out = new FileWriter(file);
            out.write(solutionString);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR: failed to write solution file.");
            System.exit(1);
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
