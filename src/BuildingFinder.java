import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * Finds for each building: four corners of MBR, area, COM
 * MBR = minimum bounding box, COM = center of mass
 */
public class BuildingFinder
{
    /**
     * For MBR, four coordinates are:
     *     rMin, cMin----rMin, cMax
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     rMax, cMin----rMax, cMax
     * @param img
     * @param numOfBuildings
     */
    public static int[][] findMBR(int[][] img, int numOfBuildings)
    {
        int H = img.length, W = img[0].length;

        /** store MBR coordinates **/
        int[] rMin = new int[numOfBuildings]; // building #1 takes 0, and so on
        int[] rMax = new int[numOfBuildings];
        int[] cMin = new int[numOfBuildings];
        int[] cMax = new int[numOfBuildings];

        /** initialize MBR coordinates **/
        for (int i = 0; i < rMin.length; i++) {
            rMin[i] = H;
        }
        for (int i = 0; i < rMax.length; i++) {
            cMin[i] = W;
        }

        /** scan image **/
        for (int r = 0; r < img.length; r++)
            for (int c = 0; c < img[0].length; c++) {
                if (img[r][c] == 0) { // skip black space
                } else if (rMin[img[r][c]-1] > r) {
                    rMin[img[r][c]-1] = r;
                } else if (rMax[img[r][c]-1] < r) {
                    rMax[img[r][c]-1] = r;
                } else if (cMin[img[r][c]-1] > c) {
                    cMin[img[r][c]-1] = c;
                } else if (cMax[img[r][c]-1] < c) {
                    cMax[img[r][c]-1] = c;
                }
            }

        /** assemble solution **/
        int res[][] = new int[4][];
        res[0] = rMin; res[1] = rMax; res[2] = cMin; res[3] = cMax;
        System.out.println(Arrays.toString(rMin));
        System.out.println(Arrays.toString(rMax));
        System.out.println(Arrays.toString(cMin));
        System.out.println(Arrays.toString(cMax));
        return res;
    }

    public static void displayMBR(BufferedImage image, int[][] MBRCoordinates)
    {
        int[] rMin = MBRCoordinates[0], rMax = MBRCoordinates[1], cMin = MBRCoordinates[2], cMax = MBRCoordinates[3];
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        int r = 2;
        /** for each building, draw MBR **/
        for (int i = 0; i < MBRCoordinates[0].length; i++) {
            /** upper left **/
            g.drawOval(cMin[i]-r, rMin[i]-r, 2 * r, 2 * r);
            /** upper right **/
            g.drawOval(cMax[i]-r, rMin[i]-r, 2*r, 2*r);
            /** lower left **/
            g.drawOval(cMin[i]-r, rMax[i]-r, 2*r, 2*r);
            /** lower right **/
            g.drawOval(cMax[i]-r, rMax[i]-r, 2*r, 2*r);
        }
        ShowImg.show(image);
    }

    public static void main(String[] args)
    {
        int[][] MBRCoordinates = BuildingFinder.findMBR(PGMReader.read("ass3-labeled.pgm"), 27);
        BuildingFinder.displayMBR(ImageReader.read("ass3-campus.png"), MBRCoordinates);
    }
}
