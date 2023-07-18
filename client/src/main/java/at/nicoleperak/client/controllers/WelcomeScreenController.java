package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;
import static at.nicoleperak.client.FXMLLocation.SIGN_UP_SCREEN;
import static at.nicoleperak.client.Validation.assertEmailIsValid;
import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;

public class WelcomeScreenController {
    private static final Jsonb jsonb = JsonbBuilder.create();

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

    @FXML
    protected void onSignUpLinkClicked(ActionEvent event) {
        try {
            Scene scene = loadScene(SIGN_UP_SCREEN);
            Client.getStage().setScene(scene);
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
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
            alertMessageLabel.setTextFill(Color.RED);
            alertMessageLabel.setText(e.getMessage());
        }
    }

    private void redirectToFinancialAccountsOverviewScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN);
            Stage stage = Client.getStage();
            stage.setScene(scene);
            centerStage(stage);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private static void saveLoggedInUser(User loggedInUser) {
        Client.setLoggedInUser(loggedInUser);
    }

    private static User loadLoggedInUser() throws ClientException {
        String jsonResponse = ServiceFunctions.get("users");
        return jsonb.fromJson(jsonResponse, User.class);
    }

    private void saveUserCredentials(User user) {
        Client.setUserCredentials(user);
    }

    private static void centerStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public void setAlertMessageLabelText(String message) {
        alertMessageLabel.setText(message);
    }
}
