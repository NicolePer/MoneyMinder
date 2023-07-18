package at.nicoleperak.shared;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RecurringTransactionOrder {

    private Long id;
    private String description;
    private BigDecimal amount;
    private Category category;
    private String transactionPartner;
    private String note;
    private LocalDate lastDate;
    private LocalDate nextDate;
    private LocalDate endDate;
    private Interval interval;

    public RecurringTransactionOrder() {
    }

    public RecurringTransactionOrder(Long id, String description, BigDecimal amount, Category category, String transactionPartner, String note, LocalDate lastDate, LocalDate nextDate, LocalDate endDate, Interval interval) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.transactionPartner = transactionPartner;
        this.note = note;
        this.lastDate = lastDate;
        this.nextDate = nextDate;
        this.endDate = endDate;
        this.interval = interval;
    }

    public enum Interval { monthly, quarterly, semiannual, yearly}
}
