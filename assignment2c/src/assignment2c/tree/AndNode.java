/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree node that ANDs the values of its children
 * @author William Riley-Land
 */
public class AndNode extends BinaryNode {

    public AndNode(List<Tree<Boolean>> children, int depth) {
        super(children, depth);
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return this.children.get(0).getValue(history) && this.children.get(1).getValue(history);
    }

    @Override
    public String getLabel() {
        return "AND";
    }

    protected AndNode() {
    }

    @Override
    public AndNode copy() {
        AndNode copy = new AndNode();

        copy.children = new ArrayList<Tree<Boolean>>(this.children.size());
        copy.depth = this.depth;

        for (Tree<Boolean> child : this.children) {
            copy.children.add(child.copy());
        }

        return copy;
    }
}
