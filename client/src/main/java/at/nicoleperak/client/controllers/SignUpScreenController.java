package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;


import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.Validation.*;

public class SignUpScreenController {
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
    private Label alertMessageLabel;

    @FXML
    protected void onSignUpButtonClicked(ActionEvent event) {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String retypedPassword =retypePasswordField.getText();
        try {
            assertUserInputLengthIsValid(username, "username", 1, 255);
            assertUserInputLengthIsValid(email, "email address", 4, 255);
            assertEmailIsValid(email);
            assertUserInputLengthIsValid(password, "password", 8, 255);
            assertPasswordsMatch(password, retypedPassword);
            signUpUser(new User(null, username, email, password));
            redirectToWelcomeScreen("Your account has been successfully created!", alertMessageLabel);
        } catch (ClientException clientException) {
            alertMessageLabel.setText(clientException.getMessage());
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
        ServiceFunctions.post("users", jsonb.toJson(newUser), false);
    }

    private void showAlertIfPasswordsDiffer() {
        if (passwordsDiffer(passwordField.getText(), retypePasswordField.getText())) {
            alertMessageLabel.setText("passwords do not match");
        } else {
            alertMessageLabel.setText("");
        }
    }

}

