package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.IOException;

import static at.nicoleperak.client.Client.*;
import static at.nicoleperak.client.FXMLLocation.SIGN_UP_SCREEN;
import static at.nicoleperak.client.LoadingUtils.loadLoggedInUser;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsOverviewScreen;
import static at.nicoleperak.client.Validation.assertEmailIsValid;
import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.paint.Color.RED;

public class WelcomeScreenController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    private Label alertMessageLabel;

    @FXML @SuppressWarnings("unused")
    protected void onSignUpLinkClicked(ActionEvent event) {
        try {
            Scene scene = loadScene(SIGN_UP_SCREEN);
            getStage().setScene(scene);
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML @SuppressWarnings("unused")
    protected void onSignInButtonClicked(ActionEvent event) {
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

    private static void saveLoggedInUser(User loggedInUser) {
        setLoggedInUser(loggedInUser);
    }

    private void saveUserCredentials(User user) {
        setUserCredentials(user);
    }

    public void setWelcomeScreenAlertMessageLabelText(String message) {
        alertMessageLabel.setText(message);
    }
}
