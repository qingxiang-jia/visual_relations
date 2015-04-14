/**
 * Represents a tuple of two integers. When sorting, sort based on c.
 */
public class IntTuple implements Comparable<IntTuple>
{
    int a, b, c;

    public IntTuple(int a, int b, int c)
    {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int compareTo(IntTuple other)
    {
        if (other == null)
            return 1;
        else if (this.c - other.c > 0) {
            return 1;
        } else if (this.c - other.c < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    public String toString()
    {
        return "(" + a + " " + b + " " + c +")";
    }
}
