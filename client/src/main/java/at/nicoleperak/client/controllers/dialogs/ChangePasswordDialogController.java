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

    /**
     * Upon initialization, engages event filters for the dialog.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engageEventFilter();
    }

    @FXML
    void onKeyTypedInRetypedPasswordField() {
        alertIfPasswordsDiffer();
    }

    /**
     * Sets up an event filter that gets called when the "Finish" button of the dialog is clicked.
     * Checks whether the user inputs into the dialog are valid and informs the user in case they are not.
     */
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

    /**
     * Engages a number of event filters for the dialog.
     */
    private void engageEventFilter() {
        validateUserInputsOnFinish();
    }

    /**
     * Checks whether the user input in the password textfields differ and informs the user if that is the case.
     */
    private void alertIfPasswordsDiffer() {
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
