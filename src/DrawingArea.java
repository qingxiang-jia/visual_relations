import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * The area that displays the map, and responds to user click.
 */
public class DrawingArea extends JPanel
{
    BufferedImage image;
    Graphics2D g2d;
    int x, y;

    /**
     * Store pruned spatial relationship for each pixel.
     */
    int[][][] reducedMapping; // [r][c][N S W E Near] -1 represents none, else the value is building id
    PixelSet[][][][][] reducedMappingInverse; // [N][S][W][E][Near]

    /* cloud to draw */
    Pixel[] cloud;

    public DrawingArea(String path, int[][][] reducedMapping, PixelSet[][][][][] reducedMappingInverse)
    {
        image = ImageReader.read(path);
        DrawingAreaMouseAdapter mouseAdapter = new DrawingAreaMouseAdapter();
        this.addMouseListener(mouseAdapter);
        x = 10;
        y = 10;

        this.reducedMapping = reducedMapping;
        this.reducedMappingInverse = reducedMappingInverse;
        cloud = null;
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);

        /** draw cloud **/
        g.setColor(Color.GREEN);
        if (cloud != null) {
            for (Pixel pixel : cloud) {
                g.drawRect(pixel.getX(), pixel.getY(), 1, 1);
            }
        }

        g.setColor(Color.RED);
        g.drawOval(x, y, 5, 5);
    }

    class DrawingAreaMouseAdapter extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            x = e.getX();
            y = e.getY();
            System.out.println(x + " " + y);

            /** generate cloud **/
            if (reducedMappingInverse[reducedMapping[y][x][0]]
                    [reducedMapping[y][x][1]]
                    [reducedMapping[y][x][2]]
                    [reducedMapping[y][x][3]]
                    [reducedMapping[y][x][4]] != null) {
                cloud = reducedMappingInverse[reducedMapping[y][x][0]]
                        [reducedMapping[y][x][1]]
                        [reducedMapping[y][x][2]]
                        [reducedMapping[y][x][3]]
                        [reducedMapping[y][x][4]].getAllPixels();
                System.out.println(Arrays.toString(reducedMapping[y][x]));
                System.out.println(LangGen.tellWhereShort(reducedMapping[y][x])); // inform the user "where"
            } else {
                cloud = null;
                System.out.println("no cloud for this pixel");
            }


            repaint();
        }
    }
}
