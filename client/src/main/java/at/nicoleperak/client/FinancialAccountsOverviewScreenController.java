package at.nicoleperak.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class FinancialAccountsOverviewScreenController implements Initializable {

    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private TilePane financialAccountsTilePane;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuButton menuButton;

    @FXML
    private GridPane newFinancialAccountTile;

    @FXML
    private Label userLabel;

    public TilePane getFinancialAccountsTilePane() {
        return financialAccountsTilePane;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showFinancialAccounts();
    }

    public void showFinancialAccounts() {
        try {
            for (String kontoName : List.of("Haushaltskonto", "Sparkonto", "Reservekonto")) {
                FXMLLoader accountTileLoader = new FXMLLoader();
                accountTileLoader.setLocation(getClass().getResource("/financial-account-tile.fxml"));
                Parent accountTile = accountTileLoader.load();
                FinancialAccountTileController accountTileController = accountTileLoader.getController();
                accountTileController.getFinancialAccountBalanceLabel().setText("1.000€");
                accountTileController.getFinancialAccountTitleLabel().setText(kontoName);
                financialAccountsTilePane.getChildren().add(accountTile);
            }
            showCreateFinancialAccountTile();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public void showCreateFinancialAccountTile() {
        try {
            FXMLLoader createAccountTileLoader = new FXMLLoader();
            createAccountTileLoader.setLocation(getClass().getResource("/create-financial-account-tile.fxml"));
            Parent createAccountTile = createAccountTileLoader.load();
            CreateFinancialAccountTileController createAccountTileController = createAccountTileLoader.getController();
            financialAccountsTilePane.getChildren().add(createAccountTile);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
