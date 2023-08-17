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

    private void setNewNextDate(RecurringTransactionOrder order) {
        LocalDate endDate = order.getEndDate();
        LocalDate currentNextDate = order.getNextDate();
        LocalDate newNextDate = order.getNextDate();
        Interval interval = order.getInterval();
        switch (interval) {
            case monthly -> newNextDate = currentNextDate.plusMonths(1);
            case quarterly -> newNextDate = currentNextDate.plusMonths(3);
            case semiannual -> newNextDate = currentNextDate.plusMonths(6);
            case yearly -> newNextDate = currentNextDate.plusYears(1);
        }
        if (newNextDate.isBefore(endDate) || newNextDate.isEqual(endDate)) {
            order.setNextDate(newNextDate);
        } else {
            order.setNextDate(LocalDate.MAX);
        }
    }

    public static void executeIfOrderIsOutstanding(RecurringTransactionOrder order) {
        if (isOutstanding(order)) {
            try (RecurringTransactionsExecutorService executorService = new RecurringTransactionsExecutorService()) {
                executorService.executeOutstandingRecurringTransactionOrders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isOutstanding(RecurringTransactionOrder order) {
        LocalDate nextDate = order.getNextDate();
        ZonedDateTime now = now(ZoneId.of("Europe/Vienna"));
        LocalDate today = now.toLocalDate();
        return nextDate.isBefore(today) || nextDate.isEqual(today);
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
