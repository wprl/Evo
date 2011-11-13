/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import assignment2c.Solution;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a node in a tree that performs the negation operation on its child
 * @author William Riley-Land
 */
public class NotNode extends Tree<Boolean> {

    public NotNode(List<Tree<Boolean>> children, int depth) {
        super(children, depth);

        if (children.size() != 1 || children.get(0) == null) {
            throw new IllegalArgumentException("NOT operates on one operand!");
        }
    }

    @Override
    public Boolean getValue(List<Outcome> history, Solution s) {
        return !this.children.get(0).getValue(history, s); // contract in constructor, so this is safe
    }

    @Override
    public String getLabel() {
        return "NOT";
    }

        protected NotNode() {
    }

    @Override
    public NotNode copy() {
        NotNode copy = new NotNode();

        copy.children = new ArrayList<Tree<Boolean>>(this.children.size());
        copy.depth = this.depth;

        for (Tree<Boolean> child : this.children) {
            copy.children.add(child.copy());
        }

        return copy;
    }
}
