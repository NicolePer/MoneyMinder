package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.TransactionDialogController;
import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.TRANSACTION_FORM;
import static at.nicoleperak.client.FXMLLocation.TRANSACTION_TILE;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.TransactionFactory.buildTransaction;
import static at.nicoleperak.client.factories.TransactionTileFactory.buildTransactionTile;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.ButtonType.FINISH;

public class TransactionDetailsTileController {

    private Transaction transaction;
    private VBox transactionsPane;

    @FXML
    private Label transactionAddedLabel;

    @FXML
    private Button closeDetailsButton;

    @FXML
    private Button editTransactionButton;

    @FXML
    private Button removeTransactionButton;

    @FXML
    private Label transactionAmountLabel;

    @FXML
    private Label transactionCategoryLabel;

    @FXML
    private Label transactionDateLabel;

    @FXML
    private Label transactionDescriptionLabel;

    @FXML
    private Label transactionNoteLabel;

    @FXML
    private Label transactionPartnerLabel;

    @FXML
    private Label transactionTypeLabel;

    @FXML
    private VBox transactionDetailsTile;

    @FXML
    @SuppressWarnings("unused")
    void onCloseDetailsButtonClicked(ActionEvent event) {
        closeTransactionDetailsTile();
    }

    @FXML
    @SuppressWarnings("unused")
    void onEditTransactionButtonClicked(ActionEvent event) {
        showEditTransactionDialog();
    }

    @FXML
    @SuppressWarnings("unused")
    void onRemoveTransactionButtonClicked(ActionEvent event) {
        removeTransaction();
    }

    @FXML
    @SuppressWarnings("unused")
    void onTransactionOverviewTileClicked(MouseEvent event) {
        closeTransactionDetailsTile();
    }

    private void closeTransactionDetailsTile() {
        try {
            ObservableList<Node> transactionTileList = transactionsPane.getChildren();
            int tileIndex = transactionTileList.indexOf(transactionDetailsTile);
            FXMLLoader loader = TRANSACTION_TILE.getLoader();
            Parent transactionTile = buildTransactionTile(transaction, loader, transactionsPane);
            transactionTileList.set(tileIndex, transactionTile);
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showEditTransactionDialog() {
        try {
            FXMLLoader loader = TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            TransactionDialogController controller = loader.getController();
            controller.setSelectedTransaction(transaction);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH){
                Transaction editedTransaction = buildTransaction(controller, false);
                putEditedTransaction(editedTransaction);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void putEditedTransaction(Transaction editedTransaction) {
        try {
            put("transactions/" + transaction.getId(), jsonb.toJson(editedTransaction));
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void removeTransaction() {
        try {
            delete("transactions/" + transaction.getId());
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    public Label getTransactionAmountLabel() {
        return transactionAmountLabel;
    }

    public Label getTransactionCategoryLabel() {
        return transactionCategoryLabel;
    }

    public Label getTransactionDateLabel() {
        return transactionDateLabel;
    }

    public Label getTransactionDescriptionLabel() {
        return transactionDescriptionLabel;
    }

    public Label getTransactionNoteLabel() {
        return transactionNoteLabel;
    }

    public Label getTransactionPartnerLabel() {
        return transactionPartnerLabel;
    }

    public Label getTransactionTypeLabel() {
        return transactionTypeLabel;
    }

    public Label getTransactionAddedLabel() {
        return transactionAddedLabel;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setTransactionsPane(VBox transactionsPane) {
        this.transactionsPane = transactionsPane;
    }

}

