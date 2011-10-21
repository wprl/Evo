/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment1a;

/**
 *
 * @author jagwio
 */
public class Path {
    private int id;
    private int[] routers;

    public int getId() {
        return id;
    }

    public int[] getRouters() {
        return routers;
    }
    
    public Path (int id, int... routers) {
        this.id = id;
        this.routers = routers;
    }
}
