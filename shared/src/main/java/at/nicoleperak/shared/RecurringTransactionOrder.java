package at.nicoleperak.shared;

import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("unused")
public class RecurringTransactionOrder {

    private Long id;
    private String description;
    private BigDecimal amount;
    private Category category;
    private String transactionPartner;
    private String note;
    private LocalDate nextDate;
    private LocalDate endDate;
    private Interval interval;

    public RecurringTransactionOrder() {
    }


    public RecurringTransactionOrder(Long id, String description, BigDecimal amount, Category category, String transactionPartner, String note, LocalDate nextDate, LocalDate endDate, Interval interval) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.transactionPartner = transactionPartner;
        this.note = note;
        this.nextDate = nextDate;
        this.endDate = endDate;
        this.interval = interval;
    }

    @Override
    public String toString() {
        return "RecurringTransactionOrder{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category=" + category +
                ", transactionPartner='" + transactionPartner + '\'' +
                ", note='" + note + '\'' +
                ", nextDate=" + nextDate +
                ", endDate=" + endDate +
                ", interval=" + interval +
                '}';
    }

    public enum Interval {
        MONTHLY("monthly"),
        QUARTERLY("quarterly"),
        SEMIANNUAL("semiannual"),
        YEARLY("yearly");

        private final String label;

        Interval(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getTransactionPartner() {
        return transactionPartner;
    }

    public void setTransactionPartner(String transactionPartner) {
        this.transactionPartner = transactionPartner;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getNextDate() {
        return nextDate;
    }

    public void setNextDate(LocalDate nextDate) {
        this.nextDate = nextDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }


}

