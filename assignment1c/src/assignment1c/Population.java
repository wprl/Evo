package assignment1c;

import assignment1c.Solution.FitnessComparator;
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
     * Generate off spring using fitness proportional parent selection
     * @param matingMethod string denoting whether to use uniform or n-point x-over
     * @param numOffspring the number of offspring produced in mating step
     * @param mutationRate probability a gene will be mutated
     * @param nPoints if using n-point crossover, n points to use
     */
    public void generateOffspringFitnessProportional(String matingMethod, int numOffspring, double mutationRate, int nPoints) {
        double totalFitness = this.getTotalFitness();

        // stochastic universal sampling algorithm (p. 63)
        // first, generate the array of probabilities to enter mating pool
        double[] a = new double[this.population.size()];
        a[0] = this.pareto.getFitnessFor(this.population.get(0)) / totalFitness;

        for (int i = 1; i < this.population.size(); i++) {
            double fitness = this.pareto.getFitnessFor(this.population.get(i));
            double probabilityOfParenthood = fitness / totalFitness;
            a[i] = a[i - 1] + probabilityOfParenthood;
        }

        List<Solution> matingPool = new ArrayList<Solution>();
        int currentMember = 0;
        int i = 0;
        double r = (rng.nextDouble() % 1) / (double) numOffspring; // random value between 0 and 1 / #offspring (lambda)
        while (currentMember < numOffspring) {
            while (r <= a[i]) {
                matingPool.add(this.population.get(i));
                r = r + (1.0 / numOffspring);
                currentMember++;
            }
            i++;
        }

        this.generateOffspring(matingMethod, matingPool, mutationRate, nPoints);
    }

    /**
     * Generate offspring using k-tournament
     * @param matingMethod string denoting whether to use uniform or n-point x-over
     * @param numOffspring the number of offspring produced in mating step
     * @param k number of solutions in tournament
     * @param mutationRate probability a gene will be mutated
     * @param nPoints if using n-point crossover, n points to use
     */
    public void generateOffspringKTournaments(String matingMethod, int numOffspring, int k, double mutationRate, int nPoints) {
        List<Solution> matingPool = this.kTournament(k, numOffspring / 2); // parents produce 2 children
        this.generateOffspring(matingMethod, matingPool, mutationRate, nPoints);
    }

    /**
     * Private method used for common mating algorithm pieces
     * @param matingMethod string denoting whether to use uniform or n-point x-over
     * @param matingPool solutions eligible for mating
     * @param mutationRate probability a gene will be mutated
     * @param nPoints if using n-point crossover, n points to use
     */
    private void generateOffspring(String matingMethod, List<Solution> matingPool, double mutationRate, int nPoints) {
        for (Solution parent1 : matingPool) {
            // for each parent, choose a random solution from the pool to mate with
            int randomIndex = this.rng.nextInt(matingPool.size());
            Solution parent2 = matingPool.get(randomIndex);

            // get the children, mutate them (possibly), and add them to the population
            Solution[] children;
            if (matingMethod.compareTo("uniform-crossover") == 0) {
                children = parent1.uniformCrossover(parent2);
            } else {
                children = parent1.nPointCrossover(parent2, nPoints);
            }

            children[0].mutate(mutationRate);
            children[1].mutate(mutationRate);

            this.population.add(children[0]);
            this.population.add(children[1]);
        }

        // calculate new Pareto fronts
        this.pareto = new ParetoUtil(this.population, 7); // TODO make omega share configurable?!
    }

    /** 
     * Perform survival step using k-tournament
     * @param k number of solutions in tournament
     */
    public void survivalKTournament(int k) {
        this.population = this.kTournament(k, this.mu);
    }

    /** Perform survival step by truncating lowest fitness solutions */
    public void survivalTruncation() {
        System.out.println("BROKEN!");
        System.exit(1);

        Collections.sort(this.population, new FitnessComparator()); // sort by low to high fitness
        Collections.reverse(this.population); // reverse (high to low)
        this.population = this.population.subList(0, this.mu); // discard least fit members, keeping the mu with the hights fitness
    }

    /**
     * Private method to perform a k-tournament
     * @param k number of solutions in tournament
     * @param length number of survivors
     * @return a list of solutions that won the tournament
     */
    private List<Solution> kTournament(int k, int length) {

        List<Solution> result = new ArrayList<Solution>();

        while (result.size() < length) {
            // pick k unique individuals
            List<Solution> kIndividuals = new ArrayList<Solution>();
            List<Solution> availableIndividuals = new ArrayList<Solution>(this.population);
            for (int i = 0; i < k; i++) {
                int randomIndex = rng.nextInt(availableIndividuals.size());
                kIndividuals.add(availableIndividuals.get(randomIndex));
                availableIndividuals.remove(randomIndex);
            }

            // find the best out of those k individuals
            Solution best = null;
            for (Solution s : kIndividuals) {
                if (best == null || this.pareto.getFitnessFor(best) < this.pareto.getFitnessFor(s)) {
                    best = s;
                }
            }

            // add the best to the pool
            result.add(best);
        }

        return result;
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
