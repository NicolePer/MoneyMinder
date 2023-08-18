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
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.ButtonType.*;

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

    private void deleteUserAccount() {
        new Alert(CONFIRMATION, "Are you sure you want to delete your user account?")
                .showAndWait()
                .ifPresent(response -> {
                    if (response == OK) {
                        try {
                            delete("users/" + getLoggedInUser().getId());
                            dialog.setResult(CANCEL);
                            logout("User account successfully deleted.");
                        } catch (ClientException e) {
                            new Alert(ERROR, e.getMessage()).showAndWait();
                        }
                    }
                });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
    }

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

    @FXML
    void onChangePasswordButtonClicked() {
        showChangePasswordField();
    }

    @FXML
    void onDeleteAccountButtonClicked() {
        deleteUserAccount();
    }

    private void showChangePasswordField() {
        try {
            FXMLLoader loader = CHANGE_PASSWORD_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            ChangePasswordDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                password = controller.getPasswordField().getText();
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
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
