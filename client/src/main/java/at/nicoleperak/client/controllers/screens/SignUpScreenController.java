package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;
import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.post;
import static at.nicoleperak.client.Validation.*;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;

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

    /**
     * Reloads the scene.
     */
    public static void reloadFinancialAccountsOverviewScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    @FXML
    void onSignUpButtonClicked() {
        signUpUser();
    }

    @FXML
    void onKeyTypedInRetypedPasswordField() {
        showAlertIfPasswordsDiffer();
    }

    @FXML
    void onGoBackButtonClicked() {
        redirectToWelcomeScreen("", alertMessageLabel);
    }

    /**
     * Asserts that user inputs are valid and shows alert message to user, if this is not the case.
     * Sends post-request to server to create a user. Then redirects user to welcome screen
     * and displays success message to user.
     */
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

    /**
     * Checks whether the user input in the password textfields differ and informs the user if that is the case.
     */
    private void showAlertIfPasswordsDiffer() {
        if (passwordsDiffer(passwordField.getText(), retypePasswordField.getText())) {
            alertMessageLabel.setText("passwords do not match");
        } else {
            alertMessageLabel.setText("");
        }
    }

    /**
     * Sends post-request to server to create a user.
     *
     * @param newUser User to be created.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void postUser(User newUser) throws ClientException {
        post("users", jsonb.toJson(newUser), false);
    }
}

