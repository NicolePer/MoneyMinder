package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.TransactionDialogController;
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
import static at.nicoleperak.client.FXMLLocation.TRANSACTION_FORM;
import static at.nicoleperak.client.FXMLLocation.TRANSACTION_TILE;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderSuccessAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.TransactionFactory.buildTransaction;
import static at.nicoleperak.client.factories.TransactionTileFactory.buildTransactionTile;
import static javafx.scene.control.ButtonType.FINISH;

public class ExpandedTransactionTileController {
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
        closeExpandedTransactionTile();
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
        closeExpandedTransactionTile();
    }

    /**
     * Closes an expanded transaction tile by replacing the control with a (not expanded) transaction tile  control.
     */
    private void closeExpandedTransactionTile() {
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

    /**
     * Shows the "Edit Transaction" dialog to the user.
     * Upon finish, updates the transaction.
     */
    private void showEditTransactionDialog() {
        try {
            FXMLLoader loader = TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            TransactionDialogController controller = loader.getController();
            controller.setSelectedTransaction(transaction);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                updateTransaction(controller);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Takes user inputs from dialog and creates new Transaction object.
     * Sends put-request to server. Then shows success message to user and reloads the screen.
     *
     * @param controller The FXML controller of the dialog that gathered the input from the user.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void updateTransaction(TransactionDialogController controller) throws ClientException {
        Transaction editedTransaction = buildTransaction(controller, false);
        put("transactions/" + transaction.getId(), jsonb.toJson(editedTransaction));
        showMoneyMinderSuccessAlert("Changes saved");
        reloadFinancialAccountDetailsScreen();
    }

    /**
     * Sends delete-request to server to remove a transaction from a financial account,
     * following the user's confirmation.
     * Then displays success message to user and reloads the screen.
     */
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

