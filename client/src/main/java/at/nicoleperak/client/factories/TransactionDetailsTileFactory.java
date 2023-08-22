package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.TransactionDetailsTileController;
import at.nicoleperak.shared.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static at.nicoleperak.client.Format.formatBalance;
import static at.nicoleperak.shared.Category.CategoryType.Income;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.LONG;
import static java.time.format.FormatStyle.MEDIUM;
import static java.util.Locale.US;

public class TransactionDetailsTileFactory {

    public static VBox buildTransactionDetailsTile(Transaction transaction, FXMLLoader loader, VBox transactionsPane) throws IOException {
        VBox transactionDetailsTile = loader.load();
        TransactionDetailsTileController controller = loader.getController();
        controller
                .getTransactionDateLabel()
                .setText(transaction.getDate().format(ofLocalizedDate(MEDIUM).withLocale(US)).toUpperCase());
        controller
                .getTransactionDateLabel2()
                .setText(transaction.getDate().format(ofLocalizedDate(LONG).withLocale(US)));
        controller
                .getTransactionPartnerLabel()
                .setText(transaction.getTransactionPartner().toUpperCase());
        controller
                .getTransactionPartnerLabel2()
                .setText(transaction.getTransactionPartner());
        controller
                .getTransactionDescriptionLabel()
                .setText(transaction.getDescription().toUpperCase());
        controller
                .getTransactionDescriptionLabel2()
                .setText(transaction.getDescription());
        controller
                .getTransactionAmountLabel()
                .setText(formatBalance(transaction.getAmount()));
        controller
                .getTransactionAmountLabel2()
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
        controller
                .getTransactionPartnerTitleLabel()
                .setText(transaction.getCategory().getType().equals(Income) ? "Source:" : "Recipient:");
        return transactionDetailsTile;
    }
}
