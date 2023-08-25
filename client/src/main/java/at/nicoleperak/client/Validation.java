package at.nicoleperak.client;

import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.User;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static at.nicoleperak.shared.RecurringTransactionOrder.Interval;
import static java.time.LocalDate.now;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;

public class Validation {

    /**
     * Checks if the length of a user input is valid based on minimum and maximum character limits.
     *
     * @param input     The user input string.
     * @param fieldName The name of the input field.
     * @param charMin   The minimum number of characters allowed.
     * @param charMax   The maximum number of characters allowed.
     * @throws ClientException If the input length is too short, too long, or empty.
     */
    public static void assertUserInputLengthIsValid(String input, String fieldName, int charMin, int charMax) throws ClientException {
        int inputLength = input != null ? input.length() : 0;
        if (inputLength < charMin) {
            if (inputLength == 0) {
                throw new ClientException("Please enter " + fieldName);
            }
            throw new ClientException(fieldName + " must at least contain " + charMin + " characters");
        }
        if (inputLength > charMax) {
            throw new ClientException(fieldName + "is too long (up to " + charMax + " characters allowed)");
        }
    }

    /**
     * Checks if an email address is valid using a regex pattern match.
     *
     * @param email The email address to validate.
     * @throws ClientException If the email is not in a valid format.
     */
    public static void assertEmailIsValid(String email) throws ClientException {
        Pattern validEmailPattern = compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", CASE_INSENSITIVE);
        Matcher matcher = validEmailPattern.matcher(email);
        if (!matcher.find()) {
            throw new ClientException("Please enter valid email address");
        }
    }

    /**
     * Checks if two passwords are different.
     *
     * @param password        The first password.
     * @param retypedPassword The second password.
     * @return True if passwords are different, otherwise false.
     */
    public static boolean passwordsDiffer(String password, String retypedPassword) {
        return !password.equals(retypedPassword);
    }

    /**
     * Checks if two passwords match.
     *
     * @param password        The first password.
     * @param retypedPassword The second password.
     * @throws ClientException If the passwords don't match.
     */
    public static void assertPasswordsMatch(String password, String retypedPassword) throws ClientException {
        if (passwordsDiffer(password, retypedPassword)) {
            throw new ClientException("Retyped password must match password.");
        }
    }

    /**
     * Checks if a date is not null.
     *
     * @param date The date to validate.
     * @throws ClientException If the date is null.
     */
    public static void assertDateIsNotNull(LocalDate date) throws ClientException {
        if (date == null) {
            throw new ClientException("Please select date");
        }
    }

    /**
     * Checks if a date is in the past.
     *
     * @param date The date to validate.
     * @throws ClientException If the date is in the future.
     */
    public static void assertDateIsInPast(LocalDate date) throws ClientException {
        if (date.isAfter(now())) {
            throw new ClientException("Date cannot be in the future");
        }
    }

    /**
     * Checks if a date is in the future.
     *
     * @param date The date to validate.
     * @throws ClientException If the date is not in the future.
     */
    public static void assertDateIsInTheFuture(LocalDate date) throws ClientException {
        if (date.isBefore(now())) {
            throw new ClientException("End date must be in the future");
        }
    }

    /**
     * Checks if a radio button is selected in a toggle group.
     *
     * @param toggleGroup The toggle group containing radio buttons.
     * @throws ClientException If no radio button is selected.
     */
    public static void assertRadioButtonIsSelected(ToggleGroup toggleGroup) throws ClientException {
        if (toggleGroup.selectedToggleProperty().getValue() == null) {
            throw new ClientException("Please select income or expense");
        }
    }

    /**
     * Checks if an item is selected in a ComboBox for user selection.
     *
     * @param comboBox The ComboBox to validate.
     * @throws ClientException If no item is selected in the ComboBox.
     */
    public static void assertOwnerIsSelected(ComboBox<User> comboBox) throws ClientException {
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
            throw new ClientException("Please select owner");
        }
    }

    /**
     * Checks if an item is selected in a ComboBox for category selection.
     *
     * @param comboBox The ComboBox to validate.
     * @throws ClientException If no item is selected in the ComboBox.
     */
    public static void assertCategoryIsSelected(ComboBox<Category> comboBox) throws ClientException {
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
            throw new ClientException("Please select category");
        }
    }

    /**
     * Checks if an item is selected in a ComboBox for interval selection.
     *
     * @param comboBox The ComboBox to validate.
     * @throws ClientException If no item is selected in the ComboBox.
     */
    public static void assertIntervalIsSelected(ComboBox<Interval> comboBox) throws ClientException {
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
            throw new ClientException("Please select interval");
        }
    }

    /**
     * Checks if a string can be converted into a valid BigDecimal number.
     *
     * @param amount The string to validate.
     * @throws ClientException If the string cannot be converted to a valid number.
     */
    public static void assertAmountIsBigDecimal(String amount) throws ClientException {
        try {
            new BigDecimal(amount);
        } catch (Exception e) {
            throw new ClientException("Please enter valid number for amount");
        }
    }

    /**
     * Converts a given string into a valid format for use as a file name.
     *
     * @param string The string to be converted.
     * @return The converted string suitable for a file name.
     */
    public static String convertToValidFileName(String string) {
        return string.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }
}
