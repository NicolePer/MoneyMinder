package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.EditUserAccountDialogController;
import at.nicoleperak.shared.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Client.*;
import static at.nicoleperak.client.FXMLLocation.EDIT_USER_ACCOUNT_FORM;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsOverviewScreen;
import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.put;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static java.util.Objects.requireNonNull;
import static javafx.scene.control.ButtonType.FINISH;

public class NavigationBarController implements Initializable {
    @FXML
    private HBox navigationBarBox;
    @FXML
    private Label userLabel;
    @FXML
    private ImageView goBackIcon;

    /**
     * Logs the user out of their user account.
     * Sets the stored user data to null and
     * redirects the user to the welcome screen where a message will be displayed.
     *
     * @param message Message to be displayed on welcome screen.
     */
    public static void logout(String message) {
        redirectToWelcomeScreen(message);
        setLoggedInUser(null);
        setUserCredentials(null);
    }

    /**
     * Upon initialization, sets labels in the control.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
    }

    /**
     * Sets userLabel to the username of the logged-in user.
     */
    private void setLabels() {
        userLabel.setText(Client.getLoggedInUser().getUsername());
    }

    @FXML
    void onGoBackIconClicked() {
        redirectToFinancialAccountsOverviewScreen();
    }

    @FXML
    void onHelpMenuItemClicked() {
        openUserGuide();
    }

    @FXML
    void onLogoutMenuItemClicked() {
        logout();
    }

    @FXML
    void onUserAccountSettingsMenuItemClicked() {
        showEditUserAccountDialog();
    }

    /**
     * Shows the "Edit User Account" dialog to the user.
     * Upon finish, updates the user data.
     */
    private void showEditUserAccountDialog() {
        try {
            FXMLLoader loader = EDIT_USER_ACCOUNT_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            EditUserAccountDialogController controller = loader.getController();
            insertUserData(controller);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            controller.setDialog(dialog);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                updateUser(controller);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Inserts the logged-in user's data into the dialog.
     *
     * @param controller The FXML controller of the dialog the data is to be inserted into.
     */
    private void insertUserData(EditUserAccountDialogController controller) {
        controller.getEmailTextField().setText(getLoggedInUser().getEmail());
        controller.getUsernameTextField().setText(getLoggedInUser().getUsername());
    }

    /**
     * Takes user inputs from dialog and creates new User object.
     * Sends put-request to server. Then logs user out of account and redirects user to welcome screen.
     * Directs user to login again with their updated user data.
     *
     * @param controller The FXML controller of the dialog that gathered the input from the user.
     * @throws ClientException if there is an issue regarding the server interaction.
     */
    private void updateUser(EditUserAccountDialogController controller) throws ClientException {
        String password = controller.getPassword();
        String username = controller.getUsernameTextField().getText();
        String email = controller.getEmailTextField().getText();
        User editedUser = new User(null, username, email, password);
        put("users/" + getLoggedInUser().getId(), jsonb.toJson(editedUser));
        logout("User account data successfully updated - please login again");
    }

    /**
     * Opens the MoneyMinder User Guide.
     */
    private void openUserGuide() {
        try {
            File file = new File((requireNonNull(getClass().getResource("/help/MoneyMinderUserGuide.pdf")).toURI()));
            openFile(file);
        } catch (Exception e) {
            showMoneyMinderErrorAlert("User Guide could not be opened");
        }
    }

    /**
     * Opens the given file in current system's default application.
     *
     * @param file file to be opened
     */
    private void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    /**
     * Logs the user out of their user account.
     * Sets the stored user data to null and
     * redirects the user to the welcome screen.
     */
    private void logout() {
        redirectToWelcomeScreen();
        setLoggedInUser(null);
        setUserCredentials(null);
    }

    public HBox getNavigationBarBox() {
        return navigationBarBox;
    }

    public ImageView getGoBackIcon() {
        return goBackIcon;
    }
}
