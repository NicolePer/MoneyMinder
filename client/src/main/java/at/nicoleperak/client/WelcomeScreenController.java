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
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

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
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sign-up-screen.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            root.requestFocus();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void redirectToFinancialAccountsOverviewScreen() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/financial-accounts-overview-screen.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = (Stage) signInButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void onSignInButtonClicked(ActionEvent event) {
        try {
            assertUserInputLengthIsValid(emailField.getText(), "email address", 4, 255);
            assertEmailIsValid(emailField.getText());
            assertUserInputLengthIsValid(passwordField.getText(), "password", 8, 255);
            Client.setUserCredentials(new User(null, null, emailField.getText(), passwordField.getText()));
            String jsonResponse = ServiceFunctions.get("users");
            User loggedInUser = jsonb.fromJson(jsonResponse, User.class);
            Client.setLoggedInUser(loggedInUser);
            redirectToFinancialAccountsOverviewScreen();
        } catch (ClientException e) {
            alertMessageLabel.setTextFill(Color.RED);
            alertMessageLabel.setText(e.getMessage());
        }
    }

    public void setAlertMessageLabelText(String message) {
        alertMessageLabel.setText(message);
    }
}
