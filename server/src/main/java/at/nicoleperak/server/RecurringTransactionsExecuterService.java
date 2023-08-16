package at.nicoleperak.server;

import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.Transaction;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static at.nicoleperak.server.TransactionFactory.buildFromRecurringTransactionOrder;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.*;
import static at.nicoleperak.shared.RecurringTransactionOrder.Interval;

@SuppressWarnings("CallToPrintStackTrace")
public class RecurringTransactionsExecuterService implements AutoCloseable {
    private final ScheduledExecutorService executorService;

    public RecurringTransactionsExecuterService() {
        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    public void scheduleService() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Vienna"));
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        ZonedDateTime startOfTomorrow = ZonedDateTime.of(tomorrow, LocalTime.MIN, ZoneId.of("Europe/Vienna"));
        Duration d = Duration.between(now, startOfTomorrow);
        long minutesUnitStartOfTomorrow = d.toMinutes();
        executorService.scheduleAtFixedRate(() -> {
                    try {
                        executeOutstandingRecurringTransactionOrders();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, minutesUnitStartOfTomorrow,
                TimeUnit.DAYS.toMinutes(1),
                TimeUnit.MINUTES
        );
        System.out.println("RecurringTransactionExecuter: recurring transaction execution scheduled");
    }


    public void executeOutstandingRecurringTransactionOrders() {
        try {
            int executionCounter = 0;
            List<RecurringTransactionOrder> outstandingOrders = selectListOfOutstandingOrders();
            System.out.println("RecurringTransactionExecuter: " + outstandingOrders.size() + " outstanding recurring transaction orders retrieved.");
            for (RecurringTransactionOrder order : outstandingOrders) {
                try {
                    Transaction transaction = buildFromRecurringTransactionOrder(order);
                    Long financialAccountId = selectFinancialAccountId(order.getId());
                    setNewNextDate(order);
                    if (order.getNextDate().equals(LocalDate.MAX)) {
                        insertTransactionAndDeleteOrder(order, transaction, financialAccountId);
                    } else {
                        insertTransactionAndUpdateOrder(order, transaction, financialAccountId);
                    }
                    executionCounter++;
                } catch (ServerException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("RecurringTransactionExecuter: Execution finished at "
                    + ZonedDateTime.now(ZoneId.of("Europe/Vienna"))
                    .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))
                    + "\nRecurringTransactionExecuter: " + executionCounter + " orders executed.");
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

    @Override
    public void close() throws Exception {
        executorService.shutdown();
    }

    public static void checkForOutstandingExecution(RecurringTransactionOrder order) {
        LocalDate nextDate = order.getNextDate();
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Vienna"));
        LocalDate today = now.toLocalDate();
        if (nextDate.isBefore(today) || nextDate.isEqual(today)) {
            try (RecurringTransactionsExecuterService executerService = new RecurringTransactionsExecuterService()) {
                executerService.executeOutstandingRecurringTransactionOrders();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
