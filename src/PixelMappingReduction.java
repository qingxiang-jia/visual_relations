import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * For each pixel, reduce the number of spatial relationships to around 2 or 3.
 * Here is the rule: we want to preserve orthogonal relationships such as N E,
 * N W, etc, because it helps to locate a pixel in 2D space. When there is near
 * attribute available, we do it, otherwise, not.
 */
public class PixelMappingReduction
{
    /**
     * area of each building
     */
    int[] area;

    /**
     * center of mass of each building
     */
    int[][] centroids;

    /**
     * Store spatial relationship for each pixel.
     */
    boolean[][][] mappedNorth, mappedSouth, mappedWest, mappedEast, mappedNear; // [r][c][building]

    /**
     * Store pruned spatial relationship for each pixel.
     */
    int[][][] reducedMapping; // [r][c][N S W E Near] -1 represents none, else the value is building id
    PixelSet[][][][][] reducedMappingInverse; // [N][S][W][E][Near]

    public PixelMappingReduction(int[] area, int[][] centroids, boolean[][][] mappedNorth, boolean[][][] mappedSouth,
                                 boolean[][][] mappedWest, boolean[][][] mappedEast, boolean[][][] mappedNear)
    {
        this.area = area;
        this.centroids = centroids;
        this.mappedNorth = mappedNorth;
        this.mappedSouth = mappedSouth;
        this.mappedWest = mappedWest;
        this.mappedEast = mappedEast;
        this.mappedNear = mappedNear;
        reducedMapping = new int[mappedNorth.length][mappedNorth[0].length][5];
    }

    public void reduce()
    {
        for (int r = 0; r < mappedNorth.length; r++)
            for (int c = 0; c < mappedNorth[0].length; c++)
                reduceByAreaAndDist(r, c);
    }

    public void createInverse()
    {
        reducedMappingInverse = new PixelSet[area.length + 1][area.length + 1][area.length + 1][area.length + 1][area.length + 1];
        for (int r = 0; r < reducedMapping.length; r++)
            for (int c = 0; c < reducedMapping[0].length; c++) {
                if (reducedMappingInverse[reducedMapping[r][c][0]][reducedMapping[r][c][1]]
                        [reducedMapping[r][c][2]][reducedMapping[r][c][3]][reducedMapping[r][c][4]] == null) {
                    PixelSet pixelSet = new PixelSet();
                    pixelSet.add(new Pixel(r, c));
                    reducedMappingInverse[reducedMapping[r][c][0]][reducedMapping[r][c][1]]
                            [reducedMapping[r][c][2]][reducedMapping[r][c][3]][reducedMapping[r][c][4]] = pixelSet;
                }
                reducedMappingInverse[reducedMapping[r][c][0]][reducedMapping[r][c][1]]
                        [reducedMapping[r][c][2]][reducedMapping[r][c][3]][reducedMapping[r][c][4]].add(new Pixel(r, c));
            }
    }

    /**
     * Prune spatial relationship for a pixel.
     * Reduction rule:
     * 1) for each orthogonal pair: N S, N E, S W, S E, only keep the building with smallest area.
     * 2) if after 1), there are more than one pair, choose the pair with minimal average area.
     * 3) if before 1), cannot find such pair, use one of N, S, W, E whose building's area is the minimal.
     * 4) if there is Near, use the building with minimal area (because presumably it's the closet building).
     */
    private void reduce(int row, int col)
    {
        int northBuilding = findBuildingWithMinArea(mappedNorth[row][col]);
        int southBuilding = findBuildingWithMinArea(mappedSouth[row][col]);
        int westBuilding = findBuildingWithMinArea(mappedWest[row][col]);
        int eastBuilding = findBuildingWithMinArea(mappedEast[row][col]);

        int sumNW, sumNE, sumSW, sumSE;
        sumNW = ifNotNegativeThenSum(northBuilding, westBuilding);
        sumNE = ifNotNegativeThenSum(northBuilding, eastBuilding);
        sumSW = ifNotNegativeThenSum(southBuilding, westBuilding);
        sumSE = ifNotNegativeThenSum(southBuilding, eastBuilding);

        if (sumNW == -1 && sumNE == -1 && sumSW == -1 && sumSE == -1) { // no orthogonal pair possible
            int[] compareArr = new int[]{northBuilding, southBuilding, westBuilding, eastBuilding};
            int areaSoFar = Integer.MAX_VALUE;
            int building = -1;
            int direction = -1;
            for (int i = 0; i < compareArr.length; i++) {
                if (compareArr[i] != -1 && area[compareArr[i]] < areaSoFar) {
                    building = compareArr[i];
                    direction = i;
                }
            }
            reducedMapping[row][col][direction] = building + 1;
        } else { // there is at least one orthogonal pair
            int[] compareArr = new int[]{sumNW, sumNE, sumSW, sumSE};
            int smallest = Integer.MAX_VALUE;
            int smallestIndex = -1;
            for (int i = 0; i < compareArr.length; i++) {
                if (compareArr[i] != -1 && compareArr[i] < smallest) {
                    smallest = compareArr[i];
                    smallestIndex = i;
                }
            }
            if (smallestIndex == 0) { // N W
                reducedMapping[row][col][0] = northBuilding + 1;
                reducedMapping[row][col][2] = westBuilding + 1;
            } else if (smallestIndex == 1) { // N E
                reducedMapping[row][col][0] = northBuilding + 1;
                reducedMapping[row][col][3] = eastBuilding + 1;
            } else if (smallestIndex == 2) { // S W
                reducedMapping[row][col][1] = southBuilding + 1;
                reducedMapping[row][col][2] = westBuilding + 1;
            } else if (smallestIndex == 3) { // S E
                reducedMapping[row][col][1] = southBuilding + 1;
                reducedMapping[row][col][3] = eastBuilding + 1;
            }
        }
        /** add near **/
        if (ArrUtil.countTrue(mappedNear[row][col]) > 0) {
            reducedMapping[row][col][4] = findBuildingWithMinArea(mappedNear[row][col]) + 1;
        }
    }

    /**
     * Prune spatial relationship for a pixel.
     * Reduction rule:
     * 1) for each of N, S, W, E, find the two attributes whose corresponding building is closest to the pixel.
     * 2) if there is only one such attribute, add it.
     * 3) if there is Near, use the building with minimal area (because presumably it's the closet building).
     */
    private void reduceByAreaAndDist(int row, int col)
    {
        int northBuilding = findBuildingWithMinDist(mappedNorth[row][col], row, col);
//        System.out.println(row + " " + col + " "  + northBuilding);
        int southBuilding = findBuildingWithMinDist(mappedSouth[row][col], row, col);
        int westBuilding = findBuildingWithMinDist(mappedWest[row][col], row, col);
//        System.out.print(row + " " + col + " ");
//        for (boolean bol : mappedWest[row][col]) {
//            if (bol)
//                System.out.print("T ");
//            else
//                System.out.print("F ");
//        }
//        System.out.println();
        int eastBuilding = findBuildingWithMinDist(mappedEast[row][col], row, col);

//        if (row == 296 && col == 171) {
//            System.out.printf("northbuilding: %d southbuilding: %d westbuilding: %d eastbuiling: %d\n", northBuilding, southBuilding, westBuilding, eastBuilding);
//            System.out.println("north: " + Arrays.toString(mappedNorth[row][col]));
//            System.out.println("south: " + Arrays.toString(mappedSouth[row][col]));
//            System.out.println("west: " + Arrays.toString(mappedWest[row][col]));
//            System.out.println("east: " + Arrays.toString(mappedEast[row][col]));
//        }

        /** find two buildings with two lowest distances **/ // there are better ways, but catching the deadline
        List<IntTuple> tuples = new ArrayList<>();
        if (northBuilding != -1)
            tuples.add(new IntTuple(northBuilding, 0, Compute.dist(row, col, centroids[northBuilding][0], centroids[northBuilding][1])));
        if (southBuilding != -1)
            tuples.add(new IntTuple(southBuilding, 1, Compute.dist(row, col, centroids[southBuilding][0], centroids[southBuilding][1])));
        if (westBuilding != -1)
            tuples.add(new IntTuple(westBuilding, 2, Compute.dist(row, col, centroids[westBuilding][0], centroids[westBuilding][1])));
        if (eastBuilding != -1)
            tuples.add(new IntTuple(eastBuilding, 3, Compute.dist(row, col, centroids[eastBuilding][0], centroids[eastBuilding][1])));
        Collections.sort(tuples); // sort by distance in ascending order
        if (tuples.size() != 0)
            reducedMapping[row][col][tuples.get(0).b] = tuples.get(0).a + 1;
        if (tuples.size() > 1)
            reducedMapping[row][col][tuples.get(1).b] = tuples.get(1).a + 1;

        /** add near **/
        reducedMapping[row][col][4] = findBuildingWithMinDist(row, col) + 1;
    }

    /**
     * Find the building that those cell is true, and has minimal area among all other building whose
     * cell value is true in arr.
     * @param arr
     * @return
     */
    private int findBuildingWithMinArea(boolean[] arr)
    {
        int areaSoFar = Integer.MAX_VALUE;
        int building = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] && area[i] < areaSoFar) {
                building = i;
                areaSoFar = area[i];
            }
        }
        return building;
    }

    /**
     * Find the building whose cell is true, and has minimal distance to the pixel.
     * @param arr
     * @return
     */
    private int findBuildingWithMinDist(boolean[] arr, int row, int col)
    {
        int minDistSoFar = Integer.MAX_VALUE;
        int building = -1;
        for (int i = 0; i < arr.length; i++) {
            int dist = Compute.dist(row, col, centroids[i][0], centroids[i][1]);
            if (arr[i] && dist < minDistSoFar) {
                building = i;
                minDistSoFar = dist;
            }
        }
        return building;
    }

    /**
     * Find the building with minimum distance given pixel's row and col.
     * @return
     */
    private int findBuildingWithMinDist(int row, int col)
    {
        int minDistSoFar = Integer.MAX_VALUE;
        int building = -1;
        for (int i = 0; i < centroids.length; i++) {
            int dist = Compute.dist(row, col, centroids[i][0], centroids[i][1]);
            if (dist < minDistSoFar) {
                building = i;
                minDistSoFar = dist;
            }
        }
        return building;
    }

    private int ifNotNegativeThenSum(int a, int b)
    {
        if (!(a == -1) && !(b == -1))
            return a + b;
        else
            return -1;
    }



    public static void main(String[] args)
    {
        /** deserialize required files **/
        int[] area = (int[]) IOUtil.deserialize("area.ser");
        int[][] centroids = (int[][]) IOUtil.deserialize("centroids.ser");
        boolean[][][] mappedNorth = (boolean[][][]) IOUtil.deserialize("mappedNorth.ser");
        boolean[][][] mappedSouth = (boolean[][][]) IOUtil.deserialize("mappedSouth.ser");
        boolean[][][] mappedWest = (boolean[][][]) IOUtil.deserialize("mappedWest.ser");
        boolean[][][] mappedEast = (boolean[][][]) IOUtil.deserialize("mappedEast.ser");
        boolean[][][] mappedNear = (boolean[][][]) IOUtil.deserialize("mappedNear.ser");

        /** extract spacial relationships **/
        PixelMappingReduction reducer = new PixelMappingReduction(area, centroids,
                mappedNorth, mappedSouth, mappedWest, mappedEast, mappedNear);
        reducer.reduce();
        IOUtil.serialize("reducedMapping.ser", reducer.reducedMapping);
        reducer.createInverse();
        IOUtil.serialize("reducedMappingInverse.ser", reducer.reducedMappingInverse);
    }
}
