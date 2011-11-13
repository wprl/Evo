/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c;

import assignment2c.task.EvaluateFitness;
import assignment2c.task.tree.CreateRandomTree;
import assignment2c.task.SubTreeMutation;
import assignment2c.task.SubTreeRecombination;
import assignment2c.task.selection.SelectTree;
import assignment2c.tree.Tree;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class Population {

    private Config config = Config.getSingleton();
    private List<Solution> solutions;
    private int totalNumberOfEvaluations = 0;

    public Population() throws ExecutionException, InterruptedException, TimeoutException {
        this.solutions = createRandomForest();
        this.updateFitness(this.solutions, this.solutions);
    }

    private Population(List<Solution> solutions, int totalNumberOfEvaluations) {
        this.solutions = solutions;
        this.totalNumberOfEvaluations = totalNumberOfEvaluations;
    }

    public Population nextGeneration(SelectTree parentSelector, SelectTree survivalSelector) throws InstantiationException, IllegalAccessException, IllegalAccessException, InterruptedException, ExecutionException, TimeoutException {
        List<Solution> parents = parentSelector.select(config.getNumberOfChildren(), this.solutions);

        List<Solution> children = mutate(mate(parents));
        this.solutions.addAll(children);

        this.updateFitness(children, this.solutions); // set fitnesses for children

        List<Solution> survivors;
        if (config.getSurvivalStrategy() == Config.SurvivalStrategy.COMMA) {
            // comma survival strategy -- select survivors from children (all parents die)
            survivors = survivalSelector.select(config.getPopulationSize(), children);
        } else {
            // plus survival strategy -- select survivors from parents and children
            survivors = survivalSelector.select(config.getPopulationSize(), this.solutions);
        }

        return new Population(survivors, this.totalNumberOfEvaluations);
    }

    private <T> List<T> runWorkers(List workers) throws InterruptedException, ExecutionException {
        List<Future> futures = config.getExec().invokeAll(workers);
        List<T> results = new ArrayList<T>(workers.size());

        for (int i = 0; i < workers.size(); i++) {
            T result = (T) futures.get(i).get();
            results.add(result);
        }

        return results;
    }

    private List<Solution> createRandomForest() throws ExecutionException, InterruptedException, TimeoutException {
        // Use ramped half-and-half to intialize forest
        List<CreateRandomTree> workers = new ArrayList<CreateRandomTree>(config.getPopulationSize());

        for (int i = 0; i < config.getPopulationSize(); i++) {
            workers.add(CreateRandomTree.getWorker(config.getMaxTreeDepth()));
        }

        // get raw trees
        List<Tree<Boolean>> forest = this.runWorkers(workers);

        // convert to Solutions
        List<Solution> forestSolutions = new ArrayList<Solution>(config.getPopulationSize());

        for (Tree<Boolean> tree : forest) {
            forestSolutions.add(new Solution(tree));
        }

        return forestSolutions;
    }

    private void updateFitness(List<Solution> forest, List<Solution> opponents) throws InterruptedException, ExecutionException {
        // evaluate fitness of each tree in forest
        List<EvaluateFitness> workers = new ArrayList<EvaluateFitness>(forest.size());

        for (int i = 0; i < forest.size(); i++) {
            EvaluateFitness worker = new EvaluateFitness(forest.get(i), opponents);
            workers.add(worker);
        }

        List<Double> scores = this.runWorkers(workers);

        for (int i = 0; i < scores.size(); i++) {
            Solution s = forest.get(i);

            double score = scores.get(i);
            s.setFitness(score);
        }

        this.totalNumberOfEvaluations += forest.size() * config.getCoevolutionarySampleSize();
    }

    private List<Solution> mate(List<Solution> parents) throws ExecutionException, InterruptedException, TimeoutException {
        // create the forest
        List<SubTreeRecombination> workers = new ArrayList<SubTreeRecombination>(config.getNumberOfChildren());
        for (int g = 0; g < config.getNumberOfChildren() / 2; g++) {
            Solution a = parents.get(config.getRng().nextInt(parents.size())); // chance parents will be same (makes clones)
            Solution b = parents.get(config.getRng().nextInt(parents.size()));
            SubTreeRecombination worker = new SubTreeRecombination(a, b);
            workers.add(worker);
        }

        List<List<Solution>> childPairs = this.runWorkers(workers);
        List<Solution> children = new ArrayList<Solution>(childPairs.size() * 2);

        for (List<Solution> pair : childPairs) {
            children.addAll(pair);
        }

        return children;
    }

    private List<Solution> mutate(List<Solution> children) throws ExecutionException, InterruptedException, TimeoutException {
        // create the forest
        List<SubTreeMutation> workers = new ArrayList<SubTreeMutation>(children.size());

        for (int i = 0; i < children.size(); i++) {
            SubTreeMutation worker = new SubTreeMutation(children.get(i));
            workers.add(worker);
        }

        return this.runWorkers(workers); // return mutants
    }

    public int getTotalNumberOfEvaluations() {
        return totalNumberOfEvaluations;
    }

    public Solution getBest() {
        Solution best = null;

        for (Solution s : this.solutions) {
            if (best == null || s.getFitness() > best.getFitness()) {
                best = s;
            }
        }
        return best;
    }

    public double getAverageFitness() {
        double sum = 0.0;

        for (Solution s : this.solutions) {
            sum += s.getFitness();
        }
        return sum / this.solutions.size();
    }
}
