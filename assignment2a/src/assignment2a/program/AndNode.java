/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.program;

import assignment2a.Outcome;
import java.util.List;

/**
 * Represents a tree node that ANDs the values of its children
 * @author William Riley-Land
 */
public class AndNode extends BinaryNode {

    public AndNode(List<Tree<Boolean>> children) {
        super(children);
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return this.children.get(0).getValue(history) && this.children.get(1).getValue(history);
    }

    @Override
    public String getLabel() {
        return "AND";
    }
}
