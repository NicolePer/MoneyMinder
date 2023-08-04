package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.FinancialAccount;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.CREATE_FINANCIAL_ACCOUNT_FORM;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;

@SuppressWarnings("unused")

public class CreateFinancialAccountTileController{

    private static final Jsonb jsonb = JsonbBuilder.create();

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
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showCreateFinancialAccountDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(CREATE_FINANCIAL_ACCOUNT_FORM.getLocation()));
            DialogPane createFinancialAccountDialogPane = loader.load();
            CreateFinancialAccountDialogController dialogController = loader.getController();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(createFinancialAccountDialogPane);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.FINISH) {
                    String title = dialogController.getFinancialAccountTitleField().getText();
                    String description = dialogController.getFinancialAccountDescriptionField().getText();
                    FinancialAccount financialAccount = new FinancialAccount(title, description);
                    try {
                        ServiceFunctions.post("financial-accounts", jsonb.toJson(financialAccount), true);
                        reloadFinancialAccountsOverviewScreen();
                    } catch (ClientException e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}