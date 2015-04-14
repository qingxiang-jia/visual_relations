/**
 * For each pixel, reduceByArea the number of spatial relationships to around 2 or 3.
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

    public void reduceByArea()
    {
        for (int r = 0; r < mappedNorth.length; r++)
            for (int c = 0; c < mappedNorth[0].length; c++) {
                reduceByArea(r, c);
//                System.out.println(r + " " + c + " " + Arrays.toString(reducedMapping[r][c]));
            }
    }

    public void createInverse()
    {
        reducedMappingInverse = new PixelSet[area.length][area.length][area.length][area.length][area.length];
        for (int r = 0; r < reducedMapping.length; r++)
            for (int c = 0; c < reducedMapping[0].length; c++) {
                if (reducedMappingInverse[reducedMapping[r][c][0]][reducedMapping[r][c][1]]
                        [reducedMapping[r][c][2]][reducedMapping[r][c][3]][reducedMapping[r][c][4]] == null) {
                    reducedMappingInverse[reducedMapping[r][c][0]][reducedMapping[r][c][1]]
                            [reducedMapping[r][c][2]][reducedMapping[r][c][3]][reducedMapping[r][c][4]] = new PixelSet();
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
    private void reduceByArea(int row, int col)
    {
        int northBuilding = findBuildingWithMinArea(mappedNorth[row][col]);
        int southBuilding = findBuildingWithMinArea(mappedSouth[row][col]);
        int westBuilding = findBuildingWithMinArea(mappedWest[row][col]);
        int eastBuilding = findBuildingWithMinArea(mappedEast[row][col]);

        int sumNW = -1, sumNE = -1, sumSW = -1, sumSE = -1;
        ifNotNegativeThenSum(northBuilding, westBuilding, sumNW);
        ifNotNegativeThenSum(northBuilding, eastBuilding, sumNE);
        ifNotNegativeThenSum(southBuilding, westBuilding, sumSW);
        ifNotNegativeThenSum(southBuilding, eastBuilding, sumSE);

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
            reducedMapping[row][col][direction] = building;
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
                reducedMapping[row][col][0] = northBuilding;
                reducedMapping[row][col][2] = westBuilding;
            } else if (smallestIndex == 1) { // N E
                reducedMapping[row][col][0] = northBuilding;
                reducedMapping[row][col][3] = eastBuilding;
            } else if (smallestIndex == 2) { // S W
                reducedMapping[row][col][1] = southBuilding;
                reducedMapping[row][col][2] = westBuilding;
            } else if (smallestIndex == 3) { // S E
                reducedMapping[row][col][1] = southBuilding;
                reducedMapping[row][col][3] = eastBuilding;
            }
        }
        /** add near **/
        if (ArrUtil.countTrue(mappedNear[row][col]) > 0) {
            reducedMapping[row][col][4] = findBuildingWithMinArea(mappedNear[row][col]);
        }
    }

    /**
     * Prune spatial relationship for a pixel.
     * Reduction rule:
     * 1) for each of N, S, W, E, find the two attributes whose corresponding building is closest to the pixel.
     * 2) if there is only one such attribute, add it.
     * 3) if there is Near, use the building with minimal area (because presumably it's the closet building).
     */
    private void reduceByAreaAndDist(int row, int col) //todo
    {
//        int northBuilding = findBuildingWithMinDist(mappedNorth[row][col]);
//        int southBuilding = findBuildingWithMinDist(mappedSouth[row][col]);
//        int westBuilding = findBuildingWithMinDist(mappedWest[row][col]);
//        int eastBuilding = findBuildingWithMinDist(mappedEast[row][col]);

        /** add near **/
        if (ArrUtil.countTrue(mappedNear[row][col]) > 0) {
            reducedMapping[row][col][4] = findBuildingWithMinArea(mappedNear[row][col]);
        }
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
    private int findBuildingWithMinDist(boolean[] arr, int row, int col) //todo
    {
        int minDistSoFar = Integer.MAX_VALUE;
        int building = -1;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] && (computeDist(row, col, centroids[i][0], centroids[i][1]) < minDistSoFar)) {
                building = i;
                minDistSoFar = computeDist(row, col, centroids[i][0], centroids[i][1]);
            }
        }
        return -1;
    }

    private void ifNotNegativeThenSum(int a, int b, int sum)
    {
        if (!(a == -1) && !(b == -1))
            sum = a + b;
    }

    private int computeDist(int row1, int col1, int row2, int col2)
    {
        return (int) Math.sqrt((row1 - row2)^2 + (col1 - col2)^2);
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
        reducer.reduceByArea();
        IOUtil.serialize("reducedMapping.ser", reducer.reducedMapping);
        reducer.createInverse();
        IOUtil.serialize("reducedMappingInverse.ser", reducer.reducedMappingInverse);
    }
}
