/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import assignment2c.Solution;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree node that ORs the values of its children
 * @author William Riley-Land
 */
public class PrisonerNode extends Tree<Boolean> {

    private int memory;

    public PrisonerNode(List<Tree<Boolean>> children, int depth, int memory) {
        super(children, depth);

        if (!children.isEmpty()) {
            throw new IllegalArgumentException("Prisoner nodes must be terminal nodes.");
        }

        if (memory < 0) {
            throw new IllegalArgumentException("Position in history must be greater than 0.");
        }

        this.memory = memory;
    }

    @Override
    public Boolean getValue(List<Outcome> history, Solution s) {
        return history.get(memory).getCooperationFor(s);
    }

    @Override
    public String getLabel() {
        return "P" + memory;
    }

    public int getMemory() {
        return memory;
    }

    protected PrisonerNode() {
    }

    @Override
    public PrisonerNode copy(int newDepth) {
        PrisonerNode copy = new PrisonerNode();

        copy.children = new ArrayList<Tree<Boolean>>(this.children.size());
        copy.depth = this.depth;

        for (Tree<Boolean> child : this.children) {
            copy.children.add(child.copy(newDepth + 1));
        }

        return copy;
    }
}
