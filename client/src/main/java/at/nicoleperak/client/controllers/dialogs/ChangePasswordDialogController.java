package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Validation.*;
import static javafx.scene.control.ButtonType.FINISH;

public class ChangePasswordDialogController implements Initializable {

    @FXML
    private Label alertMessageLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField retypePasswordField;

    @FXML
    private DialogPane dialogPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
    }

    private void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(FINISH);
        finish.addEventFilter(ActionEvent.ACTION, f -> {
            try {
                assertUserInputLengthIsValid(passwordField.getText(), "password", 8, 255);
                assertPasswordsMatch(passwordField.getText(), retypePasswordField.getText());
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
            }
        });
    }

    @FXML
    void onKeyTypedInRetypedPasswordField() {
        showAlertIfPasswordsDiffer();
    }

    private void showAlertIfPasswordsDiffer() {
        if (passwordsDiffer(passwordField.getText(), retypePasswordField.getText())) {
            alertMessageLabel.setText("passwords do not match");
        } else {
            alertMessageLabel.setText("");
        }
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }
}
