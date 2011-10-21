/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a.program;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the root node of a tree.  Because each node of a tree can be defined
 * as a subtree, only one class is needed to represent the tree and its nodes.
 * @author William Riley-Land
 */
public abstract class Tree<T> {

    protected List<Tree<T>> children;

    public List<Tree<T>> getChildren() {
        return children;
    }

    public Tree(List<Tree<T>> children) {
        if(children == null) {
            throw new IllegalArgumentException("Children may be empty, but not null.");
        }
        
        this.children = new ArrayList<Tree<T>>(children);
    }

    @Override
    public String toString() {
        return null;
    }

    public abstract T getValue();

    public abstract String getLabel();
}
