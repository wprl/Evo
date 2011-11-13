/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task.selection;

import assignment2c.Config;
import assignment2c.Solution;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class KTournament extends SelectTree<Solution> {

    private Config config = Config.getSingleton();

    @Override
    public List<Solution> select(int numberToSelect, List<Solution> pool) {
        List<Solution> result = new ArrayList<Solution>();

        while (result.size() < numberToSelect) {

            // pick k unique individuals
            List<Solution> kIndividuals = new ArrayList<Solution>(config.getKInTournament());
            List<Solution> availableIndividuals = new ArrayList<Solution>(pool);
            for (int i = 0; i < config.getKInTournament(); i++) {
                int randomIndex = config.getRng().nextInt(availableIndividuals.size());
                kIndividuals.add(availableIndividuals.get(randomIndex));
                availableIndividuals.remove(randomIndex);
            }

            // find the best out of those k individuals
            Solution best = null;
            for (Solution t : kIndividuals) {
                if (best == null || best.getFitness() < t.getFitness()) {
                    best = t;
                }
            }

            result.add(best);
        }

        return result;
    }
}
