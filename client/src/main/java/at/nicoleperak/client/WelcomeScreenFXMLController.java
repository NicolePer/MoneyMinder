package at.nicoleperak.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeScreenFXMLController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signInButton;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    private Label successMessageLabel;

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
        //TODO Login
    }


    public void setSuccessMessageLabelText(String message) {
        successMessageLabel.setText(message);
    }
}
