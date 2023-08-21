package at.nicoleperak.shared;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@SuppressWarnings("unused")
public class Transaction implements Serializable {

    @CsvIgnore
    private Long id;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "amount")
    private BigDecimal amount;

    @CsvBindByName(column = "date")
    private LocalDate date;

    @CsvBindByName(column = "category")
    private Category category;

    @CsvBindByName(column = "transaction partner")
    private String transactionPartner;

    @CsvBindByName(column = "note")
    private String note;

    @CsvBindByName(column = "added automatically")
    private boolean addedAutomatically;

    public Transaction() {
    }

    public Transaction(Long id, String description, BigDecimal amount, LocalDate date, Category category, String transactionPartner, String note, boolean addedAutomatically) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.transactionPartner = transactionPartner;
        this.note = note;
        this.addedAutomatically = addedAutomatically;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                ", category=" + category +
                ", transactionPartner='" + transactionPartner + '\'' +
                ", note='" + note + '\'' +
                ", addedAutomatically=" + addedAutomatically +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public Category getCategory() {
        return category;
    }

    public String getTransactionPartner() {
        return transactionPartner;
    }

    public String getNote() {
        return note;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTransactionPartner(String transactionPartner) {
        this.transactionPartner = transactionPartner;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAddedAutomatically(boolean addedAutomatically) {
        this.addedAutomatically = addedAutomatically;
    }

    public boolean isAddedAutomatically() {
        return addedAutomatically;
    }
}
