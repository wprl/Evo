/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.program;

import assignment2a.Config;
import assignment2a.Outcome;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Represents the root node of a tree.  Because each node of a tree can be defined
 * as a subtree, only one class is needed to represent the tree and its nodes.
 * @author William Riley-Land
 */
public abstract class Tree<T> {

    protected List<Tree<T>> children;

    public List<Tree<T>> getChildren() {
        return children;
    }

    public Tree(List<Tree<T>> children) {
        if (children == null) {
            throw new IllegalArgumentException("Children may be empty, but not null.");
        }

        this.children = new ArrayList<Tree<T>>(children);
    }

    private String toPreOrderFormat() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getLabel());
        sb.append(" ");

        for (Tree<T> child : this.children) {
            sb.append(child.toPreOrderFormat());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toPreOrderFormat();
    }

    public abstract T getValue(List<Outcome> history);

    public abstract String getLabel();

    public static <T> List<Tree<T>> createRandomForest(final Config config, final Random rng) throws ExecutionException, InterruptedException, TimeoutException {
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);

        // create the forest
        List<Callable<Tree<T>>> workers = new ArrayList<Callable<Tree<T>>>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            //CreateRandomTree worker = new CreateRandomTree(rng, config.getMaxTreeDepth(), config.getHistoryLength());
            //workers.add(worker);
            workers.add(new Callable<Tree<T>>() {

                private Tree<T> create(int depth) throws Exception {

                    // First pick what kind of node this will be
                    int type;
                    if (depth < config.getMaxTreeDepth()) {
                        type = rng.nextInt(6);
                    } else {
                        type = rng.nextInt(2) + 4; // may only be terminal node
                    }

                    // Next, create its children recursively
                    int childCount = numberOfChildren(type);
                    List<Tree<T>> children = new ArrayList<Tree<T>>(childCount);
                    for (int c = 0; c < childCount; c++) {
                        children.add(this.create(depth + 1));
                    }

                    // Finally, create and return the node
                    return getNodeFromType(type, children);
                }

                @Override
                public Tree<T> call() {
                    Tree<T> newTree = null;
                    try {
                        newTree = this.create(0);
                    } catch (Exception e) {
                        System.out.println("Fatal error while creating a tree");
                        System.exit(1);
                    }
                    return newTree;
                }

                private int numberOfChildren(int type) throws Exception {
                    int numberOfChildren;

                    switch (type) {
                        case 0: // NOT
                            numberOfChildren = 1;
                            break;
                        case 1: // AND
                        case 2: // OR
                        case 3: // XOR
                            numberOfChildren = 2;
                            break;
                        case 4: // Opponent history value
                        case 5: // Prisoner history value
                            numberOfChildren = 0;
                            break;
                        default:
                            throw new Exception("Compiler needs this / RNG corrupt");
                    }

                    return numberOfChildren;
                }

                private Tree<T> getNodeFromType(int type, List<Tree<T>> children) throws Exception {
                    int memory; // hold the meory position of the node, if used
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
                            memory = rng.nextInt(config.getHistoryLength());
                            return new OpponentNode(children, memory);
                        case 5: // Prisoner history value
                            memory = rng.nextInt(config.getHistoryLength()); // TODO pass MAX instead of value?  (where to get rng in ndoe class
                            return new PrisonerNode(children, memory);
                        default:
                            throw new Exception("Compiler needs this / RNG corrupt");
                    }
                }
            });
        }

        List<Future<Tree<T>>> futures = exec.invokeAll(workers);

        // harvest forest values
        List<Tree<T>> forest = new ArrayList<Tree<T>>(config.getNumGenerations());
        for (int g = 0; g < config.getNumGenerations(); g++) {
            Tree<T> tree = futures.get(g).get();
            forest.add(tree);
        }

        exec.shutdown();

        return forest;
    }
}
