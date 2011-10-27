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
public class PrisonerNode extends Tree<Boolean> {

    private int memory;

    public PrisonerNode(List<Tree<Boolean>> children, int memory) {
        super(children);

        if (!children.isEmpty()) {
            throw new IllegalArgumentException("Prisoner nodes must be terminal nodes.");
        }

        if (memory < 0) {
            throw new IllegalArgumentException("Position in history must be greater than 0.");
        }

        this.memory = memory;
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return history.get(memory).getPrisonerCooperates();
    }

    @Override
    public String getLabel() {
        return "P" + memory;
    }

    public int getMemory() {
        return memory;
    }
}
