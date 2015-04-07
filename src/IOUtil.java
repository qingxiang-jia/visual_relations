import java.io.*;

/**
 * Handles all IO related operation, such as read in a file, de/serialization.
 */
public class IOUtil
{
    /**
     * Serializes an object obj into file at path.
     * @param path A String object that represents the path of the
     * @param obj The object to be written to file
     */
    public static void serialize(String path, Object obj)
    {
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;
        try {
            fileOut = new FileOutputStream(path);
            objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Failed to serialize");
        } finally { // close streams
            try {
                if (objOut != null)
                    objOut.close();
                if (fileOut != null)
                    fileOut.close();
            } catch (IOException e) {
                System.out.println("Failed to close file &/ object stream");
            }
        }
    }

    /**
     * Deserializes a file at path into an object.
     * @param path A String object that represents the path to the file
     * @return An object that was deserialized from the file
     */
    public static Object deserialize(String path)
    {
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        Object obj = null;
        try {
            fileIn = new FileInputStream(path);
            objIn = new ObjectInputStream(fileIn);
            obj = objIn.readObject();
        } catch (IOException e) {
            System.out.println("Failed to serialize");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // which never happens
        } finally {
            try { // close streams
                if (objIn != null)
                    objIn.close();
                if (fileIn != null)
                    fileIn.close();
            } catch (IOException e) {
                System.out.println("Failed to close file &/ object stream");
            }
        }
        return obj;
    }
}
