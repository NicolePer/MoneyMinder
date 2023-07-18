package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.Validation.*;
import static at.nicoleperak.client.FXMLLocation.WELCOME_SCREEN;

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
            redirectToWelcomeScreen("Your account has been successfully created!");
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
        redirectToWelcomeScreen("");
    }

    private void redirectToWelcomeScreen(String successMessage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Scene scene = loadScene(WELCOME_SCREEN, loader);
            WelcomeScreenController welcomeScreen = loader.getController();
            welcomeScreen.setAlertMessageLabelText(successMessage);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            this.alertMessageLabel.setText(e.getMessage());
        }
    }

    private void showAlertIfPasswordsDiffer() {
        if (passwordsDiffer(passwordField.getText(), retypePasswordField.getText())) {
            alertMessageLabel.setText("passwords do not match");
        } else {
            alertMessageLabel.setText("");
        }
    }

    private static void signUpUser(User newUser) throws ClientException {
        ServiceFunctions.post("users", jsonb.toJson(newUser), false);
    }
}

