package at.nicoleperak.client;

import at.nicoleperak.shared.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpScreenFXMLController {
    private static final Jsonb jsonb = JsonbBuilder.create();

    @FXML
    private TextField emailField;

    @FXML
    private AnchorPane goBackButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField retypePasswordField;

    @FXML
    private Button signUpButton;

    @FXML
    private TextField usernameField;

    @FXML
    private Label warningLabel;

    @FXML
    protected void registerUser(ActionEvent event) {
        try {
            assertUserInputLengthIsValid(usernameField.getText(), "username", 1, 255);
            assertUserInputLengthIsValid(emailField.getText(), "email address", 4, 255);
            assertEmailIsValid();
            assertUserInputLengthIsValid(passwordField.getText(), "password", 8, 255);
            assertPasswordsMatch();
            User newUser = new User(null, usernameField.getText(), emailField.getText(), passwordField.getText());
            ServiceFunctions.post("users", jsonb.toJson(newUser));
            redirectToWelcomeScreen("Your account has been successfully created!");
        } catch (ClientException clientException) {
            warningLabel.setText(clientException.getMessage());
        }
    }

    @FXML
    protected void showWarningIfPasswordsDiffer(KeyEvent event) {
        if (passwordsDiffer()) {
            warningLabel.setText("passwords do not match");
        } else {
            warningLabel.setText("");
        }
    }

    @FXML
    protected void onGoBackButtonClicked(MouseEvent event) {
        redirectToWelcomeScreen("");
    }

    private boolean passwordsDiffer() {
        return !passwordField.getText().equals(retypePasswordField.getText());
    }

    private void assertPasswordsMatch() throws ClientException {
        if (passwordsDiffer()) {
            throw new ClientException("Retyped password must match password.");
        }
    }

    private void assertEmailIsValid() throws ClientException {
        Pattern validEmailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        String email = emailField.getText();
        Matcher matcher = validEmailPattern.matcher(email);
        if (!matcher.find()) {
            throw new ClientException("Please enter valid email address");
        }
    }

    private void assertUserInputLengthIsValid(String input, String fieldName, int charMin, int charMax) throws ClientException {
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

    private void redirectToWelcomeScreen(String successMessage) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/welcome-screen.fxml"));
        try {
            Parent root = loader.load();
            WelcomeScreenFXMLController controller = loader.getController();
            controller.setAlertMessageLabelText(successMessage);
            Stage stage = (Stage) goBackButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}

