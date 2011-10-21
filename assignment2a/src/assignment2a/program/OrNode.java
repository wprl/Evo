/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.program;

import java.util.List;

/**
 * Represents a tree node that ORs the values of its children
 * @author William Riley-Land
 */
public class OrNode extends BinaryNode {

    public OrNode(List<Tree<Boolean>> children) {
        super(children);
    }

    @Override
    public Boolean getValue() {
        return this.children.get(0).getValue() || this.children.get(1).getValue();
    }
}
