package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.FinancialAccountTileController;
import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import static at.nicoleperak.client.Format.formatBalance;


public class FinancialAccountTileFactory {

    /**
     * Constructs a financial account tile for displaying financial account information.
     *
     * @param financialAccount The FinancialAccount object representing the financial account.
     * @param loader           The FXMLLoader instance used for loading the financial account tile layout.
     * @return A new financial account tile control containing information about the financial account.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    public static Parent buildFinancialAccountTile(FinancialAccount financialAccount, FXMLLoader loader) throws IOException {
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
