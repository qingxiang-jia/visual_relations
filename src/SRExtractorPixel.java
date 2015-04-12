/**
 * Spatial relationship extractor for each pixel.
 * Very similar to SpacialRelationExtractor, but it works for each individual pixel instead of
 * each building. Each pixel is considered a tiny building, and the extractor extracts North(S, T),
 * South(S, T), West(S, T), East(S, T), and Near(S, T).
 */
public class SRExtractorPixel
{
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
    boolean[][][] north, south, west, east, near;

    public SRExtractorPixel(int[] area, int[][] centroids, int[][] img)
    {
        this.area = area;
        this.centroids = centroids;
        this.img = img;
        north = new boolean[img.length][img[0].length][area.length];
        south = new boolean[img.length][img[0].length][area.length];
        west = new boolean[img.length][img[0].length][area.length];
        east = new boolean[img.length][img[0].length][area.length];
        near = new boolean[img.length][img[0].length][area.length];
    }

    /**
     * Extracts all five relationships for each pixel that is not in a
     * building.
     */
    public void extract()
    {

    }

    /**
     * Extract North(S, T). Notice that S is a pixel, where T is a building.
     * Algorithm: similar to the building version of this method.
     * When scanning north, it starts certain distance north from this pixel,
     * a imaginary bar with certain width will scan north, any building touched
     * int this scan will be north of this pixel.
     * @param row
     * @param col
     */
    private void extractNorth(int row, int col)
    {

    }
}
