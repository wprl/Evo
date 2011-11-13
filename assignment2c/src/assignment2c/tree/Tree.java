/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2c.tree;

import assignment2c.Config;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root node of a tree.  Because each node of a tree can be defined
 * as a subtree, only one class is needed to represent the tree and its nodes.
 * @author William Riley-Land
 */
public abstract class Tree<T> {

    private Config config = Config.getSingleton();
    protected List<Tree<T>> children;
    protected int depth;
    protected List<Tree<T>> allNodesInTree;

    public abstract T getValue(List<Outcome> history);

    public abstract String getLabel();

    public abstract Tree<T> copy();

    protected Tree() {
    }

    @Override
    public String toString() {
        return this.toPreOrderFormat();
    }

    public List<Tree<T>> getChildren() {
        return children;
    }

    public int getDepth() {
        return depth;
    }

    public Tree(List<Tree<T>> children, int depth) {
        if (children == null) {
            throw new IllegalArgumentException("Children may be empty, but not null.");
        }

        if (depth < 0) {
            throw new IllegalArgumentException("Depth must be 0 or greater");
        }

        this.children = new ArrayList<Tree<T>>(children);
        this.depth = depth;
    }

    public String toPreOrderFormat() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.getLabel());
        sb.append(" ");

        for (Tree<T> child : this.children) {
            sb.append(child.toPreOrderFormat());
        }

        return sb.toString();
    }

    public Tree<T> randomSubTree() {
        int randomIndex = config.getRng().nextInt(this.getAllnodesInTree().size());
        return this.getAllnodesInTree().get(randomIndex);
    }

    public void replace(Tree<T> subtree, Tree<T> newTree) {
        if (this.children.contains(subtree)) {
            this.children.remove(subtree);
            this.children.add(newTree);
        } else {
            for (Tree<T> child : this.children) {
                child.replace(subtree, newTree);
            }
        }
    }

    protected final List<Tree<T>> getAllnodesInTree() {
        if (this.allNodesInTree == null) {

            List<Tree<T>> temp = new ArrayList<Tree<T>>();
            temp.add(this);

            for (Tree<T> child : this.children) {
                temp.addAll(child.getAllnodesInTree());
            }

            this.allNodesInTree = temp;
        }
        return allNodesInTree;
    }

    public int getTreeDepth() {
        int maxChildDepth = 0;
        for (Tree<T> child : this.children) {
            if (child.getDepth() > maxChildDepth) {
                maxChildDepth = child.getDepth();
            }
        }

        return maxChildDepth > this.getDepth() ? maxChildDepth : this.getDepth();
    }
}
