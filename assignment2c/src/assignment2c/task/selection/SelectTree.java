/*
 * 
 * Copyright 2011 William Riley-Land, all rights reserved.
 * 
 */
package assignment2c.task.selection;

import assignment2c.Config;
import java.util.List;
import java.util.Random;

/**
 *
 * @author William Riley-Land <william.riley.land@gmail.com>
 */
public abstract class SelectTree<T> {

    public abstract List<T> select(int numberToSelect, List<T> pool);
}
