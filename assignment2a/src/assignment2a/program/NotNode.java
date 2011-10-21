/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.program;

import java.util.List;

/**
 * Represents a node in a tree that performs the negation operation on its child
 * @author William Riley-Land
 */
public class NotNode extends Tree<Boolean> {

    public NotNode(List<Tree<Boolean>> children) {
        super(children);

        if (children.size() != 1 || children.get(0) == null) {
            throw new IllegalArgumentException("NOT operates on one operand!");
        }
    }

    @Override
    public Boolean getValue() {
        return !this.children.get(0).getValue(); // contract in constructor, so this is safe
    }
}
