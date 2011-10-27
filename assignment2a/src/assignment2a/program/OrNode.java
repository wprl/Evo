/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.program;

import assignment2a.Outcome;
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
    public String getLabel() {
        return "OR";
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return this.children.get(0).getValue(history) || this.children.get(1).getValue(history);
    }
}
