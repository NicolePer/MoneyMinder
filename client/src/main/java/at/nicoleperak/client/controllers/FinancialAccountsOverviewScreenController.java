package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.FinancialAccountsList;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
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
import static at.nicoleperak.client.Format.formatBalance;

public class FinancialAccountsOverviewScreenController implements Initializable {

    private static final Jsonb jsonb = JsonbBuilder.create();

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
        showFinancialAccounts();
    }

    private void showFinancialAccounts() {
        try {
            String jsonResponse = ServiceFunctions.get("financial-accounts");
            FinancialAccountsList financialAccountsList = jsonb.fromJson(jsonResponse, FinancialAccountsList.class);
            List<FinancialAccount> financialAccounts = financialAccountsList.getFinancialAccounts();
            for (FinancialAccount financialAccount : financialAccounts) {
                FXMLLoader financialAccountTileLoader = new FXMLLoader();
                financialAccountTileLoader.setLocation(getClass().getResource(FINANCIAL_ACCOUNT_TILE.getLocation()));
                Parent accountTile = buildFinancialAccountTile(financialAccount, financialAccountTileLoader);
                financialAccountsTilePane.getChildren().add(accountTile);
            }
            showCreateFinancialAccountTile();
        } catch (IOException | ClientException e) {
            alertMessageLabel.setText(e.getMessage());
        }
    }

    private void showCreateFinancialAccountTile() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(CREATE_FINANCIAL_ACCOUNT_TILE.getLocation()));
        Parent createAccountTile = loader.load();
        financialAccountsTilePane.getChildren().add(createAccountTile);
    }

    private static Parent buildFinancialAccountTile(FinancialAccount financialAccount, FXMLLoader loader) throws IOException {
        Parent accountTile = loader.load();
        FinancialAccountTileController controller = loader.getController();
        controller
                .getFinancialAccountBalanceLabel()
                .setText(formatBalance(financialAccount.getBalance()));
        controller
                .getFinancialAccountTitleLabel()
                .setText(financialAccount.getTitle());
        controller
                .setFinancialAccount(financialAccount);
        return accountTile;
    }
}
