/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.task;

import assignment2a.Outcome;
import assignment2a.program.Tree;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author jagwio
 */
public class EvaluateFitness implements Callable<Double> {

    private Tree<Boolean> tree;
    private Double fitness;
    private List<Outcome> history;
    private int numberOfGames;

    public EvaluateFitness(Tree<Boolean> tree, List<Outcome> history, int numberOfGames) {
        this.tree = tree;
        this.history = history;
        this.numberOfGames = numberOfGames;
    }

    private static int calculatePayoff(boolean prisonerCooperates, boolean opponentCooperates) {
        if (!prisonerCooperates && !opponentCooperates) {
            return 1;
        } else if (prisonerCooperates && opponentCooperates) {
            return 3;
        } else if (prisonerCooperates && !opponentCooperates) {
            return 0;
        } else { // if (!prisonerCooperates && opponentCooperates)
            return 5;
        }
    }

    private void updateHistory(boolean prisonerCooperates, boolean opponentCooperates) {
        Outcome outcome = new Outcome(prisonerCooperates, opponentCooperates);
        this.history.add(0, outcome); // add newest element
        this.history.remove(this.history.size() - 1); // remove oldest element
    }

    @Override
    public Double call() {
        int sumPayoffs = 0;

        for (int g = 0; g < numberOfGames; g++) {
            boolean prisonerCooperates = this.tree.getValue(this.history);
            boolean opponentCooperates = this.history.get(0).getPrisonerCooperates(); // opponent mimics prisoner's last move (tit-for-tat)
            sumPayoffs += calculatePayoff(prisonerCooperates, opponentCooperates);
            this.updateHistory(prisonerCooperates, opponentCooperates);
        }

        return (double) sumPayoffs / (double) this.numberOfGames;
    }
}
