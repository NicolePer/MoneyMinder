package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.FXMLLocation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.io.IOException;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.*;

@SuppressWarnings("CallToPrintStackTrace")
public class MoneyMinderAlertController {

    @FXML
    private Label alertTextLabel;

    /**
     * Displays error alert dialog with message to the user.
     *
     * @param message Message to be displayed in the alert window.
     */
    public static void showMoneyMinderErrorAlert(String message) {
        try {
            showAlert(MONEYMINDER_ERROR_ALERT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays success alert dialog with message to the user.
     *
     * @param message Message to be displayed in the alert window.
     */
    public static void showMoneyMinderSuccessAlert(String message) {
        try {
            showAlert(MONEYMINDER_SUCCESS_ALERT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays warning alert dialog with message to the user.
     *
     * @param message Message to be displayed in the alert window.
     */
    public static void showMoneyMinderWarningAlert(String message) {
        try {
            showAlert(MONEYMINDER_WARNING_ALERT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shows alert window dialogs of different kinds
     *
     * @param fxmlLocation Location of FXML-File for specific alert dialog.
     * @param message      Message to be displayed to the user in the alert dialog.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    private static void showAlert(FXMLLocation fxmlLocation, String message) throws IOException {
        FXMLLoader loader = fxmlLocation.getLoader();
        DialogPane dialogPane = loader.load();
        MoneyMinderAlertController controller = loader.getController();
        controller.getAlertTextLabel().setText(message);
        getDialog(dialogPane).showAndWait();
    }

    public Label getAlertTextLabel() {
        return alertTextLabel;
    }
}
