package assignment2a;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class representing the assignment input/network file
 * @author William Riley-Land
 */
public class InputFile {
    
    private Path[] network;
    private int pathCount;
    private int routerCount;

    /**
     * Gets the paths in the network
     * @return array of paths in the network
     */
    public Path[] getNetwork() {
        return network;
    }

    /**
     * Number of paths in the network
     * @return number of paths
     */
    public int getPathCount() {
        return pathCount;
    }

    /**
     * Number of routers in the network
     * @return
     */
    public int getRouterCount() {
        return routerCount;
    }

    /**
     * Instantiate a new input file
     * @param path path to read
     */
    public InputFile(String path) {
        network = this.read(path);
    }

    /**
     * Private method used to read the input file and instantiate network
     * @param path to file to read
     * @return an array of all paths in the network file
     */
    private Path[] read(String path) {
        File file = new File(path);

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: input file not found");
            System.exit(1);
        }

        scanner.nextLine(); // skip "Number of hosts/paths:"
        pathCount = Integer.parseInt(scanner.nextLine());
        scanner.nextLine(); // skip "Number of routers:"
        routerCount = Integer.parseInt(scanner.nextLine());
        scanner.nextLine(); // skip "Paths:"

        Path[] inputNetwork = new Path[pathCount];

        int networkIndex = 0;
        while (scanner.hasNextLine()) {
            String[] elements = scanner.nextLine().split("[ ]"); // split the line into IDs separated by spaces
            int pathId = Integer.parseInt(elements[0]);
            int[] routerIds = new int[elements.length - 1];

            for (int i = 1; i < elements.length; i++) { // skip the path ID and convert the router IDs to ints
                routerIds[i - 1] = Integer.parseInt(elements[i]) - pathCount; // start router IDs from 0 internally
            }

            Path p = new Path(pathId, routerIds);
            inputNetwork[networkIndex] = p;

            networkIndex++;
        }

        scanner.close();
        return inputNetwork;
    }
}
