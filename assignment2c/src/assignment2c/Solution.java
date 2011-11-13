package assignment2c;

import assignment2c.tree.Tree;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class used to repesent a solution
 * @author William Riley-Land
 */
public class Solution {

    private Tree<Boolean> tree;
    private Double fitness;

    public double getFitness() {
        if (fitness == null) {
            System.out.println("Fitness was accessed but had not been set!");
            System.exit(1);
        }
        return fitness;
    }

    public void setFitness(double fitness) {
        if (this.fitness != null) {
            System.out.println("Fitness may be set only once!");
            System.exit(1);
        }
        this.fitness = fitness;
    }

    public boolean isFitnessSet() {
        return this.fitness != null;
    }

    public Tree<Boolean> getTree() {
        return tree;
    }

    public Solution(Tree<Boolean> tree) {
        this.tree = tree;
    }

    public Solution copy() {
        Solution copy = new Solution(this.tree.copy());
        return copy;
    }

    public void writeToPath(String path) {
        FileWriter out;
        File file = new File(path);

        try {
            file.createNewFile();
            out = new FileWriter(file);
            out.write(this.tree.toPreOrderFormat());
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR: Couldn't write solution file: " + e.getMessage());
            System.exit(1);
        }
    }
}
