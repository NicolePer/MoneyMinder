package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.ExpandedTransactionTileController;
import at.nicoleperak.shared.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static at.nicoleperak.client.Format.formatBalance;
import static at.nicoleperak.shared.Category.CategoryType.INCOME;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;
import static java.util.Locale.US;

public class ExpandedTransactionTileFactory {

    /**
     * Constructs an expanded transactions tile for displaying detailed information about a transaction.
     *
     * @param transaction      The Transaction object containing transaction information.
     * @param loader           The FXMLLoader instance used for loading the transaction details tile layout.
     * @param transactionsPane The VBox in which the transaction details will be placed.
     * @return An expanded transaction tile containing details about the transactions.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    public static VBox buildExpandedTransactionTile(Transaction transaction, FXMLLoader loader, VBox transactionsPane) throws IOException {
        VBox transactionDetailsTile = loader.load();
        ExpandedTransactionTileController controller = loader.getController();
        controller
                .getTransactionDateLabel()
                .setText(transaction.getDate().format(ofLocalizedDate(MEDIUM).withLocale(US)).toUpperCase());
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
                .getTransactionTypeLabel()
                .setText(transaction.getCategory().getType().getLabel());
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
                .setText(transaction.getCategory().getType().equals(INCOME) ? "Source:" : "Recipient:");
        return transactionDetailsTile;
    }
}
