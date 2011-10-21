/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2a;

/**
 * Represents one path in the network
 * @author William Riley-Land
 */
public class Path {
    private int hostId;
    private int[] routers;

    /**
     * The ID of the host in this path
     * @return
     */
    public int getHostId() {
        return hostId;
    }

    /**
     * Returns an array of the router IDs in this path
     * @return
     */
    public int[] getRouters() {
        return routers;
    }

    /**
     * Instantiate a new Path
     * @param hostId the ID of the host in this path
     * @param routers the IDs of the router(s) in this path
     */
    public Path (int hostId, int... routers) {
        this.hostId = hostId;
        this.routers = routers;
    }
}
