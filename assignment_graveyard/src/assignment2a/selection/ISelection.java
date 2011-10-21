/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.selection;

import assignment2a.Population;
import assignment2a.Solution;
import java.util.List;

/**
 *
 * @author jagwio
 */
public interface ISelection extends Runnable {
    public void initialize(Population, int);
    public List<Solution> getSelected();
}
