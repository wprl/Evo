/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import assignment2c.Config;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author subdude
 */
public class Outcome {

    private boolean prisonerCooperates; // false if defects, true if cooperates
    private boolean opponentCooperates; // false if defects, true if cooperates

    public boolean getOpponentCooperates() {
        return opponentCooperates;
    }

    public boolean getPrisonerCooperates() {
        return prisonerCooperates;
    }

    public Outcome(boolean prisoner, boolean opponent) {
        this.prisonerCooperates = prisoner;
        this.opponentCooperates = opponent;
    }

    public static List<Outcome> createRandomHistory(int length) {
        List<Outcome> history = new ArrayList<Outcome>(length);

        while (history.size() < length) {
            boolean prisoner = Config.getSingleton().getRng().nextBoolean();
            boolean opponent = Config.getSingleton().getRng().nextBoolean();
            Outcome o = new Outcome(prisoner, opponent);
            history.add(o);
        }

        return history;
    }

    public int getPayoff() {
        if (!this.prisonerCooperates && !this.opponentCooperates) {
            return 1;
        } else if (this.prisonerCooperates && this.opponentCooperates) {
            return 3;
        } else if (this.prisonerCooperates && !this.opponentCooperates) {
            return 0;
        } else { // if (!this.prisonerCooperates && this.opponentCooperates)
            return 5;
        }
    }
}
