package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.dialogs.RecurringTransactionDialogController;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.RecurringTransactionOrder;

import java.math.BigDecimal;
import java.time.LocalDate;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Format.formatAmount;

public class RecurringTransactionOrderFactory {

    public static RecurringTransactionOrder buildRecurringTransactionOrder(RecurringTransactionDialogController controller) {
        LocalDate nextDate = controller.getNextDatePicker().getValue();
        LocalDate endDate = controller.getEndDatePicker().getValue();
        RecurringTransactionOrder.Interval interval = controller.getIntervalComboBox().getSelectionModel().getSelectedItem();
        String transactionPartner = controller.getTransactionPartnerField().getText();
        String description = controller.getDescriptionField().getText();
        Category category = controller.getCategoryComboBox().getSelectionModel().getSelectedItem();
        String amountString = convertIntoParsableDecimal(controller.getAmountField().getText());
        BigDecimal amount = new BigDecimal(formatAmount(amountString, category));
        String note = controller.getNoteArea().getText();
        return new RecurringTransactionOrder(null, description, amount, category, transactionPartner, note, nextDate, endDate, interval);
    }
}
