/**
 * In Step 2, we extract five spatial relationships for each building. They are
 * decided by scanning the map. This class uses the scanned area to assign spatial
 * relationships for each pixel. This is a substitute for SRExtractorPixel, which
 * has a downside of hard to perform transitive reduction. With this, the reduction
 * will be performed based on how close the pixel is to the building, making it
 * possible to perform reduction on the fly.
 * Influence refers to the scanned area of each building. Mapping refers to that
 * the pixel will be marked for each influence.
 */
public class InfluenceMapping
{
    /**
     * store MBR coordinates *
     */
    int[] rMin, rMax, cMin, cMax;

    /**
     * area of each building *
     */
    int[] area;

    /**
     * centers of mass *
     */
    int[][] centroids;

    /**
     * int[][] version of ass3-labeled.pgm *
     */
    int[][] img;

    /**
     * Store spatial relationship for each pixel.
     */
    boolean[][][] mappedNorth, mappedSouth, mappedWest, mappedEast, mappedNear; // [r][c][building]

    public InfluenceMapping(int[][] MBRCoordinates, int[] area, int[][] centroids, int[][] img)
    {
        rMin = MBRCoordinates[0];
        rMax = MBRCoordinates[1];
        cMin = MBRCoordinates[2];
        cMax = MBRCoordinates[3];
        this.area = area;
        this.centroids = centroids;
        this.img = img;
        mappedNorth = new boolean[img.length][img[0].length][area.length];
        mappedSouth = new boolean[img.length][img[0].length][area.length];
        mappedWest = new boolean[img.length][img[0].length][area.length];
        mappedEast = new boolean[img.length][img[0].length][area.length];
        mappedNear = new boolean[img.length][img[0].length][area.length];
    }

    public void map()
    {
        int numOfBuilding = area.length;
        for (int building = 0; building < numOfBuilding; building++) {
            mapNorth(building);
            mapSouth(building);
            mapWest(building);
            mapEast(building);
            mapNear(building);
        }

//        // test print
//        for (int r = 0; r < img.length; r++) {
//            for (int c = 0; c < img[0].length; c++) {
//                if (mappedEast[r][c][18])
//                    System.out.print("T");
//                else
//                    System.out.print(" ");
//            }
//            System.out.println();
//        }

    }

    /**
     * Extracts North(S, T) for each building.
     * Algorithm:
     *             ^ scan north
     *             |
     *             |
     *             |
     * rMin, cMin----rMin, cMax
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * rMax, cMin----rMax, cMax
     * Above is the MBR, initialize a scan whose width is roughly (cMax - cMin) to the
     * north, any building touched in this scan will be T in North(S, T).
     * @param building
     */
    private void mapNorth(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = colMax - colMin + 1;

        int scanBegin = colMin, scanEnd = colMax;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img[0].length)
            scanEnd = img[0].length - 1;

        /** scan north & save results **/
        for (int r = rowMin; r >= 0; r--)
            for (int c = scanBegin; c <= scanEnd; c++) {
                mappedNorth[r][c][building] = true;
            }
    }

    /**
     * Extracts South(S, T) for each building.
     * Algorithm:
     * rMin, cMin----rMin, cMax
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * rMax, cMin----rMax, cMax
     *             |
     *             |
     *             |
     *             V scan south
     * Above is the MBR, initialize a scan whose width is roughly (cMax - cMin) to the
     * south, any building touched in this scan will be T in South(S, T).
     * @param building
     */
    private void mapSouth(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = colMax - colMin + 1;

        int scanBegin = colMin, scanEnd = colMax;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img[0].length)
            scanEnd = img[0].length - 1;

        /** scan south & save results **/
        for (int r = rowMax; r < img.length; r++)
            for (int c = scanBegin; c <= scanEnd; c++) {
                mappedSouth[r][c][building] = true;
            }
    }

    /**
     * Extracts West(S, T) for each building.
     * Algorithm:
     * rMin, cMin----rMin, cMax
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * rMax, cMin----rMax, cMax
     * Above is the MBR, initialize a scan whose width is roughly (rMax - rMin) to the
     * west, any building touched in this scan will be T in West(S, T).
     * @param building
     */
    private void mapWest(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = rowMax - rowMin + 1;

        int scanBegin = rowMin, scanEnd = rowMax;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img.length)
            scanEnd = img.length - 1;

        /** scan west & save results **/
        for (int r = scanBegin; r < scanEnd; r++)
            for (int c = colMin; c >= 0; c--) {
                    mappedWest[r][c][building] = true;
            }
    }

    /**
     * Extracts East(S, T) for each building.
     * Algorithm:
     * rMin, cMin----rMin, cMax
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * rMax, cMin----rMax, cMax
     * Above is the MBR, initialize a scan whose width is roughly (rMax - rMin) to the
     * east, any building touched in this scan will be T in East(S, T).
     * @param building
     */
    private void mapEast(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = rowMax - rowMin + 1;

        int scanBegin = rowMin, scanEnd = rowMax;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img.length)
            scanEnd = img.length - 1;

        /** scan east & save results **/
        for (int r = scanBegin; r < scanEnd; r++)
            for (int c = colMax; c < img[0].length; c++) {
                mappedEast[r][c][building] = true;
            }
    }

    /**
     * Extracts the Near(S, T) relationship.
     * Algorithm: increase the MBR by a constant factor to get a new bounding box. Scan through
     * the new bounding box to see if there is any building in it. The side of the bounding box
     * depends on the side of MBR which depends on the shape and size of the building.
     * rMin, cMin----rMin, cMax
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * |                      |
     * rMax, cMin----rMax, cMax
     * @param building
     */
    private void mapNear(int building)
    {
        final float STRETCH = 0.55f;
        int id = building + 1;
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int horizontal = colMax - colMin, vertical = rowMax - rowMin;
        int rowChange, colChange;
        if (vertical <= 15)
            rowChange = (int) (vertical * (1 + 2f * STRETCH)) / 2;
        else if (vertical <= 50)
            rowChange = (int) (vertical * (1 + STRETCH)) / 2;
        else if (vertical <= 250)
            rowChange = (int) (vertical * (1 + 0.2f * STRETCH)) / 2;
        else
            rowChange = (int) (vertical * (0.8f * STRETCH)) / 2;
        if (vertical <= 15)
            colChange = (int) (horizontal * (1 + 2f * STRETCH)) / 2;
        else if (horizontal <= 50)
            colChange = (int) (horizontal * (1 + STRETCH)) / 2;
        else if (horizontal <= 250)
            colChange = (int) (horizontal * (1 + 0.2f * STRETCH)) / 2;
        else
            colChange = (int) (horizontal * (0.8f * STRETCH)) / 2;
        if (rowChange < colChange) { // make the bounding box closer to a square
            rowChange += 0.4f * (colChange - rowChange);
        } else if (rowChange > colChange) {
            colChange += 0.4f * (rowChange - colChange);
        }

        /** redefine scan boundaries **/
        int rowBegin = rowMin - rowChange;
        if (rowBegin < 0)
            rowBegin = 0;

        int rowEnd = rowMax + rowChange;
        if (rowEnd > img.length - 1)
            rowEnd = img.length - 1;

        int colBegin = colMin - colChange;
        if (colBegin < 0)
            colBegin = 0;

        int colEnd = colMax + colChange;
        if (colEnd > img[0].length - 1)
            colEnd = img[0].length - 1;

        /** scan the new bounding box & save results **/
        for (int r = rowBegin; r <= rowEnd; r++)
            for (int c = colBegin; c <= colEnd; c++) {
                mappedNear[r][c][building] = true;
            }
    }

    public static void main(String[] args)
    {
        /** deserialize required files **/
        int[][] img = (int[][]) IOUtil.deserialize("img.ser");
        int[][] MBRCoordinates = (int[][]) IOUtil.deserialize("MBRCoordinates.ser");
        int[] area = (int[]) IOUtil.deserialize("area.ser");
        int[][] centroids = (int[][]) IOUtil.deserialize("centroids.ser");

        /** extract spacial relationships **/
        InfluenceMapping mapper = new InfluenceMapping(MBRCoordinates, area, centroids, img);
        mapper.map();
//        IOUtil.serialize("mappedNorth.ser", mapper.mappedNorth);
//        IOUtil.serialize("mappedSouth.ser", mapper.mappedSouth);
//        IOUtil.serialize("mappedWest.ser", mapper.mappedWest);
//        IOUtil.serialize("mappedEast.ser", mapper.mappedEast);
//        IOUtil.serialize("mappedNear.ser", mapper.mappedNear);
    }
}
