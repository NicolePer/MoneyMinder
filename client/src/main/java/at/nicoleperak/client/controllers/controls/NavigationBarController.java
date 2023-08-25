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


    private static void logout() {
        redirectToWelcomeScreen();
        setLoggedInUser(null);
        setUserCredentials(null);
    }

    public static void logout(String successMessage) {
        redirectToWelcomeScreen(successMessage);
        setLoggedInUser(null);
        setUserCredentials(null);
    }

    private static void insertUserData(EditUserAccountDialogController controller) {
        controller.getEmailTextField().setText(getLoggedInUser().getEmail());
        controller.getUsernameTextField().setText(getLoggedInUser().getUsername());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
                putEditedUser(controller);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void putEditedUser(EditUserAccountDialogController controller) throws ClientException {
        String password = controller.getPassword();
        String username = controller.getUsernameTextField().getText();
        String email = controller.getEmailTextField().getText();
        User editedUser = new User(null, username, email, password);
        put("users/" + getLoggedInUser().getId(), jsonb.toJson(editedUser));
        logout("User account data successfully updated - please login again");
    }

    private void openUserGuide() {
        try {
            File file = new File((requireNonNull(getClass().getResource("/help/MoneyMinderUserGuide.pdf")).toURI()));
            openFile(file);
        } catch (Exception e) {
            showMoneyMinderErrorAlert("User Guide could not be loaded");
        }
    }

    private void openFile(File file) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    public HBox getNavigationBarBox() {
        return navigationBarBox;
    }

    public ImageView getGoBackIcon() {
        return goBackIcon;
    }
}
