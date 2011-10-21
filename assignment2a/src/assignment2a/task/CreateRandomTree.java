/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.task;

import assignment2a.program.AndNode;
import assignment2a.program.NotNode;
import assignment2a.program.OpponentNode;
import assignment2a.program.OrNode;
import assignment2a.program.PrisonerNode;
import assignment2a.program.Tree;
import assignment2a.program.XorNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author jagwio
 */
public class CreateRandomTree implements Runnable {

    private Random rng;
    private int maxDepth;
    private int depth = 0;
    private Tree<Boolean> tree;
    
    public CreateRandomTree(Random rng, int maxDepth) {
        this.rng = rng;
        this.maxDepth = maxDepth;
    }

    public Tree<Boolean> getTree() {
        return tree;
    }

    private Tree<Boolean> create(int depth) {
        // First pick what kind of node this will be
        int type;

        if (depth != this.maxDepth) {
            type = rng.nextInt(6);
        } else {
            type = rng.nextInt(2) + 4; // may only be terminal node
        }

        // Next, create its children
        List<Tree<Boolean>> children = new ArrayList<Tree<Boolean>>(2); // 2 elements max
        switch (type) {
            case 0: // NOT
                Tree<Boolean> child = create(depth + 1);
                children.add(child);
                break;
            case 1: // AND
            case 2: // OR
            case 3: // XOR
                Tree<Boolean> child1 = create(depth + 1);
                Tree<Boolean> child2 = create(depth + 1);
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
                return new OpponentNode(children, opponentHistory); // TODO in here or in fitness evaluator?  // pass both in to FitnessEval?
            case 5: // Prisoner history value
                return new PrisonerNode(children, prisonerHistory);
            default:
                throw new IllegalStateException("very BAD");
        }
    }

    @Override
    public void run() {
        this.tree = this.create(0);
    }
}
