package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.CREATE_FINANCIAL_ACCOUNT_FORM;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static javafx.scene.control.Alert.AlertType.*;
import static javafx.scene.control.ButtonType.*;

@SuppressWarnings("unused")

public class CreateFinancialAccountTileController{

    @FXML
    private GridPane createFinancialAccountTile;

    @FXML
    protected void onCreateFinancialAccountTileClicked(MouseEvent event) {
        showCreateFinancialAccountDialog();
    }

    private void reloadFinancialAccountsOverviewScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showCreateFinancialAccountDialog() {
        try {
            FXMLLoader loader = CREATE_FINANCIAL_ACCOUNT_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            CreateFinancialAccountDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                    FinancialAccount financialAccount = buildFinancialAccountObject(controller);
                    try {
                        post("financial-accounts", jsonb.toJson(financialAccount), true);
                        reloadFinancialAccountsOverviewScreen();
                    } catch (ClientException e) {
                        new Alert(ERROR, e.getMessage()).showAndWait();
                    }
                }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private static FinancialAccount buildFinancialAccountObject(CreateFinancialAccountDialogController controller) {
        String title = controller.getFinancialAccountTitleField().getText();
        String description = controller.getFinancialAccountDescriptionField().getText();
        return new FinancialAccount(title, description);
    }
}