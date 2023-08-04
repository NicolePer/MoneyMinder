package at.nicoleperak.client.controllers;

import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;

import static at.nicoleperak.client.FXMLLocation.TRANSACTION_DETAILS_TILE;
import static at.nicoleperak.client.Format.formatBalance;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;

public class TransactionTileController {

    private Transaction transaction;

    private VBox transactionsPane;

    @FXML
    private Label transactionAmountLabel;

    @FXML
    private Label transactionDateLabel;

    @FXML
    private Label transactionDescriptionLabel;

    @FXML
    private Label transactionPartnerLabel;

    @FXML
    private GridPane transactionTile;

    @FXML @SuppressWarnings("unused")
    void onTransactionTileClicked(MouseEvent event) {
        try {
            ObservableList<javafx.scene.Node> transactionTileList = transactionsPane.getChildren();
            int transactionTileIndex = transactionTileList.indexOf(transactionTile);
            FXMLLoader transactionDetailsTileLoader = new FXMLLoader();
            transactionDetailsTileLoader.setLocation(getClass().getResource(TRANSACTION_DETAILS_TILE.getLocation()));
            VBox transactionDetailsTile = buildTransactionDetailsTile(transaction, transactionDetailsTileLoader);
            transactionTileList.set(transactionTileIndex, transactionDetailsTile);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private VBox buildTransactionDetailsTile(Transaction transaction, FXMLLoader loader) throws IOException {
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

    public Label getTransactionAmountLabel() {
        return transactionAmountLabel;
    }

    public Label getTransactionDateLabel() {
        return transactionDateLabel;
    }

    public Label getTransactionDescriptionLabel() {
        return transactionDescriptionLabel;
    }


    public Label getTransactionPartnerLabel() {
        return transactionPartnerLabel;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setTransactionsPane(VBox transactionsPane) {
        this.transactionsPane = transactionsPane;
    }

}
