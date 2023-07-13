package at.nicoleperak.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {
    public static void assertUserInputLengthIsValid(String input, String fieldName, int charMin, int charMax) throws ClientException {
        if (input.length() < charMin) {
            if (input.length() == 0) {
                throw new ClientException("Please enter " + fieldName);
            }
            throw new ClientException(fieldName + " must at least contain " + charMin + " characters");
        }
        if (input.length() > charMax) {
            throw new ClientException(fieldName + "is too long (up to " + charMax + " characters allowed)");
        }
    }

    public static void assertEmailIsValid(String email) throws ClientException {
        Pattern validEmailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = validEmailPattern.matcher(email);
        if (!matcher.find()) {
            throw new ClientException("Please enter valid email address");
        }
    }

    public static boolean passwordsDiffer(String password, String retypedPassword) {
        return !password.equals(retypedPassword);
    }

    public static void assertPasswordsMatch(String password, String retypedPassword) throws ClientException {
        if (passwordsDiffer(password, retypedPassword)) {
            throw new ClientException("Retyped password must match password.");
        }
    }

}
