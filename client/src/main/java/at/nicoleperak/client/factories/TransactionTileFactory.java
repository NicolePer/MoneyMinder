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

    /**
     * Constructs a transaction tile for displaying  information about a transaction.
     *
     * @param transaction      The Transaction object containing transaction information.
     * @param loader           The FXMLLoader instance used for loading the transaction tile layout.
     * @param transactionsPane The VBox in which the transaction tile will be placed.
     * @return A transaction tile containing information about the transactions.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
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
