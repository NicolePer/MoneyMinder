package at.nicoleperak.server;

import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.Transaction;

public class TransactionFactory {

    public static Transaction buildFromRecurringTransactionOrder(RecurringTransactionOrder order) {
        return new Transaction(null,
                order.getDescription(),
                order.getAmount(),
                order.getNextDate(),
                order.getCategory(),
                order.getTransactionPartner(),
                order.getNote(),
                true);
    }
}
