package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.CreateFinancialAccountDialogController;
import at.nicoleperak.client.factories.FinancialAccountFactory;
import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.CREATE_FINANCIAL_ACCOUNT_FORM;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.post;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.screens.SignUpScreenController.reloadFinancialAccountsOverviewScreen;
import static javafx.scene.control.ButtonType.FINISH;

public class CreateFinancialAccountTileController {

    @FXML
    void onCreateFinancialAccountTileClicked() {
        showCreateFinancialAccountDialog();
    }

    /**
     * Shows the "Create Financial Account" dialog to the user.
     * Upon finish, creates new financial account.
     */
    private void showCreateFinancialAccountDialog() {
        try {
            FXMLLoader loader = CREATE_FINANCIAL_ACCOUNT_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            CreateFinancialAccountDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                createFinancialAccount(controller);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Takes user inputs from dialog and creates new financial account.
     * Sends post-request to server. Then reloads the screen.
     *
     * @param controller The FXML controller of the dialog that gathered the input from the user.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void createFinancialAccount(CreateFinancialAccountDialogController controller) throws ClientException {
        FinancialAccount financialAccount = FinancialAccountFactory.buildFinancialAccount(controller);
        post("financial-accounts", jsonb.toJson(financialAccount), true);
        reloadFinancialAccountsOverviewScreen();
    }
}