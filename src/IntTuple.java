/**
 * Represents a tuple of two integers. When sorting, sort based on b.
 */
public class IntTuple implements Comparable<IntTuple>
{
    int a, b;

    public IntTuple(int a, int b)
    {
        this.a = a;
        this.b = b;
    }

    public int compareTo(IntTuple other)
    {
        if (other == null)
            return 1;
        else if (this.b - other.b > 0) {
            return 1;
        } else if (this.b - other.b < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
