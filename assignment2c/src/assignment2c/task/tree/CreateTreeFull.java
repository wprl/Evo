/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task.tree;

import assignment2c.Config;
import assignment2c.tree.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public class CreateTreeFull extends CreateRandomTree {

    private Config config = Config.getSingleton();

    public CreateTreeFull(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    protected Tree<Boolean> create(int depth) throws Exception {
        // First pick what kind of node this will be
        int type;
        if (depth < this.maxDepth) {
            type = config.getRng().nextInt(4); // may only be functional node
        } else {
            type = config.getRng().nextInt(2) + 4; // may only be terminal node
        }

        // Next, create its children recursively
        int childCount = this.numberOfChildren(type);
        List<Tree<Boolean>> children = new ArrayList<Tree<Boolean>>(childCount);
        for (int c = 0; c < childCount; c++) {
            children.add(this.create(depth + 1));
        }

        // Finally, create and return the node
        return getNodeFromType(type, children, depth);
    }
}
