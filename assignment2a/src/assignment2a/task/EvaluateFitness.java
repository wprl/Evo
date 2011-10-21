/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.task;

import assignment2a.program.Tree;

/**
 *
 * @author jagwio
 */
public class EvaluateFitness implements Runnable {

    private Tree<Boolean> tree;
    private Double fitness;

    public EvaluateFitness(Tree<Boolean> tree) {
        this.tree = tree;
    }

    @Override
    public void run() {
        this.fitness = 0.0;
    }

    public Double getFitness() {
        return fitness;
    }

    public Tree<Boolean> getTree() {
        return tree;
    }

}
