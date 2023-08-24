package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.CreateTransactionDialogController;
import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.CREATE_TRANSACTION_FORM;
import static at.nicoleperak.client.FXMLLocation.TRANSACTION_TILE;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderSuccessAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.TransactionFactory.buildTransaction;
import static at.nicoleperak.client.factories.TransactionTileFactory.buildTransactionTile;
import static javafx.scene.control.ButtonType.FINISH;

public class TransactionDetailsTileController {

    private Transaction transaction;
    private VBox transactionsPane;

    @FXML
    private Label transactionAddedLabel;

    @FXML
    private Label transactionAmountLabel;

    @FXML
    private Label transactionCategoryLabel;

    @FXML
    private Label transactionDateLabel;

    @FXML
    private Label transactionDescriptionLabel;

    @FXML
    private Label transactionDescriptionLabel2;

    @FXML
    private Label transactionNoteLabel;

    @FXML
    private Label transactionPartnerLabel;

    @FXML
    private Label transactionPartnerLabel2;

    @FXML
    private Label transactionPartnerTitleLabel;

    @FXML
    private Label transactionTypeLabel;

    @FXML
    private VBox transactionDetailsTile;

    @FXML
    void onCloseDetailsButtonClicked() {
        closeTransactionDetailsTile();
    }

    @FXML
    void onEditTransactionButtonClicked() {
        showEditTransactionDialog();
    }

    @FXML
    void onRemoveTransactionButtonClicked() {
        removeTransaction();
    }

    @FXML
    void onTransactionOverviewTileClicked() {
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
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void showEditTransactionDialog() {
        try {
            FXMLLoader loader = CREATE_TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            CreateTransactionDialogController controller = loader.getController();
            controller.setSelectedTransaction(transaction);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                Transaction editedTransaction = buildTransaction(controller, false);
                putEditedTransaction(editedTransaction);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void putEditedTransaction(Transaction editedTransaction) throws ClientException {
        put("transactions/" + transaction.getId(), jsonb.toJson(editedTransaction));
        showMoneyMinderSuccessAlert("Changes saved");
        reloadFinancialAccountDetailsScreen();
    }

    private void removeTransaction() {
        if (userHasConfirmedActionWhenAskedForConfirmation(
                "Are you sure you want to delete this transaction?")) {
            try {
                delete("transactions/" + transaction.getId());
                reloadFinancialAccountDetailsScreen();
            } catch (ClientException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
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

    public Label getTransactionDescriptionLabel2() {
        return transactionDescriptionLabel2;
    }

    public Label getTransactionPartnerLabel2() {
        return transactionPartnerLabel2;
    }

    public Label getTransactionPartnerTitleLabel() {
        return transactionPartnerTitleLabel;
    }
}

