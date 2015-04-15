import java.util.List;

/**
 * Utility class for Array related operation.
 */
public class ArrUtil
{
    /**
     * Find maximum in an array.
     * @param arr
     * @return the index of maximum value
     */
    public static int findMax(int[] arr)
    {
        int max = -Integer.MAX_VALUE, maxIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static int findMin(int[] arr)
    {
        int min = Integer.MAX_VALUE, minIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * Find maximum in an array.
     * @param arr
     * @return the index of maximum value
     */
    public static int findMax(float[] arr)
    {
        float max = -Float.MAX_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static int findMin(float[] arr)
    {
        float min = Integer.MAX_VALUE;
        int minIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static int countTrue(boolean[] arr)
    {
        int cnt = 0;
        for (boolean elem : arr) {
            if (elem)
                cnt++;
        }
        return cnt;
    }

    public static int[] IntegerList2IntArray(List<Integer> list)
    {
        int[] arr = new int[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }
}
