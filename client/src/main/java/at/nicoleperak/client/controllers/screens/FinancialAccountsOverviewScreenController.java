package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.controls.NavigationBarController;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.FinancialAccountsList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static at.nicoleperak.client.FXMLLocation.*;
import static at.nicoleperak.client.ServiceFunctions.get;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.factories.FinancialAccountTileFactory.buildFinancialAccountTile;

public class FinancialAccountsOverviewScreenController implements Initializable {
    @FXML
    private TilePane financialAccountsTilePane;
    @FXML
    private VBox screenPane;

    /**
     * Upon initialization inserts the navigation bar control into the screenPane and
     * displays an overview of all financial accounts where the user is owner or collaborator.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        insertNavigationBar();
        showFinancialAccounts();
    }

    /**
     * Inserts the navigation bar control into screenPane.
     */
    private void insertNavigationBar() {
        try {
            FXMLLoader loader = NAVIGATION_BAR.getLoader();
            HBox navigationBarBox = loader.load();
            NavigationBarController controller = loader.getController();
            controller.getNavigationBarBox().getChildren().remove(controller.getGoBackIcon());
            screenPane.getChildren().add(0, navigationBarBox);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Inserts a dynamic amount of financial account tiles into the financialAccountsTilePane
     * to display an overview of all financial accounts where the user is owner or collaborator to the user.
     */
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
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Inserts a createFinancialAccountTile control into the financialAccountsTilePane
     *
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    private void showCreateFinancialAccountTile() throws IOException {
        FXMLLoader loader = CREATE_FINANCIAL_ACCOUNT_TILE.getLoader();
        Parent createAccountTile = loader.load();
        financialAccountsTilePane.getChildren().add(createAccountTile);
    }
}
