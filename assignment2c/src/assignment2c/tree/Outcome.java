/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import assignment2c.Config;
import assignment2c.Solution;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author subdude
 */
public class Outcome {

    private Map<Solution, Boolean> cooperation;

    public Outcome(Solution a, Solution b, boolean aCooperates, boolean bCooperates) {
        this.cooperation = new HashMap<Solution, Boolean>(2);
        this.cooperation.put(a, aCooperates);
        this.cooperation.put(b, bCooperates);
    }

    public boolean getCooperationFor(Solution s) {
        return this.cooperation.get(s);
    }

    public Solution getOpponent(Solution s) {
        for (Solution other : this.cooperation.keySet()) {
            if (other == s) {
                continue; // not really other
            }

            return other;
        }

        // playing against self
        return s;
    }

    public static List<Outcome> createRandomHistory(Solution a, Solution b, int length) {
        List<Outcome> history = new ArrayList<Outcome>(length);

        while (history.size() < length) {
            boolean aCooperates = Config.getSingleton().getRng().nextBoolean();
            boolean bCooperates = Config.getSingleton().getRng().nextBoolean();
            Outcome o = new Outcome(a, b, aCooperates, bCooperates);
            history.add(o);
        }

        return history;
    }

    public int getPayoffFor(Solution s) {
        Solution opponent = this.getOpponent(s);

        if (!this.cooperation.get(s) && !this.cooperation.get(opponent)) {
            return 1;
        } else if (this.cooperation.get(s) && this.cooperation.get(opponent)) {
            return 3;
        } else if (this.cooperation.get(s) && !this.cooperation.get(opponent)) {
            return 0;
        } else { // if (!this.cooperation.get(s) && this.cooperation.get(opponent))
            return 5;
        }
    }
}
