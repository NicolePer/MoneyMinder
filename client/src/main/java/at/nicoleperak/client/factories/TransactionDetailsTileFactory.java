package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.TransactionDetailsTileController;
import at.nicoleperak.shared.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;

import static at.nicoleperak.client.Format.formatBalance;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;

public class TransactionDetailsTileFactory {

    public static VBox buildTransactionDetailsTile(Transaction transaction, FXMLLoader loader, VBox transactionsPane) throws IOException {
        VBox transactionDetailsTile = loader.load();
        TransactionDetailsTileController controller = loader.getController();
        controller
                .getTransactionDateLabel()
                .setText(transaction.getDate().format(ofLocalizedDate(MEDIUM).withLocale(Locale.US)).toUpperCase());
        controller
                .getTransactionPartnerLabel()
                .setText(transaction.getTransactionPartner().toUpperCase());
        controller
                .getTransactionDescriptionLabel()
                .setText(transaction.getDescription().toUpperCase());
        controller.getTransactionAmountLabel()
                .setText(formatBalance(transaction.getAmount()));
        controller
                .getTransactionTypeLabel()
                .setText(transaction.getCategory().getType().name());
        controller
                .getTransactionCategoryLabel()
                .setText(transaction.getCategory().getTitle());
        controller
                .getTransactionNoteLabel()
                .setText(transaction.getNote());
        controller
                .getTransactionAddedLabel()
                .setText(transaction.isAddedAutomatically() ? "Automatically" : "Manually");
        controller
                .setTransaction(transaction);
        controller
                .setTransactionsPane(transactionsPane);

        return transactionDetailsTile;
    }
}
