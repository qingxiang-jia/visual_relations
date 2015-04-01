import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Displays a BufferedImage.
 */
public class ShowImg
{
    public static void show(BufferedImage image)
    {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setVisible(true);
    }

    public static void show(String path)
    {
        show(ImageReader.read(path));
    }

    public static void main(String[] args)
    {
        ShowImg.show("ass3-campus.png");
    }
}
