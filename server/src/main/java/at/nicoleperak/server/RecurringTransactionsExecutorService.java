package at.nicoleperak.server;

import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.Transaction;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static at.nicoleperak.server.TransactionFactory.buildFromRecurringTransactionOrder;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.*;
import static at.nicoleperak.shared.RecurringTransactionOrder.Interval;
import static java.time.ZonedDateTime.now;
import static java.time.ZonedDateTime.of;
import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

@SuppressWarnings("CallToPrintStackTrace")
public class RecurringTransactionsExecutorService implements AutoCloseable {
    private final ScheduledExecutorService executorService;

    public RecurringTransactionsExecutorService() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Initiates the execution of {@link #executeOutstandingRecurringTransactionOrders()} at midnight every day.
     */
    public void scheduleService() {
        ZonedDateTime now = now(ZoneId.of("Europe/Vienna"));
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        ZonedDateTime startOfTomorrow = of(tomorrow, LocalTime.MIN, ZoneId.of("Europe/Vienna"));
        Duration d = Duration.between(now, startOfTomorrow);
        long minutesUnitStartOfTomorrow = d.toMinutes();
        executorService.scheduleAtFixedRate(() -> {
                    try {
                        executeOutstandingRecurringTransactionOrders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                minutesUnitStartOfTomorrow,
                DAYS.toMinutes(1),
                MINUTES
        );
        System.out.println("RecurringTransactionExecutor: recurring transaction execution scheduled");
    }

    /**
     * Iterates over all recurring transaction orders and executes them in case they are outstanding, i.e., if the
     * day of their next executing is today or lies in the past.
     * For every execution, a new transaction based on the data of the respective is created.
     * In case an order has not been executed for longer than its interval, multiple transactions might be created.
     */
    public void executeOutstandingRecurringTransactionOrders() {
        try {
            int outstandingOrderCounter = 0;
            int executionCounter = 0;
            List<RecurringTransactionOrder> outstandingOrders = selectListOfOutstandingOrders();
            LinkedList<RecurringTransactionOrder> queue = new LinkedList<>(outstandingOrders);
            while (queue.peek() != null) {
                System.out.println("RecurringTransactionExecutor: " + queue.size() + " outstanding recurring transaction orders retrieved");
                outstandingOrderCounter += queue.size();
                RecurringTransactionOrder order = queue.remove();
                try {
                    Transaction transaction = buildFromRecurringTransactionOrder(order);
                    Long financialAccountId = selectFinancialAccountId(order.getId());
                    setNewNextDate(order);
                    if (order.getNextDate().equals(LocalDate.MAX)) {
                        insertTransactionAndDeleteOrder(order, transaction, financialAccountId);
                    } else {
                        insertTransactionAndUpdateOrder(order, transaction, financialAccountId);
                        if (isOutstanding(order)) {
                            queue.add(order);
                        }
                    }
                    executionCounter++;
                } catch (ServerException e) {
                    e.printStackTrace();
                    System.out.println("Could not execute order " + order.getId());
                }
            }
            System.out.println("RecurringTransactionExecutor: Execution finished at "
                    + now(ZoneId.of("Europe/Vienna"))
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                    + "\nRecurringTransactionExecutor: " + executionCounter + "/"
                    + outstandingOrderCounter + " orders executed.");
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the date of the next order execution with respect to the order interval.
     *
     * @param order The recurrign transaction order to be updated.
     */
    private void setNewNextDate(RecurringTransactionOrder order) {
        LocalDate endDate = order.getEndDate();
        LocalDate currentNextDate = order.getNextDate();
        LocalDate newNextDate = order.getNextDate();
        Interval interval = order.getInterval();
        switch (interval) {
            case MONTHLY -> newNextDate = currentNextDate.plusMonths(1);
            case QUARTERLY -> newNextDate = currentNextDate.plusMonths(3);
            case SEMIANNUAL -> newNextDate = currentNextDate.plusMonths(6);
            case YEARLY -> newNextDate = currentNextDate.plusYears(1);
        }
        if (newNextDate.isBefore(endDate) || newNextDate.isEqual(endDate)) {
            order.setNextDate(newNextDate);
        } else {
            order.setNextDate(LocalDate.MAX);
        }
    }

    /**
     * Execute a recurring transaction order if it is outstanding.
     * I.e., if the next execution date of the order is today or lies in the past, create a respective transaction.
     *
     * @param order The order to be executed if outstanding.
     */
    public static void executeIfOrderIsOutstanding(RecurringTransactionOrder order) {
        if (isOutstanding(order)) {
            try (RecurringTransactionsExecutorService executorService = new RecurringTransactionsExecutorService()) {
                executorService.executeOutstandingRecurringTransactionOrders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if a recurring transaction order is outstanding, i.e., if it is time to be executed.
     *
     * @param order The order to be checked.
     * @return True if the next execution date of the order is today or lies in the past.
     */
    private static boolean isOutstanding(RecurringTransactionOrder order) {
        LocalDate nextDate = order.getNextDate();
        ZonedDateTime now = now(ZoneId.of("Europe/Vienna"));
        LocalDate today = now.toLocalDate();
        return nextDate.isBefore(today) || nextDate.isEqual(today);
    }

    /**
     * Shut down the scheduler.
     */
    @Override
    public void close() {
        executorService.shutdown();
    }
}
