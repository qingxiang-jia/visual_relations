/**
 * Language generator that generates 1) the short description of where used in Step 2;
 * 2) the full-sentence description of where to go used in Step 4.
 */
public class LangGen
{
    final static String[] NSWE = new String[]{"north", "south", "west", "east"};
    /**
     * arr is part of reducedMapping object.
     * @param arr [N S W E Near]
     * @return
     */
    final static String tellWhereShort(int[] arr)
    {
        String part1 = null, part2 = null;
        for (int i = 0; i < arr.length - 1; i++) { // only scan the four directions
            if (arr[i] != 0) { // 0 is null
                if (part1 == null) {
//                    part1 = NSWE[i] + " of " + Id2Name.getName(arr[i]);
                    part1 = NSWE[i] + " of " + (arr[i]-1);
                } else if (part2 == null) {
//                    part2 = NSWE[i] + " of " + Id2Name.getName(arr[i]);
                    part2 = NSWE[i] + " of " + (arr[i]-1);
                } else {
                    break; // all parts have been assigned
                }
            }
        }
        /** process near **/
        String part3 = null;
        if (arr[4] != 0) {
//            part3 = "near " + Id2Name.getName(arr[4]);
            part3 = "near " + (arr[4] - 1);
        }
        if (part3 == null) {
            return part1 + " and " + part2;
        } else {
            return part1 + ", " + part2 + ", and " + part3;
        }
    }
}
