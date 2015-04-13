import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * The area that displays the map, and responds to user click.
 */
public class DrawingArea extends JPanel
{
    BufferedImage image;
    Graphics2D g2d;
    int x, y;

    public DrawingArea(String path)
    {
        image = ImageReader.read(path);
        DrawingAreaMouseAdapter mouseAdapter = new DrawingAreaMouseAdapter();
        this.addMouseListener(mouseAdapter);
        x = 10;
        y = 10;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
        g.setColor(Color.RED);
        System.out.printf("x=%d y=%d\n", x, y);
        g.drawOval(x, y, 10, 10);
    }

    class DrawingAreaMouseAdapter extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            System.out.println(e.getX() + " " + e.getY());
            x = e.getX();
            y = e.getY();
            repaint();
        }
    }
}
