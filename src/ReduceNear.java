import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reduce near relationship.
 */
public class ReduceNear
{
    /**
     * The idea is to filter out the building that is barely near. To do so, sort the buildings that are near
     * by distance. Then bookkeeping the difference between buildings. Remove the building where the difference
     * in distance is larger than a threshold.
     */
    public static boolean[][] reduce(boolean[][] near, int[][] centroids)
    {
        for (int building = 0; building < near.length; building++) {
            if (ArrUtil.countTrue(near[building]) > 1) {
                List<IntTuple> nearBuildings = new ArrayList<>();
                for (int neighbor = 0; neighbor < near[building].length; neighbor++) {
                    if (near[building][neighbor]) {
                        nearBuildings.add(new IntTuple(neighbor, 0, Compute.dist(centroids[building][0], centroids[building][1], centroids[neighbor][0], centroids[neighbor][1])));
                    }
                }
                Collections.sort(nearBuildings);
                List<Integer> toRemove = new ArrayList<>();
                for (int i = 1; i < nearBuildings.size(); i++) {
                    if (Math.abs(nearBuildings.get(i-1).c - nearBuildings.get(i).c) > 50) {
                        toRemove.add(i);
                    }
                }
                for (Integer neighbor : toRemove) {
                    near[building][neighbor] = false;
                }
            }
        }
        return near;
    }

    public static void main(String[] args)
    {
        boolean[][] nearReduced = ReduceNear.reduce((boolean[][]) IOUtil.deserialize("near.ser"), (int[][]) IOUtil.deserialize("centroids.ser"));
        IOUtil.serialize("nearReduced.ser", nearReduced);
    }
}
