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
public class OrNode extends BinaryNode {

    public OrNode(List<Tree<Boolean>> children, int depth) {
        super(children, depth);
    }

    @Override
    public String getLabel() {
        return "OR";
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return this.children.get(0).getValue(history) || this.children.get(1).getValue(history);
    }

    protected OrNode() {
    }

    @Override
    public OrNode copy() {
        OrNode copy = new OrNode();

        copy.children = new ArrayList<Tree<Boolean>>(this.children.size());
        copy.depth = this.depth;

        for (Tree<Boolean> child : this.children) {
            copy.children.add(child.copy());
        }

        return copy;
    }
}
