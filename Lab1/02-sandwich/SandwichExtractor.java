import java.util.Arrays;

public class SandwichExtractor_mySolution {
    
    public static String[] extractIngredients(String sandwich) {
        String[] splitSandwich = sandwich.split("bread");

        if (splitSandwich.length != 3) {
            return new String[0];
        }

        String[] ingredients = splitSandwich[1].split("-");

        int newSize = 0;
        for (int i = 0; i < ingredients.length; ++i) {
            if (!ingredients[i].equals("olives")) {
                ingredients[newSize++] = ingredients[i];
            }
        }

        String[] extractedIngredients = Arrays.copyOfRange(ingredients, 0, newSize);
        Arrays.sort(extractedIngredients);

        return extractedIngredients;
    }
    
}
