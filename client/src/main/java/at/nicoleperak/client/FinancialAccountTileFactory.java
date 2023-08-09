package at.nicoleperak.client;

import at.nicoleperak.client.controllers.FinancialAccountTileController;
import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

import static at.nicoleperak.client.Format.formatBalance;

public class FinancialAccountTileFactory {
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
