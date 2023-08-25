package at.nicoleperak.server;

import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.Transaction;

public class TransactionFactory {

    /**
     * Builds a transaction based on the given recurring transaction order.
     *
     * @param order The recurring transaction order.
     * @return The transaction.
     */
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
