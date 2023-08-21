package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.post;
import static at.nicoleperak.client.Validation.*;

public class SignUpScreenController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField retypePasswordField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label alertMessageLabel;

    @FXML
    void onSignUpButtonClicked() {
        signUpUser();
    }

    private void signUpUser() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String retypedPassword = retypePasswordField.getText();
        try {
            assertUserInputLengthIsValid(username, "username", 1, 255);
            assertUserInputLengthIsValid(email, "email address", 4, 255);
            assertEmailIsValid(email);
            assertUserInputLengthIsValid(password, "password", 8, 255);
            assertPasswordsMatch(password, retypedPassword);
            postUser(new User(null, username, email, password));
            redirectToWelcomeScreen("Your account has been successfully created!", alertMessageLabel);
        } catch (ClientException e) {
            alertMessageLabel.setText(e.getMessage());
        }
    }

    @FXML
    void onKeyTypedInRetypedPasswordField() {
        showAlertIfPasswordsDiffer();
    }

    @FXML
    void onGoBackButtonClicked() {
        redirectToWelcomeScreen("", alertMessageLabel);
    }

    private void showAlertIfPasswordsDiffer() {
        if (passwordsDiffer(passwordField.getText(), retypePasswordField.getText())) {
            alertMessageLabel.setText("passwords do not match");
        } else {
            alertMessageLabel.setText("");
        }
    }

    private static void postUser(User newUser) throws ClientException {
        post("users", jsonb.toJson(newUser), false);
    }
}

