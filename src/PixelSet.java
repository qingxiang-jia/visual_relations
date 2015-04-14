import java.io.Serializable;
import java.util.HashSet;

/**
 * Represents a set of pixels.
 */
public class PixelSet implements Serializable
{
    HashSet<Pixel> set;
    public PixelSet()
    {
        set = new HashSet<>();
    }

    public void add(Pixel p)
    {
        set.add(p);
    }

    public Pixel[] getAllPixels()
    {
        Pixel[] allPixels = new Pixel[set.size()];
        return set.toArray(allPixels);
    }
}
