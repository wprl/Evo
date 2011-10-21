/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.mating.recombination;

import assignment2a.Solution;
import java.util.List;

/**
 *
 * @author jagwio
 */
public interface IRecombinator {
    public void recombine();
    public List<Solution> getChildren();
}
