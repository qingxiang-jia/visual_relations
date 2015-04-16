import java.util.Arrays;
import java.util.Set;

/**
 * Language generator that generates 1) the short description of where used in Step 2;
 * 2) the full-sentence description of where to go used in Step 4.
 */
public class LangGen
{
    /**
     * Store pruned spatial relationship for each pixel.
     */
    int[][][] reducedMapping; // [r][c][N S W E Near] -1 represents none, else the value is building id
    PixelSet[][][][][] reducedMappingInverse; // [N][S][W][E][Near]

    /**
     * Shortest path from any building to any building.
     */
    int[][][] paths;

    /**
     * Store spatial relationship for each pixel.
     */
    boolean[][][] mappedNorth, mappedSouth, mappedWest, mappedEast; // [r][c][building]

    /**
     * features
     */
    Set<Integer>[] feature; // total 34 features, feature[feature_id] = {all matched buildings}

    /**
     * Reduced building-wise spatial relationship.
     */
    boolean[][] north;
    boolean[][] south;
    boolean[][] west;
    boolean[][] east;

    /**
     * int[][] version of ass3-labeled.pgm *
     */
    int[][] img;

    StringBuilder sb;


    public final static int NO_BUILDING_NAME = 0, SHOW_BUILDING_NAME = 1;
    final static String[] NSWE = new String[]{"north", "south", "west", "east"};
    final static int[] reverseNSWE = new int[]{1, 0, 3, 2};
    final static String[] featureDescription = new String[]{
            "very small",
            "medium sized",
            "very big",
            "longest",
            "long",
            "short horizontal long vertical",
            "long horizontal short vertical",
            "perfect square",
            "almost square",
            "perfect rectangle",
            "top 1/3 of the map",
            "middle 1/3 of the map",
            "bottom 1/3 of the map",
            "at north border",
            "at south border",
            "at west border",
            "at east border",
            "at center",
            "has one inner hole ",
            "has one dent on west east sides",
            "has only one dent on north side",
            "has two dents on west side",
            "has four dents on east side",
            "has only one bump on west side",
            "overall upside down L",
            "overall C reversed left to right",
            "overall L",
            "overall L reversed left to right",
            "has three corners chewed off",
            "has four corners chewed off like a square",
            "has four corners chewed off not like a square",
            "has bottom two corners chewed off like a square, top two corner chewed off not like a square",
            "has west two corners chewed off like a square, east two corner chewed off not like a square",
            "has bottom two corners chewed off irregularly"
    };
    final static String[] buildingName = new String[]{
            "Pupin",
            "Schapiro CEPSR",
            "Mudd, Engineering Terrace, Fairchild & Computer Science",
            "Physical Fitness Center",
            "Gymnasium & Uris",
            "Schermerhorn",
            "Chandler & Havemeyer",
            "Computer Center",
            "Avery",
            "Fayerweather",
            "Mathematics",
            "Low Library",
            "St. Paul's Chapel",
            "Earl Hall",
            "Lewisohn",
            "Philosophy",
            "Buell & Maison Francaise",
            "Alma Mater",
            "Dodge",
            "Kent",
            "College Walk",
            "Journalism & Furnald",
            "Hamilton, Hartley, Wallach & John Jay",
            "Lion's Court",
            "Lerner Hall",
            "Butler Library",
            "Carman"
    };

    /**
     * arr is part of reducedMapping object.
     * @param arr [N S W E Near]
     * @return
     */
    static String tellWhere(int[] arr)
    {
        /** regular case **/
        String part1 = null, part2 = null;
        for (int i = 0; i < arr.length - 1; i++) { // only scan the four directions
            if (arr[i] != 0) { // 0 is null
                if (part1 == null) {
                    part1 = NSWE[i] + " of " + buildingName[arr[i]-1];
                } else if (part2 == null) {
                    part2 = NSWE[i] + " of " + buildingName[arr[i]-1];
                } else {
                    break; // all parts have been assigned
                }
            }
        }
        /** process near **/
        String part3 = null;
        if (arr[4] != 0) {
            part3 = "near " + buildingName[arr[4] - 1];
        }
        if (part3 == null) {
            return part1 + " and " + part2;
        } else if (part2 == null){
            return part1 + " and " + part3;
        } else {
            return part1 + ", " + part2 + ", and " + part3;
        }
    }

    public LangGen(int[][][] reducedMapping, PixelSet[][][][][] reducedMappingInverse, int[][][] paths,
                   boolean[][][] mappedNorth, boolean[][][] mappedSouth, boolean[][][] mappedWest, boolean[][][] mappedEast,
                   Set<Integer>[] feature,
                   boolean[][] northReduced, boolean[][] southReduced, boolean[][] westReduced, boolean[][] eastReduced,
                   int[][] img)
    {
        this.reducedMapping = reducedMapping;
        this.reducedMappingInverse = reducedMappingInverse;
        this.paths = paths;

        this.mappedNorth = mappedNorth;
        this.mappedSouth = mappedSouth;
        this.mappedWest = mappedWest;
        this.mappedEast = mappedEast;

        this.feature = feature;

        this.north = northReduced;
        this.south = southReduced;
        this.west = westReduced;
        this.east = eastReduced;

        this.img = img;

        sb = new StringBuilder();
    }

    /**
     * First, find the closest buildings for source and target.
     * Second, generate natural language guidance to source building as well as terminal guidance.
     * Third, following the shortest path given by paths, generate natural language guidance.
     * @return
     */
    public String tellDirections(int rowSrc, int colSrc, int rowTgt, int colTgt, int mode)
    {
        sb.setLength(0); // reset

        /** find source and target building **/
        int srcBuilding = reducedMapping[rowSrc][colSrc][4];
        int tgtBuilding = reducedMapping[rowTgt][colTgt][4];

        /** see if initial guidance is necessry **/
        boolean srcIsInBuilding = false;
        if (img[rowSrc][colSrc] != 0) {
            srcIsInBuilding = true;
            srcBuilding = img[rowSrc][colSrc] - 1;
        }

        /** see if terminal guidance is necessary **/
        boolean tgtIsInBuilding = false;
        if (img[rowTgt][colTgt] != 0) {
            tgtIsInBuilding = true;
            tgtBuilding = img[rowTgt][colTgt] - 1;
        }

        /** debug **/
        System.out.printf("Debug: src=%s tgt=%s\n", buildingName[srcBuilding], buildingName[tgtBuilding]);
        System.out.println("srcIsInBuilding=");
        System.out.println(srcIsInBuilding);
        System.out.println("tgtIsInBuilding=");
        System.out.println(tgtIsInBuilding);

        /** get shortest path **/
        int[] path = paths[srcBuilding][tgtBuilding];

        /** generate initial guidance **/
        if (!srcIsInBuilding) {
            int initDirection;
            if (mappedNorth[rowSrc][colSrc][srcBuilding]) {
                initDirection = 0; // north
            } else if (mappedSouth[rowSrc][colSrc][srcBuilding]) {
                initDirection = 1; // south
            } else if (mappedWest[rowSrc][colSrc][srcBuilding]) {
                initDirection = 2; // west
            } else {
                initDirection = 3; // east
            }
            /** reverse direction **/
            initDirection = reverseNSWE[initDirection];

            /** generate initial guidance **/
            appendDirectionForBuilding(initDirection);
            if (mode == SHOW_BUILDING_NAME)
                appendName(srcBuilding);
            appendWhatForBuilding(srcBuilding);
        }

        /** generate guidance based on path **/
        for (int building = 0; building < path.length - 1; building++) {
            sb.append("\n\ngenerate guidance based on path\n");
            appendDirectionForBuilding(findDirection(path[building], path[building + 1]));
            if (mode == SHOW_BUILDING_NAME)
                appendName(path[building + 1]);
            appendWhatForBuilding(path[building + 1]);
        }
        System.out.println(Arrays.toString(path));

        /** generate terminal guidance **/
        if (!tgtIsInBuilding) {
            int terminalDirection;
            if (mappedNorth[rowTgt][colTgt][tgtBuilding]) {
                terminalDirection = 0; // north
            } else if (mappedSouth[rowTgt][colTgt][tgtBuilding]) {
                terminalDirection = 1; // south
            } else if (mappedWest[rowTgt][colTgt][tgtBuilding]) {
                terminalDirection = 2; // west
            } else {
                terminalDirection = 3; // east
            }
            /** generate terminal guidance **/
            appendWhereForPixel(rowTgt, colTgt, terminalDirection);
        }

        return sb.toString();
    }

    private void appendName(int building)
    {
        sb.append("Building Name: ");
        sb.append(buildingName[building]);
        sb.append("\n");
    }

    /**
     * Append the description of the "what" of a building.
     * @param building
     */
    private void appendWhatForBuilding(int building)
    {
        sb.append("Building features:\n");
        for (int i = 0; i < feature.length; i++) {
            if (feature[i].contains(building)) {
                sb.append(featureDescription[i]);
                sb.append("\n");
            }
        }
    }

    /**
     * Append the description of the "where" of a pixel.
     * @param row
     * @param col
     */
    private void appendWhereForPixel(int row, int col, int directionToThisPixel)
    {
        sb.append("\n\nGo ");
        sb.append(NSWE[directionToThisPixel]);
        sb.append(" where it is ");
        sb.append(tellWhere(reducedMapping[row][col]));
    }


    /**
     * Form "Go {direction} to reach a building".
     * @param direction
     */
    private void appendDirectionForBuilding(int direction)
    {

        sb.append(" to reach a building.\n");
    }

    /**
     * When generate guidance following shortest path, this is used to find out the direction
     * of the next building with respect to current building.
     * @param building
     * @param nextBuilding
     * @return
     */
    private int findDirection(int building, int nextBuilding)
    {
        /** check if it's north **/
        if (north[building][nextBuilding])
            return 0;
        /** check if it's south **/
        if (south[building][nextBuilding])
            return 1;
        /** check if it's west **/
        if (west[building][nextBuilding])
            return 2;
        /** check if it's east **/
        if (east[building][nextBuilding])
            return 3;
        return -1; // fail fast
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        /** deserialize precomputed data **/
        int[][][] reducedMapping = (int[][][]) IOUtil.deserialize("reducedMapping.ser");
        System.out.println("Deserialized reducedMapping");
        PixelSet[][][][][] reducedMappingInverse = (PixelSet[][][][][]) IOUtil.deserialize("reducedMappingInverse.ser");
        System.out.println("Deserialized reducedMappingInverse");

        int[][][] paths = (int[][][]) IOUtil.deserialize("shortestPath.ser");
        System.out.println("Deserialized paths");

        boolean[][][] mappedNorth = (boolean[][][]) IOUtil.deserialize("mappedNorth.ser");
        System.out.println("Deserialized mappedNorth");
        boolean[][][] mappedSouth = (boolean[][][]) IOUtil.deserialize("mappedSouth.ser");
        System.out.println("Deserialized mappedSouth");
        boolean[][][] mappedWest = (boolean[][][]) IOUtil.deserialize("mappedWest.ser");
        System.out.println("Deserialized mappedWest");
        boolean[][][] mappedEast = (boolean[][][]) IOUtil.deserialize("mappedEast.ser");
        System.out.println("Deserialized mappedEast");

        Set<Integer>[] feature = (Set<Integer>[]) IOUtil.deserialize("feature.ser");
        System.out.println("Deserialized feature");

        boolean[][] north = (boolean[][]) IOUtil.deserialize("northReduced.ser");
        System.out.println("Deserialized northReduced");
        boolean[][] south = (boolean[][]) IOUtil.deserialize("southReduced.ser");
        System.out.println("Deserialized southReduced");
        boolean[][] west = (boolean[][]) IOUtil.deserialize("westReduced.ser");
        System.out.println("Deserialized westReduced");
        boolean[][] east = (boolean[][]) IOUtil.deserialize("eastReduced.ser");
        System.out.println("Deserialized v");

        int[][] img = (int[][]) IOUtil.deserialize("img.ser");
        System.out.println("Deserialized img");

        LangGen langGen = new LangGen(reducedMapping, reducedMappingInverse, paths,
                mappedNorth, mappedSouth, mappedWest, mappedEast,
                feature,
                north, south, west, east, img);
        System.out.println(langGen.tellDirections(411, 44, 164, 137, SHOW_BUILDING_NAME));
    }
}
