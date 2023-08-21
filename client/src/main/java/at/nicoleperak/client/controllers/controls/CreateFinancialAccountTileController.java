package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.CreateFinancialAccountDialogController;
import at.nicoleperak.client.factories.FinancialAccountFactory;
import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.CREATE_FINANCIAL_ACCOUNT_FORM;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.post;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static javafx.scene.control.ButtonType.FINISH;

public class CreateFinancialAccountTileController {

    @FXML
    protected void onCreateFinancialAccountTileClicked() {
        showCreateFinancialAccountDialog();
    }

    private void showCreateFinancialAccountDialog() {
        try {
            FXMLLoader loader = CREATE_FINANCIAL_ACCOUNT_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            CreateFinancialAccountDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                FinancialAccount financialAccount = FinancialAccountFactory.buildFinancialAccount(controller);
                post("financial-accounts", jsonb.toJson(financialAccount), true);
                reloadFinancialAccountsOverviewScreen();
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void reloadFinancialAccountsOverviewScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }
}