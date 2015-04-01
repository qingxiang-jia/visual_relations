import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Reads in an image file; constructs a BufferedImage out of it.
 */
public class ImageReader
{
    public static BufferedImage read(String path)
    {
        File imageFile = null;
        BufferedImage image = null;
        try {
            imageFile = new File(path);
            image = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
