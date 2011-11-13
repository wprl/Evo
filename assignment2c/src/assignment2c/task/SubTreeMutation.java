/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task;

import assignment2c.Config;
import assignment2c.Solution;
import assignment2c.task.tree.CreateRandomTree;
import assignment2c.tree.Tree;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class SubTreeMutation implements Callable<Solution> {

    private Solution individual;
    private Config config = Config.getSingleton();

    public SubTreeMutation(Solution individual) {
        this.individual = individual;
    }

    @Override
    public Solution call() throws Exception {
        if (config.getRng().nextDouble() <= config.getMutationRate()) {
            this.individual = this.individual.copy();
            Tree<Boolean> subtree = individual.getTree().randomSubTree();
            int maxSubTreeDepth = config.getMaxTreeDepth() - subtree.getDepth();

            CreateRandomTree worker = CreateRandomTree.getWorker(maxSubTreeDepth);
            Tree<Boolean> newTree = worker.call();

            individual.getTree().replace(subtree, newTree);
        }

        return individual; // return the individual or its mutated copy
    }
}
