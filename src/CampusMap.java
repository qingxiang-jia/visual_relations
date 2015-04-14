import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The interactive GUI that displays cloud when user clicks on a pixel.
 */
public class CampusMap
{
    /**
     * Store pruned spatial relationship for each pixel.
     */
    int[][][] reducedMapping; // [r][c][N S W E Near] -1 represents none, else the value is building id
    PixelSet[][][][][] reducedMappingInverse; // [N][S][W][E][Near]

    BufferedImage image;

    public CampusMap(int[][][] reducedMapping, PixelSet[][][][][] reducedMappingInverse, String path)
    {
        this.reducedMapping = reducedMapping;
        this.reducedMappingInverse = reducedMappingInverse;
        image = ImageReader.read(path);
        JFrame frame = new JFrame();
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DrawingArea drawingArea = new DrawingArea(path, reducedMapping, reducedMappingInverse);
        frame.getContentPane().add(drawingArea, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new Dimension(275, 520));
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        /** deserialize files generated by PixelMappingReduction **/
        int[][][] reducedMapping = (int[][][]) IOUtil.deserialize("reducedMapping.ser");
        PixelSet[][][][][] reducedMappingInverse = (PixelSet[][][][][]) IOUtil.deserialize("reducedMappingInverse.ser");

        CampusMap campusMap = new CampusMap(reducedMapping, reducedMappingInverse, "ass3-campus.png");
    }
}