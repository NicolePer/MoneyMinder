package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.Client.getLoggedInUser;
import static at.nicoleperak.client.FXMLLocation.CHANGE_PASSWORD_FORM;
import static at.nicoleperak.client.ServiceFunctions.delete;
import static at.nicoleperak.client.Validation.assertEmailIsValid;
import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static at.nicoleperak.client.controllers.controls.NavigationBarController.logout;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.FINISH;

public class EditUserAccountDialogController implements Initializable {
    private Dialog<ButtonType> dialog;
    @FXML
    private Label alertMessageLabel;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField usernameTextField;
    private String password = null;

    /**
     * Upon initialization, engages event listeners and event filters for the dialog.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engageEventFilters();
    }

    @FXML
    void onChangePasswordButtonClicked() {
        showChangePasswordDialog();
    }

    @FXML
    void onDeleteAccountButtonClicked() {
        deleteUserAccount();
    }

    /**
     * Engages a number of event filters on the dialog.
     */
    private void engageEventFilters() {
        validateUserInputsOnFinish();
    }

    /**
     * Sets up an event filter that will be executed when the "Finish" button of the dialog is clicked.
     * Checks whether the user inputs into the dialog are valid and informs the user in case they are not.
     */
    private void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(FINISH);
        finish.addEventFilter(ActionEvent.ACTION, f -> {
            try {
                assertUserInputLengthIsValid(usernameTextField.getText(), "username", 1, 255);
                assertUserInputLengthIsValid(emailTextField.getText(), "email address", 4, 255);
                assertEmailIsValid(emailTextField.getText());
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
            }
        });
    }

    /**
     * Sends delete-request to server to remove a user account,
     * following the user's confirmation.
     * Then logs the user out of the account, and displays a success messsage.
     */
    private void deleteUserAccount() {
        if (userHasConfirmedActionWhenAskedForConfirmation(
                "Are you sure you want to permanently delete your user account?")) {
            try {
                delete("users/" + getLoggedInUser().getId());
                dialog.setResult(CANCEL);
                logout("User account successfully deleted.");
            } catch (ClientException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    /**
     * Shows the "Change password" dialog to the user.
     * Upon finish, stores the input from the user in the password variable.
     */
    private void showChangePasswordDialog() {
        try {
            FXMLLoader loader = CHANGE_PASSWORD_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            ChangePasswordDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                password = controller.getPasswordField().getText();
            }
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    public Label getAlertMessageLabel() {
        return alertMessageLabel;
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }

    public TextField getEmailTextField() {
        return emailTextField;
    }

    public TextField getUsernameTextField() {
        return usernameTextField;
    }

    public String getPassword() {
        return password;
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }
}
