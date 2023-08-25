package at.nicoleperak.client.controllers.dialogs;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.MONEYMINDER_CONFIRMATION_DIALOG;

@SuppressWarnings("CallToPrintStackTrace")
public class MoneyMinderConfirmationDialogController {

    @FXML
    private Label requestTextLabel;

    /**
     * Asserts user has confirmed after a message has been displayed to the user.
     *
     * @param confirmationMessage Message to be displayed to the user and confirmed by the user.
     * @return True if user has clicked on the "Yes" Button of the alert dialog. False in any other case.
     */
    public static boolean userHasConfirmedActionWhenAskedForConfirmation(String confirmationMessage) {
        try {
            FXMLLoader loader = MONEYMINDER_CONFIRMATION_DIALOG.getLoader();
            DialogPane dialogPane = loader.load();
            MoneyMinderConfirmationDialogController controller = loader.getController();
            controller.getRequestTextLabel().setText(confirmationMessage);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Label getRequestTextLabel() {
        return requestTextLabel;
    }
}
