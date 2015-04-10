import java.util.HashSet;
import java.util.Set;

/**
 * Generate pairwise spacial relationship between two buildings for all buildings
 * in the map, given an image file, MBR, center of mass, and area.
 */
public class SpacialRelationExtractor
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
     * stores spacial relationships. e.g. north[1][3] is "North of 1 is 3".
     */
    boolean[][] north, south, west, east, near;

    public SpacialRelationExtractor(int[][] MBRCoordinates, int[] area, int[][] centroids, int[][] img)
    {
        rMin = MBRCoordinates[0];
        rMax = MBRCoordinates[1];
        cMin = MBRCoordinates[2];
        cMax = MBRCoordinates[3];
        this.area = area;
        this.centroids = centroids;
        this.img = img;
        north = new boolean[area.length][area.length];
        south = new boolean[area.length][area.length];
        west = new boolean[area.length][area.length];
        east = new boolean[area.length][area.length];
    }

    /**
     * Extracts all five relationships for each building in the map.
     */
    public void extract()
    {
        int numOfBuilding = area.length;
        for (int building = 0; building < numOfBuilding; building++) {
            extractNorth(building);
            extractSouth(building);
            extractWest(building);
        }

        // print result
        for (int i = 0; i < numOfBuilding; i++) {
            System.out.println("building:" + i);
            for (int j = 0; j < north[i].length; j++) {
                if (north[i][j]) {
                    System.out.printf(" "+j+" ");
                    System.out.print("T\n");
                }
            }
        }
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
    private void extractNorth(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = colMax - colMin + 1;

        int scanBegin = colMin, scanEnd = colMax;
        int rowBegin = rowMin - 10;
        if (rowBegin < 10)
            rowBegin = 0;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img[0].length)
            scanEnd = img[0].length - 1;

        /** scan north **/
        Set<Integer> northOf = new HashSet<>();
        for (int r = rowBegin; r >= 0; r -= 10)
            for (int c = scanBegin; c <= scanEnd; c += 5) {
                if (img[r][c] != 0) {
                    /** check the slope formed by two centers of mass **/
                    int x1 = centroids[building][1], y1 = centroids[building][0];
                    int x2 = centroids[img[r][c] - 1][1], y2 = centroids[img[r][c] - 1][0];
                    float m = getSlopeUpsideDown(x1, y1, x2, y2);
                    if (!(Math.abs(m) < 1))
                        northOf.add(img[r][c] - 1);
                }
            }

        /** save result **/
        for (int i = 0; i < north[building].length; i++) {
            if (northOf.contains(i)) {
                north[building][i] = true;
            } // no else, all values initialized to false
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
     * north, any building touched in this scan will be T in South(S, T).
     * @param building
     */
    private void extractSouth(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = colMax - colMin + 1;

        int scanBegin = colMin, scanEnd = colMax;
        int rowBegin = rowMax + 10;
        if (rowBegin > img.length - 1)
            rowBegin = img.length - 1;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img[0].length)
            scanEnd = img[0].length - 1;

        /** scan south **/
        Set<Integer> southOf = new HashSet<>();
        for (int r = rowBegin; r < img.length; r += 10)
            for (int c = scanBegin; c <= scanEnd; c += 5) {
                if (img[r][c] != 0) {
                    /** check the slope formed by two centers of mass **/
                    int x1 = centroids[building][1], y1 = centroids[building][0];
                    int x2 = centroids[img[r][c] - 1][1], y2 = centroids[img[r][c] - 1][0];
                    float m = getSlopeUpsideDown(x1, y1, x2, y2);
                    if (!(Math.abs(m) < 1))
                        southOf.add(img[r][c] - 1);
                }
            }

        /** save result **/
        for (int i = 0; i < north[building].length; i++) {
            if (southOf.contains(i)) {
                south[building][i] = true;
            } // no else, all values initialized to false
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
     * north, any building touched in this scan will be T in South(S, T).
     * @param building
     */
    private void extractWest(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        int width = rowMax - rowMin + 1;

        int scanBegin = rowMin, scanEnd = rowMax;
        int colBegin = colMax - 10;
        if (colBegin < 0)
            colBegin = 0;

        scanBegin -= 0.01 * width; // widen the scan width a little bit
        if (scanBegin < 0)
            scanBegin = 0;

        scanEnd += 0.01 * width; // widen the scan width a little bit
        if (scanEnd > img.length)
            scanEnd = img.length - 1;

        /** scan west **/
        Set<Integer> westOf = new HashSet<>();
        for (int r = scanBegin; r < scanEnd; r += 10)
            for (int c = colBegin; c >= 0; c -= 5) {
                if (img[r][c] != 0) {
                    /** check the slope formed by two centers of mass **/
                    int x1 = centroids[building][1], y1 = centroids[building][0];
                    int x2 = centroids[img[r][c] - 1][1], y2 = centroids[img[r][c] - 1][0];
                    float m = getSlopeUpsideDown(x1, y1, x2, y2);
                    if ((Math.abs(m) < 1))
                        westOf.add(img[r][c] - 1);
                }
            }

        /** save result **/
        for (int i = 0; i < north[building].length; i++) {
            if (westOf.contains(i)) {
                west[building][i] = true;
            } // no else, all values initialized to false
        }
    }

    private float getSlopeUpsideDown(int x1, int y1, int x2, int y2)
    {
        if (x2 - x1 == 0) {
            if (y2 - y1 >= 0)
                return -Float.MAX_VALUE;
            else
                return -Float.MAX_VALUE;
        }
        return (-((float) y2 - y1)) / (x2 - x1);
    }

    public static void main(String[] args)
    {
        /** deserialize required files **/
        int[][] img = (int[][]) IOUtil.deserialize("img.ser");
        int[][] MBRCoordinates = (int[][]) IOUtil.deserialize("MBRCoordinates.ser");
        int[] area = (int[]) IOUtil.deserialize("area.ser");
        int[][] centroids = (int[][]) IOUtil.deserialize("centroids.ser");

        /** extract spacial relationships **/
        SpacialRelationExtractor srExtractor = new SpacialRelationExtractor(MBRCoordinates, area, centroids, img);

        srExtractor.extract();
    }


















































}
