package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import static at.nicoleperak.client.Client.setSelectedFinancialAccount;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsDetailsScreen;

public class FinancialAccountTileController extends GridPane {
    @FXML
    private Label financialAccountBalanceLabel;
    @FXML
    private Label financialAccountTitleLabel;
    private FinancialAccount financialAccount;

    /**
     * This method is called when a user selects a financial account from the overview.
     * The selected financial account is stored and
     * the user is redirected to the financial account details screen.
     */
    @FXML
    protected void onFinancialAccountTileClicked() {
        setSelectedFinancialAccount(financialAccount);
        redirectToFinancialAccountsDetailsScreen();
    }

    public Label getFinancialAccountBalanceLabel() {
        return financialAccountBalanceLabel;
    }

    public Label getFinancialAccountTitleLabel() {
        return financialAccountTitleLabel;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }
}
