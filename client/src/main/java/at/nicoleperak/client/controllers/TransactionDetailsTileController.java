package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

import static at.nicoleperak.client.FXMLLocation.*;
import static at.nicoleperak.client.Format.*;
import static at.nicoleperak.client.controllers.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;

public class TransactionDetailsTileController {

    private static final Jsonb jsonb = JsonbBuilder.create();

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
    void onCloseDetailsButtonClicked(ActionEvent event) {
        closeTransactionDetailsTile();
    }

    @FXML
    void onEditTransactionButtonClicked(ActionEvent event) {
        showEditTransactionDialog();
    }

    @FXML
    void onRemoveTransactionButtonClicked(ActionEvent event) {
        removeTransaction();
    }

    @FXML
    void onTransactionOverviewTileClicked(MouseEvent event) {
        closeTransactionDetailsTile();
    }

    private void closeTransactionDetailsTile() {
        try {
            ObservableList<javafx.scene.Node> transactionTileList = transactionsPane.getChildren();
            int transactionDetailsTileIndex = transactionTileList.indexOf(transactionDetailsTile);

            FXMLLoader transactionTileLoader = new FXMLLoader();
            transactionTileLoader.setLocation(getClass().getResource(TRANSACTION_TILE.getLocation()));
            Parent transactionTile = buildTransactionTile(transaction, transactionTileLoader);
            transactionTileList.add((transactionDetailsTileIndex + 1), transactionTile);
            transactionTileList.remove(transactionDetailsTileIndex);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public Parent buildTransactionTile(Transaction transaction, FXMLLoader loader) throws IOException {
        Parent transactionTile = loader.load();
        TransactionTileController controller = loader.getController();
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
                .setTransaction(transaction);
        controller
                .setTransactionsPane(transactionsPane);
        return transactionTile;
    }

    private void showEditTransactionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(TRANSACTION_FORM.getLocation()));
            DialogPane createFinancialAccountDialogPane = loader.load();
            TransactionDialogController dialogController = loader.getController();
            dialogController.setSelectedTransaction(transaction);
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(createFinancialAccountDialogPane);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.FINISH) {
                    LocalDate date = dialogController.getDatePicker().getValue();
                    String transactionPartner = dialogController.getTransactionPartnerField().getText();
                    String description = dialogController.getDescriptionField().getText();
                    Category category = (Category) dialogController.getCategoryComboBox().getSelectionModel().getSelectedItem();
                    String amountString = convertIntoParsableDecimal(dialogController.getAmountField().getText());
                    BigDecimal amount = new BigDecimal(formatAmount(amountString, category));
                    String note = dialogController.getNoteArea().getText();
                    Transaction editedTransaction = new Transaction(null, description, amount, date, category, transactionPartner, note, false);
                    try {
                        ServiceFunctions.put("transactions/" + transaction.getId(), jsonb.toJson(editedTransaction));
                        reloadFinancialAccountDetailsScreen();
                    } catch (ClientException e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void removeTransaction(){
        try {
            ServiceFunctions.delete("transactions/" + transaction.getId());
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
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

    // TODO FACTORY CLASS
}

