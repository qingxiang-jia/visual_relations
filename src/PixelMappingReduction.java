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
     * Store spatial relationship for each pixel.
     */
    boolean[][][] mappedNorth, mappedSouth, mappedWest, mappedEast, mappedNear; // [r][c][building]

    /**
     * Store pruned spatial relationship for each pixel.
     */
    int[][][] reducedMapping; // [r][c][N S W E Near] -1 represents none, else the value is building id
    Pixel[][][][][] reducedMappingInverse; // [N][S][W][E][Near]

    public PixelMappingReduction(int area[], boolean[][][] mappedNorth, boolean[][][] mappedSouth,
                                 boolean[][][] mappedWest, boolean[][][] mappedEast, boolean[][][] mappedNear)
    {
        this.area = area;
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
            for (int c = 0; c < mappedNorth[0].length; c++) {
                reduce(r, c);
//                System.out.println(r + " " + c + " " + Arrays.toString(reducedMapping[r][c]));
            }
    }

    public void createInverse()
    {
        reducedMappingInverse = new Pixel[area.length][area.length][area.length][area.length][area.length];
        for (int r = 0; r < reducedMapping.length; r++)
            for (int c = 0; c < reducedMapping[0].length; c++) {
                reducedMappingInverse[reducedMapping[r][c][0]][reducedMapping[r][c][1]]
                        [reducedMapping[r][c][2]][reducedMapping[r][c][3]][reducedMapping[r][c][4]] = new Pixel(r, c);
            }
    }

    /**
     * Prune spatial relationship for a pixel.
     * Reduction rule:
     * 1) for each orthogonal pair: N S, N E, S W, S E, only keep the building with smallest area.
     * 2) if after 1), there are more than one pair, choose the pair with minimal average distance.
     * 3) if before 1), cannot find such pair, use one of N, S, W, E whose building's area is the minimal.
     * 4) if there is Near, use the building with minimal area (because presumably it's the closet building).
     */
    private void reduce(int row, int col)
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
            }
        }
        return building;
    }

    private void ifNotNegativeThenSum(int a, int b, int sum)
    {
        if (!(a == -1) && !(b == -1))
            sum = a + b;
    }

    public static void main(String[] args)
    {
        /** deserialize required files **/
        int[] area = (int[]) IOUtil.deserialize("area.ser");
        boolean[][][] mappedNorth = (boolean[][][]) IOUtil.deserialize("mappedNorth.ser");
        boolean[][][] mappedSouth = (boolean[][][]) IOUtil.deserialize("mappedSouth.ser");
        boolean[][][] mappedWest = (boolean[][][]) IOUtil.deserialize("mappedWest.ser");
        boolean[][][] mappedEast = (boolean[][][]) IOUtil.deserialize("mappedEast.ser");
        boolean[][][] mappedNear = (boolean[][][]) IOUtil.deserialize("mappedNear.ser");

        /** extract spacial relationships **/
        PixelMappingReduction reducer = new PixelMappingReduction(area, mappedNorth, mappedSouth, mappedWest, mappedEast, mappedNear);
        reducer.reduce();
        IOUtil.serialize("reducedMapping.ser", reducer.reducedMapping);
        reducer.createInverse();
        IOUtil.serialize("reducedMappingInverse.ser", reducer.reducedMappingInverse);
    }
}
