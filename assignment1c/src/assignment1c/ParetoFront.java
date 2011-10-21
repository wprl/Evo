package assignment1c;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Pareto front of solutions
 * @author William Riley-Land
 */
public class ParetoFront {

    private List<Solution> members;

    public ParetoFront(List<Solution> members) {
        this.members = members;
    }

    public List<Solution> getMembers() {
        return members;
    }

    public int count() {
        return this.members.size();
    }

    /**
     * Generate a string representing all solutions in this front
     * @param routerIdOffset beginning router ID
     * @return solution string
     */
    public String toSolutionString(int routerIdOffset) {
        StringBuilder s = new StringBuilder();

        for (Solution m : this.members) {
            s.append(m.toSolutionString(routerIdOffset));
            s.append("\n");
        }

        return s.toString();
    }

    /**
     * Write this solution to a file in format appropriate for assignment deliverables
     * @param path location to write file
     * @param routerIdOffset starting id for routers (IDs are 0-based internally)
     */
    public void writeToFile(String path, int routerIdOffset) {
        File file = new File(path);
        String solutionString = this.toSolutionString(routerIdOffset);

        try {
            file.createNewFile();
            FileWriter out = new FileWriter(file);
            out.write(solutionString);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("ERROR: failed to write solution file.");
            System.exit(1);
        }
    }
}
