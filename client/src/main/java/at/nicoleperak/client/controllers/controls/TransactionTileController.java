package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static at.nicoleperak.client.FXMLLocation.EXPANDED_TRANSACTION_TILE;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.factories.ExpandedTransactionTileFactory.buildExpandedTransactionTile;

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

    @FXML
    void onTransactionTileClicked() {
        expandTransactionTile();
    }

    /**
     * Expands a (not expanded) transaction tile control by replacing it with an expanded transaction tile control.
     */
    private void expandTransactionTile() {
        try {
            ObservableList<Node> transactionTileList = transactionsPane.getChildren();
            int tileIndex = transactionTileList.indexOf(transactionTile);
            FXMLLoader loader = EXPANDED_TRANSACTION_TILE.getLoader();
            VBox transactionDetailsTile = buildExpandedTransactionTile(transaction, loader, transactionsPane);
            transactionTileList.set(tileIndex, transactionDetailsTile);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
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
