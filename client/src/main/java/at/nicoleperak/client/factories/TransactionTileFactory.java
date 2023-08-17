package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.TransactionTileController;
import at.nicoleperak.shared.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static at.nicoleperak.client.Format.formatBalance;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;
import static java.util.Locale.US;

public class TransactionTileFactory {

    public static Parent buildTransactionTile(Transaction transaction, FXMLLoader loader, VBox transactionsPane) throws IOException {
        Parent transactionTile = loader.load();
        TransactionTileController controller = loader.getController();
        controller
                .getTransactionDateLabel()
                .setText(transaction.getDate().format(ofLocalizedDate(MEDIUM).withLocale(US)).toUpperCase());
        controller
                .getTransactionPartnerLabel()
                .setText(transaction.getTransactionPartner().toUpperCase());
        controller
                .getTransactionDescriptionLabel()
                .setText(transaction.getDescription().toUpperCase());
        controller.getTransactionAmountLabel()
                .setText(formatBalance(transaction.getAmount()));
        controller
                .setTransaction(transaction);
        controller
                .setTransactionsPane(transactionsPane);
        return transactionTile;
    }
}
