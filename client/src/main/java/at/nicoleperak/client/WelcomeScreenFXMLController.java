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
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeScreenFXMLController {
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
    protected void redirectToSignUpScreen(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sign-up-screen.fxml"));
        try {
            SignUpScreenFXMLController controller = loader.getController();
            Parent root = loader.load();
            Stage stage = (Stage) signUpLink.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            root.requestFocus();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    protected void loginUser(ActionEvent event) {
        try {
            String jsonResponse = ServiceFunctions.get("users", emailField.getText(), passwordField.getText());
            User user = jsonb.fromJson(jsonResponse, User.class);
            Client client = new Client();
            client.setLoggedInUser(user);
            alertMessageLabel.setText("Login successful");
            //TODO User Input Validation
        } catch (ClientException e) {
            alertMessageLabel.setText(e.getMessage());
        }
    }


    public void setAlertMessageLabelText(String message) {
        alertMessageLabel.setText(message);
    }
}
