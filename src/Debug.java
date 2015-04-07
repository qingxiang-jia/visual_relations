/**
 * Debugging class.
 */
public class Debug
{
    static final char[] map = new char[]{'1' ,'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public static void print2DArray(int[][] matrix)
    {
        int R = matrix.length, C = matrix[0].length;
        for (int r = 0; r < R; r++) {
            for (int c = 0; c < C; c++) {
                if (matrix[r][c] == 0)
                    System.out.print(' ');
                else
                    System.out.print(map[matrix[r][c] - 1]);
            }
            System.out.println();
        }
    }
}
