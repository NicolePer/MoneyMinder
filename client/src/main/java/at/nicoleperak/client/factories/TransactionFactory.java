package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.TransactionDialogController;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Format.formatAmount;

public class TransactionFactory {

    public static Transaction buildTransaction(TransactionDialogController formController) {
        LocalDate date = formController.getDatePicker().getValue();
        String transactionPartner = formController.getTransactionPartnerField().getText();
        String description = formController.getDescriptionField().getText();
        Category category = (Category) formController.getCategoryComboBox().getSelectionModel().getSelectedItem();
        String amountString = convertIntoParsableDecimal(formController.getAmountField().getText());
        BigDecimal amount = new BigDecimal(formatAmount(amountString, category));
        String note = formController.getNoteArea().getText();
        return new Transaction(null, description, amount, date, category, transactionPartner, note, false);
    }
}
