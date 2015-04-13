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
}
