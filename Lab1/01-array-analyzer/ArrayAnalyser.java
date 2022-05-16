public class ArrayAnalyser {
    
    public static boolean isMountainArray(int[] array) {
        if (array.length < 3) {
            return false;
        }

        // Climb Up
        int position = 0;
        int length = array.length;
        while (((position + 1) < length) && (array[position] < array[position + 1])) {
            ++position;
        }

        // Climb Down
        while (((position + 1) < length) && (array[position] > array[position + 1])) {
            ++position;
        }

        return position == length - 1;
    }
    
}
