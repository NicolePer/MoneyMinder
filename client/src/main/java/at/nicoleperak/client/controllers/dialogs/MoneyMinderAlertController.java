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

    public static void showMoneyMinderErrorAlert(String message) {
        try {
            showAlert(MONEYMINDER_ERROR_ALERT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMoneyMinderSuccessAlert(String message) {
        try {
            showAlert(MONEYMINDER_SUCCESS_ALERT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMoneyMinderWarningAlert(String message) {
        try {
            showAlert(MONEYMINDER_WARNING_ALERT, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showAlert(FXMLLocation alert, String message) throws IOException {
        FXMLLoader loader = alert.getLoader();
        DialogPane dialogPane = loader.load();
        MoneyMinderAlertController controller = loader.getController();
        controller.getAlertTextLabel().setText(message);
        getDialog(dialogPane).showAndWait();
    }

    public Label getAlertTextLabel() {
        return alertTextLabel;
    }
}
