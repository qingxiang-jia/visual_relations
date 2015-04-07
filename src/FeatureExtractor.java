import java.util.HashSet;
import java.util.Set;

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

    // testing
    public static void main(String[] args)
    {
        /** deserialize required files **/
        int[][] img = (int[][]) IOUtil.deserialize("img.ser");
        int[][] MBRCoordinates = (int[][]) IOUtil.deserialize("MBRCoordinates.ser");
        int[] area = (int[]) IOUtil.deserialize("area.ser");
        System.out.println(area[13]);
        int[][] centroids = (int[][]) IOUtil.deserialize("centroids.ser");

        FeatureExtractor fExtractor = new FeatureExtractor(MBRCoordinates, area, centroids, img);
        /** begin extraction **/
        fExtractor.extract();
    }

}
