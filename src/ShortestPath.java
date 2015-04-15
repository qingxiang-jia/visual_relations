import java.util.*;

/**
 * Given pruned spatial relationships between buildings, use Dijkstra algorithm to find the shortest path from
 * any building to any building. This pre-computes all shortest paths for all possible source and target
 * combination.
 * Buildings are represented by integers from 0 to 26.
 * Citation: http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
 */
public class ShortestPath
{
    boolean[][] north, south, west, east;
    int[][][] paths; // the shortest paths: paths[src][tgt][nodes in between]
    int numOfNodes;

    public ShortestPath(boolean[][] north, boolean[][] south, boolean[][] west, boolean[][] east)
    {
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
        numOfNodes = north.length;
        paths = new int[numOfNodes][numOfNodes][];
    }

    /**
     * For all sources and for all targets, compute the shortest path.
     */
    public void findPathForAll()
    {
        for (int source = 0; source < numOfNodes; source++)
            runDijkstra(source);
        for (int source = 0; source < numOfNodes; source++)
            for (int target = 0; target < numOfNodes; target++) {
                System.out.printf("%d -> %d  ", source, target);
                System.out.println(Arrays.toString(paths[source][target]));
            }
    }

    /**
     * Classic Dijkstra's algorithm that computes the shortest paths from source to each building in the map.
     * @param source
     */
    private void runDijkstra(int source)
    {
        int[] dist = new int[numOfNodes];
        dist[source] = 0;

        int[] prev = new int[numOfNodes];
        prev[source] = -1;

        Set<Integer> unvisited = new HashSet<>();

        for (int building = 0; building < numOfNodes; building++) {
            if (building != source) {
                dist[building] = 5000;
                prev[building] = -1;
            }
            unvisited.add(building);
        } // initialization done

        while (unvisited.size() != 0) {
            int u = findNodeWithMinDist(dist, unvisited);
            unvisited.remove(u);

            List<Integer> neighborOfU = getNeighbor(u);
            for (Integer v : neighborOfU) {
                int altDist = dist[u] + length(u, v);
                if (altDist < dist[v]) {
                    dist[v] = altDist;
                    prev[v] = u;
                }
            }
        }

        /** generate path **/
        for (int target = 0; target < numOfNodes; target++)
            if (target == source)
                paths[source][target] = new int[]{source};
            else {
                List<Integer> path = new ArrayList<>(10);
                int prevBuilding = prev[target];
                while (prevBuilding != source) {
                    path.add(prevBuilding);
                    prevBuilding = prev[prevBuilding];
                }
                path.add(prevBuilding);
                Collections.reverse(path);
                path.add(target);
                paths[source][target] = ArrUtil.IntegerList2IntArray(path);
            }
    }

    /**
     * Part of Dijkstra, find the building to which the distance is smallest.
     * @param dist
     * @param unvisited
     * @return
     */
    private int findNodeWithMinDist(int[] dist, Set<Integer> unvisited) // I know it could be better
    {
        int minDistSoFar = Integer.MAX_VALUE, candidate = -1;
        for (Integer building : unvisited)
            if (dist[building] < minDistSoFar) {
                minDistSoFar = dist[building];
                candidate = building;
            }
        return candidate;
    }

    /**
     * Get a list of integers that represent the neighbor of u.
     * @param u
     * @return
     */
    private List<Integer> getNeighbor(int u)
    {
        List<Integer> neighborOfU = new ArrayList<>(10);
        for (int i = 0; i < north[u].length; i++)
            if (north[u][i])
                neighborOfU.add(i);
        for (int i = 0; i < south[u].length; i++)
            if (south[u][i])
                neighborOfU.add(i);
        for (int i = 0; i < west[u].length; i++)
            if (west[u][i])
                neighborOfU.add(i);
        for (int i = 0; i < east[u].length; i++)
            if (east[u][i])
                neighborOfU.add(i);
        return neighborOfU;
    }

    /**
     * The cost between u and v. For this homework, all paths have equal cost.
     * @param u
     * @param v
     * @return
     */
    private int length(int u, int v) //
    {
        return 1;
    }

    public static void main(String[] args)
    {
        /** deserialize reduced spatial relationships **/
        boolean[][] north = (boolean[][]) IOUtil.deserialize("northReduced.ser");
        boolean[][] south = (boolean[][]) IOUtil.deserialize("southReduced.ser");
        boolean[][] west = (boolean[][]) IOUtil.deserialize("westReduced.ser");
        boolean[][] east = (boolean[][]) IOUtil.deserialize("eastReduced.ser");

        ShortestPath shortestPath = new ShortestPath(north, south, west, east);
        shortestPath.findPathForAll();

        /** serialize shortest paths **/
        IOUtil.serialize("shortestPath.ser" ,shortestPath.paths);
    }
}
