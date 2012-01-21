/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task;

import assignment2c.Config;
import assignment2c.Solution;
import assignment2c.tree.Tree;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class SubTreeRecombination implements Callable<List<Solution>> {

    Solution a;
    Solution b;
    Config config = Config.getSingleton();

    public SubTreeRecombination(Solution a, Solution b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public List<Solution> call() throws Exception {
        List<Solution> children = null;
        boolean firstTry = true;
        
        while (firstTry || a.getTree().getTreeDepth() > config.getMaxTreeDepth() || b.getTree().getTreeDepth() > config.getMaxTreeDepth()) {
            Solution aCopy = this.a.copy();
            Solution bCopy = this.b.copy();

            Tree<Boolean> subtreeA = aCopy.getTree().randomSubTree();
            Tree<Boolean> subtreeB = bCopy.getTree().randomSubTree();

            // replace the subtrees in the *copies* of a and b and use them as children
            a.getTree().replace(subtreeA, subtreeB);
            b.getTree().replace(subtreeB, subtreeA);

            children = new ArrayList<Solution>(2);
            children.add(aCopy);
            children.add(bCopy);

            firstTry = false;
        }

        return children;
    }
}
