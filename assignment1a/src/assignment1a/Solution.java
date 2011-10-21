/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment1a;

import java.math.BigInteger;
import java.util.Random;

/**
 *
 * @author William P. Riley-Land 
 */
public class Solution {

    private Path[] network;
    private boolean[] genotype;
    private double penaltyCoefficient;

    public Solution(int numRouters, Path[] network, double penaltyCoefficient) {
        this.genotype = new boolean[numRouters];
        this.network = network;
        this.penaltyCoefficient = penaltyCoefficient;
    }

    public int deactivatedCount() {
        int deactivatedCount = 0;

        for (boolean b : this.genotype) {
            if (!b) {
                deactivatedCount++;
            }
        }

        return deactivatedCount;
    }

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
            
            if(!atLeastOneDisabled) {
                uncutPaths++;
            }
        }

        return uncutPaths;
    }

    public double fitness() {
        return (1.0 / (1.0 + this.deactivatedCount())) - (this.penaltyCoefficient * (double)this.uncutPaths());
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
    public void randomize(Random rng) {
        int numRouters = this.genotype.length;
        BigInteger randomInt = new BigInteger(numRouters, rng); // make a random int with range 0 to (2 ^ numRouters) - 1
        
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
}
