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
public class OpponentNode extends Tree<Boolean> {

    private int memory;

    public OpponentNode(List<Tree<Boolean>> children, int memory) {
        super(children);

        if (!children.isEmpty()) {
            throw new IllegalArgumentException("Opponent nodes must be terminal nodes.");
        }

        if (memory < 0) {
            throw new IllegalArgumentException("Position in history must be greater than 0.");
        }

        this.memory = memory;
    }

    @Override
    public Boolean getValue(List<Outcome> history) {
        return history.get(memory).getOpponentCooperates();
    }

    @Override
    public String getLabel() {
        return "O" + memory;
    }

    public int getMemory() {
        return memory;
    }
}
