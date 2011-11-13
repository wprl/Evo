/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.task.tree;

import assignment2c.Config;
import assignment2c.tree.AndNode;
import assignment2c.tree.Tree;
import assignment2c.tree.NotNode;
import assignment2c.tree.OpponentNode;
import assignment2c.tree.OrNode;
import assignment2c.tree.PrisonerNode;
import assignment2c.tree.XorNode;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author jagwio
 */
public abstract class CreateRandomTree implements Callable<Tree<Boolean>> {

    private Config config = Config.getSingleton();
    protected int maxDepth;

    protected abstract Tree<Boolean> create(int depth) throws Exception;

    protected int numberOfChildren(int type) throws Exception {
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

    protected Tree<Boolean> getNodeFromType(int type, List<Tree<Boolean>> children, int depth) throws Exception {
        int memory; // hold the meory position of the node, if used
        switch (type) {
            case 0: // NOT
                return new NotNode(children, depth);
            case 1: // AND
                return new AndNode(children, depth);
            case 2: // OR
                return new OrNode(children, depth);
            case 3: // XOR
                return new XorNode(children, depth);
            case 4: // Opponent history value
                memory = config.getRng().nextInt(config.getHistoryLength());
                return new OpponentNode(children, depth, memory);
            case 5: // Prisoner history value
                memory = config.getRng().nextInt(config.getHistoryLength());
                return new PrisonerNode(children, depth, memory);
            default:
                throw new Exception("Compiler needs this / RNG corrupt");
        }
    }

    @Override
    public Tree<Boolean> call() throws Exception {
        return this.create(0);
    }

    public static CreateRandomTree getWorker(int maxTreeDepth) {
        CreateRandomTree worker;

        // equal probability of full and grow methods (for ramped half-and-half)
        if (Config.getSingleton().getRng().nextInt(2) == 1) {
            worker = new CreateTreeFull(maxTreeDepth);
        } else {
            worker = new CreateTreeGrow(maxTreeDepth);
        }

        return worker;
    }
}
