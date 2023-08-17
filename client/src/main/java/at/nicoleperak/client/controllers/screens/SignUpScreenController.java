package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.post;
import static at.nicoleperak.client.Validation.*;

public class SignUpScreenController {

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
    private Label alertMessageLabel;

    @FXML
    protected void onSignUpButtonClicked(ActionEvent event) {
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
            signUpUser(new User(null, username, email, password));
            redirectToWelcomeScreen("Your account has been successfully created!", alertMessageLabel);
        } catch (ClientException e) {
            alertMessageLabel.setText(e.getMessage());
        }
    }

    @FXML
    protected void onKeyPressedInRetypedPasswordField(KeyEvent event) {
        showAlertIfPasswordsDiffer();
    }

    @FXML
    protected void onGoBackButtonClicked(MouseEvent event) {
        redirectToWelcomeScreen("", alertMessageLabel);
    }

    private static void signUpUser(User newUser) throws ClientException {
        post("users", jsonb.toJson(newUser), false);
    }

    private void showAlertIfPasswordsDiffer() {
        if (passwordsDiffer(passwordField.getText(), retypePasswordField.getText())) {
            alertMessageLabel.setText("passwords do not match");
        } else {
            alertMessageLabel.setText("");
        }
    }

}

