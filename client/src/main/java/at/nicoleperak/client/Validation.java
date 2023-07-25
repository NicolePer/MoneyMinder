package at.nicoleperak.client;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleGroup;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    public static void assertDateIsNotNull(LocalDate date) throws ClientException {
        if(date == null){
            throw new ClientException("Please select date");
        }
    }

    public static void assertDateIsInPast(LocalDate date) throws ClientException {
        if(date.isAfter(LocalDate.now())){
            throw new ClientException("Date cannot be in the future");
        }
    }

    public static void assertRadiobuttonIsSelected(ToggleGroup toggleGroup) throws ClientException {
        if(toggleGroup.selectedToggleProperty().getValue() == null) {
            throw new ClientException("Please select income or expense");
        };
    }

    public static void assertCategoryIsSelected(ComboBox categories) throws ClientException {
        if(categories.getSelectionModel().getSelectedItem() == null){
            throw new ClientException("Please select category");
        };
    }


    public static void assertAmountIsBigDecimal(String amount) throws ClientException {
        try{
            new BigDecimal(amount);
        }catch (Exception e){
            throw new ClientException("Please enter valid number for amount");
        }
    }
}
