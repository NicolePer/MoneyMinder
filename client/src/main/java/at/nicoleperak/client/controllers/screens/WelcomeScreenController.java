package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

import static at.nicoleperak.client.Client.*;
import static at.nicoleperak.client.FXMLLocation.SIGN_UP_SCREEN;
import static at.nicoleperak.client.PopulationUtils.loadLoggedInUser;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsOverviewScreen;
import static at.nicoleperak.client.Validation.assertEmailIsValid;
import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static javafx.scene.paint.Color.RED;

public class WelcomeScreenController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label alertMessageLabel;

    /**
     * Redirects the user to the sign-up screen.
     */
    private static void redirectToSignUpScreen() {
        try {
            Scene scene = loadScene(SIGN_UP_SCREEN);
            getStage().setScene(scene);
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    @FXML
    void onSignUpLinkClicked() {
        redirectToSignUpScreen();
    }

    @FXML
    void onSignInButtonClicked() {
        signInUser();
    }

    /**
     * Asserts that user inputs are valid and shows alert message to user, if this is not the case.
     * Stores user credentials in Client.
     * Sends get-request to server to authenticate user and gather user account information.
     * Redirects user to financial accounts overview screen.
     */
    private void signInUser() {
        String email = emailField.getText();
        String password = passwordField.getText();
        try {
            assertUserInputLengthIsValid(email, "email address", 4, 255);
            assertEmailIsValid(email);
            assertUserInputLengthIsValid(password, "password", 8, 255);
            saveUserCredentials(new User(null, null, email, password));
            User loggedInUser = loadLoggedInUser();
            saveLoggedInUser(loggedInUser);
            redirectToFinancialAccountsOverviewScreen();
        } catch (ClientException e) {
            alertMessageLabel.setTextFill(RED);
            alertMessageLabel.setText(e.getMessage());
        }
    }

    /**
     * Stores loggedInUser in Client.
     *
     * @param loggedInUser User object of logged-in user.
     */
    private void saveLoggedInUser(User loggedInUser) {
        setLoggedInUser(loggedInUser);
    }

    /**
     * Stores loggedInUser in Client.
     *
     * @param user User object with login data entered in welcome screen.
     */
    private void saveUserCredentials(User user) {
        setUserCredentials(user);
    }

    /**
     * Sets alertMessageLabel to text of message.
     *
     * @param message Message to be displayed in alertMessageLabel.
     */
    public void setWelcomeScreenAlertMessageLabelText(String message) {
        alertMessageLabel.setText(message);
    }
}
