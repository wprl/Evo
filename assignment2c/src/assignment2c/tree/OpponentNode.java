/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree node that ORs the values of its children
 * @author William Riley-Land
 */
public class OpponentNode extends Tree<Boolean> {

    private int memory;

    public OpponentNode(List<Tree<Boolean>> children, int depth, int memory) {
        super(children, depth);

        if (!children.isEmpty()) {
            throw new IllegalArgumentException("Opponent nodes must be terminal nodes.");
        }

        if (memory < 0) {
            throw new IllegalArgumentException("Position in history must be greater than 0.");
        }

        this.memory = memory;
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return history.get(memory).getOpponentCooperates();
    }

    @Override
    public String getLabel() {
        return "O" + memory;
    }

    public int getMemory() {
        return memory;
    }

    protected OpponentNode() {
    }

    @Override
    public OpponentNode copy() {
        OpponentNode copy = new OpponentNode();

        copy.children = new ArrayList<Tree<Boolean>>(this.children.size());
        copy.depth = this.depth;

        for (Tree<Boolean> child : this.children) {
            copy.children.add(child.copy());
        }

        return copy;
    }
}
