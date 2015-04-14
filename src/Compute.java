/**
 * Utility class that holds all computation-related methods.
 */
public class Compute
{
    public static int dist(int row1, int col1, int row2, int col2)
    {
        return (int) Math.sqrt(Math.pow((row1 - row2), 2) + Math.pow((col1 - col2), 2)); // not ^!!
    }
}
