import java.util.*;

/**
 * After BuildingFinder finds for each building the MBR (coordinates),
 * area, center of mass, this class starts and extracts characteristics
 * for each building (p.2 ass3, largely revised though).
 *
 * Idea: there are over 30 features used to describe each building.
 * Each building only gets a subset of the features to be identified
 * visually on the map. To make the code neat, all 34 features are
 * grouped into 11 groups from A to K by implementation similarity
 * and difficulty, i.e., A is the easiest to implement and K is the
 * hardest to implement. Yes, we can use feature vector, but since
 * the features are rather sparse, I chose to use Set instead.
 */
public class FeatureExtractor
{
    /** features **/
    Set<Integer>[] feature; // total 34

    /** store MBR coordinates **/
    int[] rMin, rMax, cMin, cMax;

    /** area of each building **/
    int[] area;

    /** centers of mass **/
    int[][] centroids;

    /** int[][] version of ass3-labeled.pgm **/
    int[][] img;

    @SuppressWarnings("unchecked")
    public FeatureExtractor(int[][] MBRCoordinates, int[] area, int[][] centroids, int[][] img)
    {
        rMin = MBRCoordinates[0]; rMax = MBRCoordinates[1]; cMin = MBRCoordinates[2]; cMax = MBRCoordinates[3];
        this.area = area;
        this.centroids = centroids;
        this.img = img;
        feature = new HashSet[area.length];
        for (int i = 0; i < area.length; i++) {
            feature[i] = new HashSet<>();
        }
    }

    /**
     * Call to extract all features for all buildings.
     */
    public void extract()
    {
        for (int i = 0; i < area.length; i++) {
            featureA(i);
            featureB(i);
            featureC(i);
            featureD(i);
            featureE(i);
            featureF(i);
            featureG(i);
        }
        // display results
        for (Set<Integer> set : feature) {
            System.out.println(set.toString());
        }
    }

    /**
     * Extracts features 0, 1, 2 for a building.
     * very small 0
     * medium     1
     * very big   2
     */
    private void featureA(int building)
    {
        if (area[building] < 400) {
            feature[0].add(building);
        } else if (700 < area[building] && area[building] < 2200) {
            feature[1].add(building);
        } else if (area[building] > 2500) {
            feature[2].add(building);
        }
    }

    /**
     * Extracts features 3, 4 for a building.
     * longest 3
     * long    4
     * @param building
     */
    private void featureB(int building)
    {
        int max = Math.max(rMax[building] - rMin[building], cMax[building] - cMin[building]);
        if (max > 250) {
            feature[3].add(building);
        } else if (max > 150) {
            feature[4].add(building);
        }
    }

    /**
     * Extracts feature 5, 6 for a building.
     * short horizontal long vertical 5
     * long horizontal short vertical 6
     * For MBR, four coordinates are:
     *     rMin, cMin----rMin, cMax
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     rMax, cMin----rMax, cMax
     * @param building
     */
    private void featureC(int building)
    {
        int h = cMax[building] - cMin[building];
        int v = rMax[building] - rMin[building];
        float hvRatio = h / (float) v;
        if (hvRatio < 0.8) { // ignoring non-obvious hvRatio, i.e., [0.8, 1.3]
            feature[5].add(building);
        } else if (hvRatio > 1.3) {
            feature[6].add(building);
        }
    }

    /**
     * Extracts feature 7, 8, 9 for a building.
     * perfect square    7
     * almost square     8
     * perfect rectangle 9
     * For MBR, four coordinates are:
     *     rMin, cMin----rMin, cMax
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     rMax, cMin----rMax, cMax
     * @param building
     */
    private void featureD(int building)
    {
        int h = cMax[building] - cMin[building] + 1;
        int v = rMax[building] - rMin[building] + 1;
        int rowMin = rMin[building], rowMax = rMax[building];
        int colMin = cMin[building], colMax = cMax[building];
        /** rough check: if MBR is square **/
        checkingSquare:
        if (h == v) {
            /** check if is square, time consuming **/
            for (int r = rowMin; r <= rowMax; r++)
                for (int c = colMin; c <= colMax; c++)
                    if (img[r][c] - 1 != building)
                        break checkingSquare;
            feature[7].add(building);
        }
        /** rough check: if MBR is almost square **/
        double hvRatio = h / (double)v;
        checkingAlmostSquare:
        if (1.0 < hvRatio && hvRatio < 1.2) {
            /** check if is all filled, time consuming **/
            for (int r = rowMin; r <= rowMax; r++)
                for (int c = colMin; c <= colMax; c++)
                    if (img[r][c] - 1 != building)
                        break checkingAlmostSquare;
            feature[8].add(building);
        }
        /** check if is perfect rectangle, time consuming **/
        if (hvRatio != 1.0) { // excluding square
            for (int r = rowMin; r <= rowMax; r++)
                for (int c = colMin; c <= colMax; c++)
                    if (img[r][c] - 1 != building)
                        return ;
            feature[9].add(building);
        }
    }

    /**
     * Extracts feature 10, 11, 12.
     * top 1/3 of the map    10
     * middle 1/3 of the map 11
     * bottom 1/3 of the map 12
     * For MBR, four coordinates are:
     *     rMin, cMin----rMin, cMax
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     rMax, cMin----rMax, cMax
     */
    private void featureE(int building)
    {
        int row = centroids[building][0]; // [building_id] -> int[]{r, c}
        if (0 <= row && row < 165) {
            feature[10].add(building);
        } else if (165 <= row && row < 320) {
            feature[11].add(building);
        } else if (330 <= row) { // notice not every building is included
            feature[12].add(building);
        }
    }

    /**
     * Extracts feature 13, 14, 15, 16, 17.
     * at north border 13
     * at south border 14
     * at west border  15
     * at east border  16
     * at center       17
     * @param building
     */
    private void featureF(int building)
    {
        int R = img.length, C = img[0].length;
        /** collect buildings at north & south border **/
        for (int c = 0; c < C; c += 30) {
            if (img[13][c] != 0)
                feature[13].add(img[13][c] - 1);
            if (img[475][c] != 0)
                feature[14].add(img[475][c] - 1);
        }
        /** collect buildings at west & east border **/
        for (int r = 0; r < R; r += 30) {
            if (img[r][15] != 0)
                feature[15].add(img[r][15] - 1);
            if (img[r][260] != 0)
                feature[16].add(img[r][260] - 1);
        }
        /** collect buildings at center **/
        for (int r = 200; r < 280; r += 10)
            for (int c = 100; c < 170; c += 10)
                if (img[r][c] != 0)
                    feature[17].add(img[r][c] - 1);
    }

    /**
     * Extracts feature 18.
     * one inner hole 18
     * Given a building id and MBR, check if there is area with color 0 by area with color building + 1.
     * Algorithm: BFS. For each pixel in MBR, using BFS to find connected component (color 0) that is not
     * touching the border of the MBR.
     *     rMin, cMin----rMin, cMax
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     |                      |
     *     rMax, cMin----rMax, cMax
     * @param building
     * A point is represented as an integer array, whose length is 2, the first element is row #, the second
     * is column #.
     */
    private void featureG(int building)
    {
        int rowMin = rMin[building], rowMax = rMax[building], colMin = cMin[building], colMax = cMax[building];
        for (int r = rowMin; r <= rowMax; r++)
            for (int c = colMin; c <= colMax; c++) {
                boolean[][] visited = new boolean[rowMax - rowMin + 1][colMax - colMin + 1];
                findConnectedComponent:
                if (img[r][c] == 0 && !visited[r - rowMin][c - colMin]) {
                    Queue<int[]> queue = new LinkedList<>();
                    queue.add(new int[]{r, c});
                    while (!queue.isEmpty()) {
                        int[] pixel = queue.remove();
                        visited[pixel[0] - rowMin][pixel[1] - colMin] = true; // mark as visited
                        /** check if is at border **/
                        if (pixel[0] == rowMin || pixel[0] == rowMax || pixel[1] == colMin || pixel[1] == colMax) {
                            /** for efficiency, all remaining pixels should be marked as visited as well **/
                            while (!queue.isEmpty()) {
                                int[] pixelUnwanted = queue.remove();
                                visited[pixelUnwanted[0] - rowMin][pixelUnwanted[1] - colMin] = true;
                            }
                            break findConnectedComponent;
                        }
                        /** add up **/
                        if ((pixel[0] - 1 >= rowMin)
                                && (!visited[(pixel[0] - 1) - rowMin][pixel[1] - colMin])
                                && (img[pixel[0] - 1][pixel[1]] == 0)) {
                            queue.add(new int[]{pixel[0] - 1, pixel[1]});
                            visited[(pixel[0] - 1) - rowMin][pixel[1] - colMin] = true;
                        }
                        /** add down **/
                        if ((pixel[0] + 1 <= rowMax)
                                && (!visited[(pixel[0] + 1) - rowMin][pixel[1] - colMin])
                                && (img[pixel[0] + 1][pixel[1]] == 0)) {
                            queue.add(new int[]{pixel[0] + 1, pixel[1]});
                            visited[(pixel[0] + 1) - rowMin][pixel[1] - colMin] = true;
                        }
                        /** add left **/
                        if ((pixel[1] - 1 >= colMin)
                                && (!visited[(pixel[0]) - rowMin][(pixel[1] - 1) - colMin])
                                && (img[pixel[0]][pixel[1] - 1] == 0)) {
                            queue.add(new int[]{pixel[0], pixel[1] - 1});
                            visited[pixel[0] - rowMin][(pixel[1] - 1) - colMin] = true;
                        }
                        /** add right **/
                        if ((pixel[1] + 1 <= colMax)
                                && (!visited[(pixel[0]) - rowMin][(pixel[1] + 1) - colMin])
                                && (img[pixel[0]][pixel[1] + 1] == 0)) {
                            queue.add(new int[]{pixel[0], pixel[1] + 1});
                            visited[pixel[0] - rowMin][(pixel[1] + 1) - colMin] = true;
                        }
                    }
                    if (queue.isEmpty()) {
                        /** must have found a hole **/
                        feature[18].add(building);
                        /** since we only want to know if there is at least a hole **/
                        return;
                    }
                }
            }
    }

    /**
     * Extracts feature 19, 20, 21, 22.
     * one dent on west
     * @param building
     */
    private void featureH(int building)
    {

    }

    // testing
    public static void main(String[] args)
    {
        /** deserialize required files **/
        int[][] img = (int[][]) IOUtil.deserialize("img.ser");
        int[][] MBRCoordinates = (int[][]) IOUtil.deserialize("MBRCoordinates.ser");
        int[] area = (int[]) IOUtil.deserialize("area.ser");
        int[][] centroids = (int[][]) IOUtil.deserialize("centroids.ser");

        FeatureExtractor fExtractor = new FeatureExtractor(MBRCoordinates, area, centroids, img);
        /** begin extraction **/
        fExtractor.extract();
    }
}
