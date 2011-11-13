/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree node that XORs the values of its children
 * @author William Riley-Land
 */
public class XorNode extends BinaryNode {

    public XorNode(List<Tree<Boolean>> children, int depth) {
        super(children, depth);
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return this.children.get(0).getValue(history) ^ this.children.get(1).getValue(history);
    }

    @Override
    public String getLabel() {
        return "XOR";
    }

    protected XorNode() {
    }

    @Override
    public XorNode copy() {
        XorNode copy = new XorNode();

        copy.children = new ArrayList<Tree<Boolean>>(this.children.size());
        copy.depth = this.depth;

        for (Tree<Boolean> child : this.children) {
            copy.children.add(child.copy());
        }

        return copy;
    }
}