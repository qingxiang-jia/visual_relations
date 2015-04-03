/**
 * After BuildingFinder finds for each building the MBR (coordinates),
 * area, center of mass, this class starts and extracts characteristics
 * for each building (p.2 ass3).
 */
public class FeatureExtractor
{
    /** feature thresholds **/
    public static final float THLD_AREA_SMALL_MIDDLE = 0.33f;
    public static final float THLD_AREA_MIDDLE_LARGE = 0.66f;
    public static final float THLD_THIN_RATIO = 0.3f; // shorter_length/longer_length
    //todo

    /**
     * Feature vector (binary): boolean[32]
     * small 0, smallest 1, medium 2, large 3, largest 4,
     * long 5, longest 6, thin 7, thinnest 8
     * square 9, rectangular 10,
     * straightBoundary 11, jaggedBoundary 12, hasBumps 13, hasDents 14
     * singleBuilding 15, multipleBuilding 16
     * I-shaped 17, L-shaped 18, C-shaped 19, partlyCurved 20
     * cornerChewedOff 21
     * symmetricEastWest 22, symmetricNorthSouth 23, irregularlyShaped 24
     * orientedEastWest 25, orientedNorthSouth 26
     * centrallyLocated 27, onBorder 28
     * northernmost 29, southernmost 30, easternmost 31, westernmost 32
     */


    /**
     * Call methods to get feature vector filled.
     * @param img int[][] type, converted from ass3-labeled.pgm
     * @param MBRCoordinates int[][] type, MBRCoordinates[building_id][row, col]
     * @param area int[] type, area[building_id]
     * @param centroids int[][] type, centroids[building_id][row, col]
     * @return featureVectors[building_id][featureVector]
     */
    public static boolean[][] extractAll32(int[][] img, int[][] MBRCoordinates, int[] area, int[][] centroids)
    {
        int[] rMin = MBRCoordinates[0], rMax = MBRCoordinates[1], cMin = MBRCoordinates[2], cMax = MBRCoordinates[3];
        /** get global metrics **/
        /** area extrema **/
        int maxArea = area[ArrUtil.findMax(area)];
        int minArea = area[ArrUtil.findMin(area)];

        /** length extrema **/
        int[] lengthV = new int[MBRCoordinates.length];
        int[] lengthH = new int[MBRCoordinates.length];
        for (int i = 0; i < lengthV.length; i++) {
            lengthV[i] = rMax[i] - rMin[i];
            lengthH[i] = cMax[i] - cMin[i];
        }
        int lengthVMax = lengthV[ArrUtil.findMax(lengthV)];
        int lengthVMin = lengthV[ArrUtil.findMin(lengthV)];
        int lenghtHMax = lengthH[ArrUtil.findMax(lengthH)];
        int lenghtHMin = lengthH[ArrUtil.findMin(lengthH)];

        /** thin ratio extrema **/
        float[] thinRatios = new float[MBRCoordinates.length];
        for (int i = 0; i < MBRCoordinates.length; i++) {
            int longer, shorter;
            if (lengthV[i] > lengthH[i]) {
                longer = lengthV[i];
                shorter = lengthH[i];
            } else {
                longer = lengthH[i];
                shorter = lengthH[i];
            }
            thinRatios[i] = shorter / (float) longer;
        }
        float thinnest = thinRatios[ArrUtil.findMin(thinRatios)];

        /** for each building, extract all features **/
        boolean[][] features = new boolean[MBRCoordinates.length][];
        for (int i = 0; i < MBRCoordinates.length; i++) {
            boolean[] feature = new boolean[32];

        }

        return null;
    }

    public static void featureSize(boolean[] feature, int area, int maxArea, int minArea, int smallMiddle, int middleLarge)
    {
        // todo if
    }

    public static void featureLongThin(boolean[] feature)
    {
    }

    public static void featureSqRect(boolean[] feature)
    {
    }

    public static void featureBoundary(boolean[] feature)
    {
    }

    public static void featureSglMultBldg(boolean[] feature)
    {
    }

    public static void featureShape(boolean[] feature)
    {
    }

    public static void featureCorner(boolean[] feature)
    {
    }

    public static void featureSymmetry(boolean[] feature)
    {
    }

    public static void featureOrient(boolean[] feature)
    {
    }

    public static void featureLocation(boolean[] feature)
    {
    }
}
