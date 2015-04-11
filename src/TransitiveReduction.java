/**
 * Perform transitive reduction on four directional relationships + near.
 */
public class TransitiveReduction
{
    /**
     * Performs transitive reduction algorithm.
     * Idea:
     * foreach x in graph.vertices:
     *   foreach y in graph.vertices:
     *     foreach z in graph.vertices:
     *       delete edge xz if edges xy and yz exist
     */
    public static void perform(boolean[][] directionRelation)
    {
        int numOfBuilding = directionRelation.length;
        for (int i = 0; i < numOfBuilding; i++)
            for (int j = 0; j < numOfBuilding; j++)
                for (int k = 0; k < numOfBuilding; k++)
                    if (directionRelation[i][j] && directionRelation[j][k]) {
                        System.out.printf("No need for relation %d->%d because you have %d->%d & %d->%d\n", i, k, i, j, j, k);
                    }
    }

    public static void main(String[] args)
    {
        /** deserialize directional relationships **/
        boolean[][] north = (boolean[][]) IOUtil.deserialize("north.ser");
        boolean[][] south = (boolean[][]) IOUtil.deserialize("south.ser");
        boolean[][] west = (boolean[][]) IOUtil.deserialize("west.ser");
        boolean[][] east = (boolean[][]) IOUtil.deserialize("east.ser");

        /** run reduction **/
        System.out.println("north");
        TransitiveReduction.perform(north);
        System.out.println("\n\nsouth");
        TransitiveReduction.perform(south);
        System.out.println("\n\nwest");
        TransitiveReduction.perform(west);
        System.out.println("\n\neast");
        TransitiveReduction.perform(east);
    }
}
