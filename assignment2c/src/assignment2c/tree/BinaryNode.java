/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import java.util.List;

/**
 * Represents a node in a tree that performs a binary operation on its two children
 * @author William Riley-Land
 */
public abstract class BinaryNode extends Tree<Boolean> {

    protected BinaryNode() {}

    public BinaryNode(List<Tree<Boolean>> children, int depth) {
        super(children, depth);

        if (children.size() != 2 || children.get(0) == null || children.get(1) == null) {
            throw new IllegalArgumentException("Binary operators must use exactly two operands!");
        }
    }
}
