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

    public SubTreeRecombination(Solution a, Solution b) {
        this.a = a.copy();
        this.b = b.copy();
    }

    @Override
    public List<Solution> call() throws Exception {
        Tree<Boolean> subtreeA = a.getTree().randomSubTree();
        Tree<Boolean> subtreeB = b.getTree().randomSubTree();

        // replace the subtrees in the *copies* of a and b and use them as children
        a.getTree().replace(subtreeA, subtreeB);
        b.getTree().replace(subtreeB, subtreeA);

        List<Solution> children = new ArrayList<Solution>(2);
        children.add(a);
        children.add(b);

        return children;
    }
}
