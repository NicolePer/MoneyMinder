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

    public static void assertEmailIsValid(String email) throws ClientException {
        Pattern validEmailPattern = compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", CASE_INSENSITIVE);
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

    public static void assertDateIsNotNull(LocalDate date) throws ClientException {
        if (date == null) {
            throw new ClientException("Please select date");
        }
    }

    public static void assertDateIsInPast(LocalDate date) throws ClientException {
        if (date.isAfter(now())) {
            throw new ClientException("Date cannot be in the future");
        }
    }

    public static void assertDateIsInTheFuture(LocalDate date) throws ClientException {
        if (date.isBefore(now())) {
            throw new ClientException("End date must be in the future");
        }
    }

    public static void assertRadioButtonIsSelected(ToggleGroup toggleGroup) throws ClientException {
        if (toggleGroup.selectedToggleProperty().getValue() == null) {
            throw new ClientException("Please select income or expense");
        }
    }

    public static void assertOwnerIsSelected(ComboBox<User> comboBox) throws ClientException {
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
            throw new ClientException("Please select owner");
        }
    }

    public static void assertCategoryIsSelected(ComboBox<Category> comboBox) throws ClientException {
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
            throw new ClientException("Please select category");
        }
    }

    public static void assertIntervalIsSelected(ComboBox<Interval> comboBox) throws ClientException {
        if (comboBox.getSelectionModel().getSelectedItem() == null) {
            throw new ClientException("Please select interval");
        }
    }

    public static void assertAmountIsBigDecimal(String amount) throws ClientException {
        try {
            new BigDecimal(amount);
        } catch (Exception e) {
            throw new ClientException("Please enter valid number for amount");
        }
    }

    public static String convertToValidFileName(String string) {
        return string.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }
}
