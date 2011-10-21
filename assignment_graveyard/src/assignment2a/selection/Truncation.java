/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.selection;

import assignment2a.Population;
import assignment2a.Solution;
import assignment2a.Solution.FitnessComparator;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author jagwio
 */
public class Truncation implements ISelection {

    private Population p;
    private int n;
    private List<Solution> selected;

    /** Perform survival step by truncating lowest fitness solutions */
    @Override
    public void run() {
        System.out.println("BROKEN!");
        System.exit(1);

        Collections.sort(p, new FitnessComparator()); // sort by low to high fitness
        Collections.reverse(p); // reverse (high to low)
        this.selected = p.subList(0, n); // discard least fit members, keeping the mu with the hights fitness
    }

    @Override
    public List<Solution> getSelected() {
        return Collections.unmodifiableList(this.selected);
    }

    @Override
    public void initialize(Population p, int n) {
        this.p = p;
        this.n = n;
    }
}
