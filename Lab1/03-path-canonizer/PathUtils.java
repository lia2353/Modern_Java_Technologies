import java.util.Arrays;

public class PathUtils {

    private static final String PATH_SEPARATOR = "/";
    private static final String CURRENT_DIRECTORY_SYMBOL = ".";
    private static final String PARENT_DIRECTORY_SYMBOL = "..";

    /* Algorithm:
       1. Split the string to array using '/' as delimiter
       2. Traverse the array from right to left, and
          - if the element is empty or is '.', then skip it
          - if element is "..", then skip the next element, else add the element to the output path
    */

    public static String getCanonicalPath(String path) {
        String[] directories = path.split(PATH_SEPARATOR);
        StringBuilder canonicalPath = new StringBuilder();

        for (int i = directories.length - 1; i >= 0; --i) {
            if (directories[i].isBlank() || directories[i].equals(CURRENT_DIRECTORY_SYMBOL)
                    || directories[i].equals(PARENT_DIRECTORY_SYMBOL)) {
                continue;
            }
            if (i + 1 < directories.length && directories[i + 1].equals(PARENT_DIRECTORY_SYMBOL)) {
                continue;
            }
            canonicalPath.insert(0, PATH_SEPARATOR + directories[i]);
        }

        return canonicalPath.length() == 0 ? PATH_SEPARATOR : canonicalPath.toString();
    }
    
}
