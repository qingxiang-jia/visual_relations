import java.io.Serializable;

/**
 * Represents a pixel.
 */
public class Pixel implements Serializable
{
    int[] coordinates;

    public Pixel(int r, int c)
    {
        coordinates = new int[]{r, c};
    }

    public int getRow()
    {
        return coordinates[0];
    }

    public int getCol()
    {
        return coordinates[1];
    }

    public int getX()
    {
        return getCol();
    }

    public int getY()
    {
        return getRow();
    }
}
