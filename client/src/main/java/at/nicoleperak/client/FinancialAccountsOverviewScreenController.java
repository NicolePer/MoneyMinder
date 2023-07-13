package at.nicoleperak.client;

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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;

public class FinancialAccountsOverviewScreenController implements Initializable {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private Label alertMessagetLabel;

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

    public void showFinancialAccounts() {
        try {
            String jsonResponse = ServiceFunctions.get("financial-accounts");
            FinancialAccountsList financialAccountsList = jsonb.fromJson(jsonResponse, FinancialAccountsList.class);
            List<FinancialAccount> financialAccounts = financialAccountsList.getFinancialAccounts();
            for (FinancialAccount financialAccount : financialAccounts) {
                FXMLLoader accountTileLoader = new FXMLLoader();
                accountTileLoader.setLocation(getClass().getResource("/financial-account-tile.fxml"));
                Parent accountTile = accountTileLoader.load();
                FinancialAccountTileController accountTileController = accountTileLoader.getController();
                accountTileController.getFinancialAccountBalanceLabel().setText(formatBalance(financialAccount.getBalance()));
                accountTileController.getFinancialAccountTitleLabel().setText(financialAccount.getTitle());
                financialAccountsTilePane.getChildren().add(accountTile);
            }
            showCreateFinancialAccountTile();
        } catch (IOException | ClientException e) {
            alertMessagetLabel.setText(e.getMessage());
        }
    }

    private String formatBalance(BigDecimal balance) {
        balance = balance.setScale(2, RoundingMode.DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingSize(3);
        return df.format(balance) + " €";
    }


    public void showCreateFinancialAccountTile() {
        try {
            FXMLLoader createAccountTileLoader = new FXMLLoader();
            createAccountTileLoader.setLocation(getClass().getResource("/create-financial-account-tile.fxml"));
            Parent createAccountTile = createAccountTileLoader.load();
            CreateFinancialAccountTileController createAccountTileController = createAccountTileLoader.getController();
            financialAccountsTilePane.getChildren().add(createAccountTile);
        } catch (IOException e) {
            alertMessagetLabel.setText(e.getMessage());
        }
    }

    public TilePane getFinancialAccountsTilePane() {
        return financialAccountsTilePane;
    }

    public Label getAlertMessagetLabel() { return alertMessagetLabel; }
}
