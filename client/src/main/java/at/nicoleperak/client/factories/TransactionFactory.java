package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.dialogs.TransactionDialogController;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Format.formatAmount;

public class TransactionFactory {

    /**
     * Constructs a new transaction based on user input from the dialog.
     *
     * @param controller         The controller of the dialog gathering transaction information.
     * @param addedAutomatically Indicates whether the transaction is added automatically.
     * @return A new Transaction instance with the specified attributes.
     */
    public static Transaction buildTransaction(TransactionDialogController controller, boolean addedAutomatically) {
        LocalDate date = controller.getDatePicker().getValue();
        String transactionPartner = controller.getTransactionPartnerField().getText();
        String description = controller.getDescriptionField().getText();
        Category category = controller.getCategoryComboBox().getSelectionModel().getSelectedItem();
        String amountString = convertIntoParsableDecimal(controller.getAmountField().getText());
        BigDecimal amount = new BigDecimal(formatAmount(amountString, category));
        String note = controller.getNoteArea().getText();
        return new Transaction(null, description, amount, date, category, transactionPartner, note, addedAutomatically);
    }
}
