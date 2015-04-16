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
    static final int INDIVIDUAL_CLOUD = 0, ST_PAIR = 1, NAV = 2;
    int mode;

    /**
     * Store pruned spatial relationship for each pixel.
     */
    int[][][] reducedMapping; // [r][c][N S W E Near] -1 represents none, else the value is building id
    PixelSet[][][][][] reducedMappingInverse; // [N][S][W][E][Near]

    /* currCloud to draw */
    Pixel[] currCloud;
    Pixel[] prevCloud;
    boolean isGreen;

    public DrawingArea(String path, int[][][] reducedMapping, PixelSet[][][][][] reducedMappingInverse, int mode)
    {
        image = ImageReader.read(path);
        DrawingAreaMouseAdapter mouseAdapter = new DrawingAreaMouseAdapter();
        this.addMouseListener(mouseAdapter);
        x = 10; y = 10;

        this.reducedMapping = reducedMapping;
        this.reducedMappingInverse = reducedMappingInverse;
        currCloud = null;

        this.mode = mode;
        if (mode == ST_PAIR || mode == NAV) {
            isGreen = true;
            currCloud = null;
            prevCloud = null;
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);

        /** draw cloud **/
        if (mode == INDIVIDUAL_CLOUD) {
            g.setColor(Color.GREEN);
            drawCloud(currCloud, g);
        } else if (mode == ST_PAIR) {
            if (isGreen) { // currCloud should be drawn green, prevCloud should be drawn red
                g.setColor(Color.GREEN);
                drawCloud(currCloud, g);
                g.setColor(Color.RED);
                drawCloud(prevCloud, g);
                isGreen = !isGreen; // flip the bit
            } else { // currCloud should be drawn red, prevCloud should be drawn green
                g.setColor(Color.RED);
                drawCloud(currCloud, g);
                g.setColor(Color.GREEN);
                drawCloud(prevCloud, g);
                isGreen = !isGreen;
            }
        } else if (mode == NAV) { // only draw cursor

        }

        g.setColor(Color.BLUE); // draw cursor
        g.drawOval(x, y, 2, 2);
    }

    public Pixel[] genCloud()
    {
        Pixel[] cloud = null;
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
        }
        return cloud;
    }

    public void drawCloud(Pixel[] cloud, Graphics g)
    {
        if (cloud != null)
            for (Pixel pixel : cloud)
                g.drawRect(pixel.getX(), pixel.getY(), 1, 1);
    }

    class DrawingAreaMouseAdapter extends MouseAdapter
    {
        @Override
        public void mouseClicked(MouseEvent e)
        {
            x = e.getX();
            y = e.getY();
            System.out.println(y + " " + x); // row col fashion

            if (mode == INDIVIDUAL_CLOUD) { // generates currCloud whenever user clicks
                /** generate currCloud **/
                currCloud = genCloud();
                System.out.println(Arrays.toString(reducedMapping[y][x]));
                System.out.println(LangGen.tellWhere(reducedMapping[y][x])); // inform the user "where"

                if (currCloud == null)
                    System.out.println("no cloud for this pixel");
            } else if (mode == ST_PAIR) { // generates green/red clouds alternating
                prevCloud = currCloud;
                currCloud = genCloud();
            } else if (mode == NAV) { // after determined source and target, only draw cursor
                if (prevCloud == null && currCloud == null) {// initial case
                    currCloud = genCloud();
                } else if (prevCloud == null && currCloud != null) {// second click
                    prevCloud = currCloud;
                    currCloud = genCloud();
                }
            }
            repaint();
        }
    }
}
