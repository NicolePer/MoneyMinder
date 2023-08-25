package at.nicoleperak.client;

import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import java.io.IOException;

import static at.nicoleperak.client.FXMLLocation.WELCOME_SCREEN;

public class Client extends Application {
    private static User loggedInUser;
    private static User userCredentials;
    private static Stage stage;
    private static FinancialAccount selectedFinancialAccount;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the MoneyMinder application by initializing the primary stage, loading the welcome screen,
     * and displaying the main scene.
     *
     * @param primaryStage The primary stage of the JavaFX application.
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            Scene scene = loadScene(WELCOME_SCREEN);
            primaryStage.setScene(scene);
            primaryStage.setTitle("MoneyMinder");
            primaryStage.show();
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    /**
     * Loads a JavaFX scene from the specified FXML location.
     *
     * @param fxmlLocation The FXMLLocation enum indicating the location of the FXML file.
     * @return A new Scene instance containing the loaded control.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    public static Scene loadScene(FXMLLocation fxmlLocation) throws IOException {
        FXMLLoader loader = fxmlLocation.getLoader();
        Parent root = loader.load();
        return new Scene(root);
    }

    /**
     * Loads a JavaFX scene from the specified FXML location using the provided FXMLLoader.
     *
     * @param fxmlLocation The FXMLLocation enum indicating the location of the FXML file.
     * @param loader       The FXMLLoader instance used for loading the FXML.
     * @return A new Scene instance containing the loaded UI.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    public static Scene loadScene(FXMLLocation fxmlLocation, FXMLLoader loader) throws IOException {
        loader.setLocation(Client.class.getResource(fxmlLocation.getLocation()));
        Parent root = loader.load();
        return new Scene(root);
    }

    /**
     * Creates and returns a JavaFX dialog with the specified DialogPane.
     *
     * @param dialogPane The DialogPane to set as the content of the dialog.
     * @return A new Dialog instance with the provided content.
     */
    public static Dialog<ButtonType> getDialog(DialogPane dialogPane) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        return dialog;
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        Client.loggedInUser = loggedInUser;
    }

    public static User getUserCredentials() {
        return userCredentials;
    }

    public static void setUserCredentials(User userCredentials) {
        Client.userCredentials = userCredentials;
    }

    public static Stage getStage() {
        return stage;
    }

    public static FinancialAccount getSelectedFinancialAccount() {
        return selectedFinancialAccount;
    }

    public static void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        Client.selectedFinancialAccount = selectedFinancialAccount;
    }

}