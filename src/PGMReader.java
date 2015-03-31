import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Reads in image in pgm format; converts it to int[][].
 * Inspired by http://stackoverflow.com/questions/3639198/how-to-read-pgm-images-in-java
 * However, I wrote the code myself.
 */
public class PGMReader
{
    public static int[][] read(String path)
    {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            System.out.println("File " + path + " not found.");
            try {
                if (fis != null) fis.close();
            } catch (IOException e1) {
                System.out.println("Failed to close FileInputStream.");
            }
        }
        if (fis == null) return null;

        /** process image headers **/
        Scanner scan = new Scanner(fis);
        String magicNum = scan.nextLine();
        String comment = scan.nextLine();
        int width = scan.nextInt();
        int height = scan.nextInt();
        int max = scan.nextInt();
        System.out.printf("Magic number: %s Comment: %s Width: %d Height: %d Max: %d\n", magicNum, comment, width, height, max);
        try {
            fis.close();
        } catch (IOException e) {
            System.out.println("Failed to close FileInputStream.");
        }
        fis = null;

        /** process pixels **/
        try {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            System.out.println("File " + path + " not found.");
            try {
                if (fis != null ) fis.close();
            } catch (IOException e1) {
                System.out.println("Failed to close FileInputStream.");
            }
        }
        if (fis == null) return null;
        DataInputStream dis = new DataInputStream(fis);
        /** skip headers **/
        try {
            int numOfNewline = 4;
            while (numOfNewline > 0) {
                char c;
                do {
                    c = (char)(dis.readUnsignedByte());
                } while (c != '\n');
                numOfNewline--;
            }
        } catch (IOException e) {
            System.out.println("Failed reading file " + path + ".");
            try {
                fis.close();
            } catch (IOException e1) {
                System.out.println("Failed to close FileInputStream.");
            }
        }
        /** read pixels **/
        try {
            int[][] img = new int[height][width];
            for (int r = 0; r < height; r++)
                for (int c = 0; c < width; c++)
                    img[r][c] = dis.readUnsignedByte();
            fis.close();
            return img;
        } catch (IOException e) {
            System.out.println("Failed reading file " + path + ".");
            try {
                fis.close();
            } catch (IOException e1) {
                System.out.println("Failed to close FileInputStream.");
            }
        }
        return null;
    }

    // quick test
    public static void main(String args[])
    {
        int[][] img = PGMReader.read("/Users/lee/Dropbox/VIC/assn3/ass3-labeled.pgm");
    }
}
