package assignment2a;

import assignment2a.Solution.FitnessComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class representing a population of solutions
 * @author William Riley-Land
 */
public class Population {

    /**
     * current population (may include offspring)
     */
    private List<Solution> population = new ArrayList<Solution>();
    /**
     * mu - the number of offspring generated each generation
     */
    private int mu;
    /**
     * "global" random number generator
     */
    private Random rng;
    private ParetoUtil pareto;

    /**
     *
     * @param size the size of the population
     * @param numRouters the total number of routers in the network
     * @param network an array of paths (hosts and routers)
     * @param penaltyCoefficient
     * @param rng "global" random number generator
     */
    public Population(int size, int numRouters, Path[] network, double penaltyCoefficient, Random rng) {
        this.rng = rng;
        this.mu = size;

        for (int i = 0; i < size; i++) {
            Solution s = new Solution(numRouters, network, penaltyCoefficient, rng);
            s.randomize();
            this.population.add(s);
        }

        // initialize the Pareto front util with initial population.  Pareto fronts are recreated after offspring are generated
        this.pareto = new ParetoUtil(this.population, 7); // TODO make omega share configurable?!
    }

    

    /** 
     * Perform survival step using k-tournament
     * @param k number of solutions in tournament
     */
    public void survivalKTournament(int k) {
        this.population = this.kTournament(k, this.mu);
    }




    }

    /**
     * Get the best member of the current population
     * Fitness is not calculated via Pareto fronts
     * @return the best member
     */
    public Solution getBest() {
        Solution best = this.population.get(0);

        for (int i = 1; i < this.population.size(); i++) {
            Solution current = this.population.get(i);
            if (current.getFitness() > best.getFitness()) {
                best = current;
            }
        }

        return best;
    }

    /**
     *  Get the total of all the current population's fitnesses
     * @return total fitness
     */
    public double getTotalFitness() {
        double totalFitness = 0;

        for (Solution s : this.population) {
            totalFitness += this.pareto.getFitnessFor(s);
        }

        return totalFitness;
    }

    /**
     * Get average fitness of current population
     * @return mean fitness
     */
    public double getAverageFitness() {
        return this.getTotalFitness() / (double) this.population.size();
    }

    public ParetoFront getFirstFront() {
        return this.pareto.getFront(0);
    }
}
