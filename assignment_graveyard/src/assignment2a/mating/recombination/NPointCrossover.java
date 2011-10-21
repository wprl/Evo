/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.mating.recombination;

import assignment2a.Solution;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jagwio
 */
public class NPointCrossover implements IRecombinator, Runnable {

    private Solution a;
    private Solution b;

    private List<Solution> children;

    public NPointCrossover(Solution a, Solution b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public List<Solution> getChildren() {
        return new ArrayList<Solution>(children);
    }


    @Override
    public void recombine() {
        if (n < 1 || n > a.genotype.length() - 1) {
            throw new IllegalArgumentException("n for n-points corssover must be within range 1 to " + (this.genotype.length - 1));
        }

        // first, pick n points (the points come after the index, so only 0 to #genes - 1)
        List<Integer> availableIndices = new ArrayList<Integer>();
        for (int i = 0; i < a.genotype.length - 1; i++) {
            availableIndices.add(i);
        }

        List<Integer> crossoverPoints = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            int chosenIndex = this.rng.nextInt(availableIndices.size()); // pick a random index in to the available indices list
            crossoverPoints.add(availableIndices.get(chosenIndex)); // add it to the crossover points list
            availableIndices.remove(chosenIndex); // remove it from the available indices to be chosen
        }

        Collections.sort(crossoverPoints); // order crossovers from low to high

        boolean[] childGenotype1 = new boolean[a.genotype.length]; // TODO think we only want one child
        boolean[] childGenotype2 = new boolean[a.genotype.length];

        boolean takeFromA = true; // if we're taking genes for child1 from this or mate

        for (int i = 0; i < a.genotype.length; i++) {
            if (takeFromA) {
                childGenotype1[i] = a.genotype[i];
                childGenotype2[i] = b.genotype[i];
            } else {
                childGenotype1[i] = b.genotype[i];
                childGenotype2[i] = a.genotype[i];
            }

            // if there is a crossover points after this index, switch who children get next gene(s) from
            if (crossoverPoints.contains(i)) {
                takeFromA = !takeFromA;
            }
        }

        Solution child1 = new Solution(a.genotype.length, this.network, this.penaltyCoefficient, this.rng);
        Solution child2 = new Solution(a.genotype.length, this.network, this.penaltyCoefficient, this.rng);

        child1.genotype = childGenotype1;
        child2.genotype = childGenotype2;

        children = new ArrayList<Solution>(2);
        children.add(child1);
        children.add(child2);
    }


    @Override
    public void run() {
        this.recombine();
    }

}
