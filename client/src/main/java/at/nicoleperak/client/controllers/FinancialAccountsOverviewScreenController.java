package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.FinancialAccountsList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static at.nicoleperak.client.FXMLLocation.CREATE_FINANCIAL_ACCOUNT_TILE;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNT_TILE;
import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.factories.FinancialAccountTileFactory.buildFinancialAccountTile;

public class FinancialAccountsOverviewScreenController implements Initializable {


    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private Label alertMessageLabel;

    @FXML
    private TilePane financialAccountsTilePane;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuButton menuButton;

    @FXML
    private Label userLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userLabel.setText(Client.getLoggedInUser().getUsername());
        showFinancialAccounts();
    }

    private void showFinancialAccounts() {
        try {
            String jsonResponse = get("financial-accounts");
            FinancialAccountsList financialAccountsList = jsonb.fromJson(jsonResponse, FinancialAccountsList.class);
            List<FinancialAccount> financialAccounts = financialAccountsList.getFinancialAccounts();
            for (FinancialAccount financialAccount : financialAccounts) {
                FXMLLoader loader = FINANCIAL_ACCOUNT_TILE.getLoader();
                Parent accountTile = buildFinancialAccountTile(financialAccount, loader);
                financialAccountsTilePane.getChildren().add(accountTile);
            }
            showCreateFinancialAccountTile();
        } catch (IOException | ClientException e) {
            alertMessageLabel.setText(e.getMessage());
        }
    }

    private void showCreateFinancialAccountTile() throws IOException {
        FXMLLoader loader = CREATE_FINANCIAL_ACCOUNT_TILE.getLoader();
        Parent createAccountTile = loader.load();
        financialAccountsTilePane.getChildren().add(createAccountTile);
    }

    @FXML
    void onLogoutMenuItemClicked(ActionEvent event) {
        redirectToWelcomeScreen();
    }

}
