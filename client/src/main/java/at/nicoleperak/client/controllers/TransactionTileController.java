package at.nicoleperak.client.controllers;

import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static at.nicoleperak.client.FXMLLocation.TRANSACTION_DETAILS_TILE;
import static at.nicoleperak.client.factories.TransactionDetailsTileFactory.buildTransactionDetailsTile;
import static javafx.scene.control.Alert.AlertType.*;

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
            ObservableList<Node> transactionTileList = transactionsPane.getChildren();
            int tileIndex = transactionTileList.indexOf(transactionTile);
            FXMLLoader loader = TRANSACTION_DETAILS_TILE.getLoader();
            VBox transactionDetailsTile = buildTransactionDetailsTile(transaction, loader, transactionsPane);
            transactionTileList.set(tileIndex, transactionDetailsTile);
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
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
