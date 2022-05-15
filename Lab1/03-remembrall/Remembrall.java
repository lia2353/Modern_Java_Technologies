package remembrall;

public class Remembrall {
    public static boolean isPhoneNumberForgettable(String phoneNumber) {

        // if null or empty, phoneNumber can NOT be forgotten
        if (phoneNumber == null|| phoneNumber.isBlank()) {
            return false;
        }

        boolean[] uniqueDigits = new boolean[10];
        boolean hasAllUniqueDigits = true;

        for (int i = 0; i < phoneNumber.length(); i++) {
            char digit = phoneNumber.charAt(i);

            if (digit == '-' || digit == ' ' || digit == '(' || digit == ')') {
                continue;
            }

            // if phoneNumber contains letters, it is forgettable
            if (Character.isLetter(digit)) {
                return true;
            }

            // if phoneNumber contains repeating digits, it can NOT be forgotten
            if (uniqueDigits[digit - '0']) {
                hasAllUniqueDigits = false;
            } else {
                uniqueDigits[digit - '0'] = true;
            }
        }

        // if phoneNumber has only unique digits, it is forgettable
        return hasAllUniqueDigits;
    }

}
