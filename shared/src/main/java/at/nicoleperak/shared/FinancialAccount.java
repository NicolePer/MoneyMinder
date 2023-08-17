package at.nicoleperak.shared;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("unused")
public class FinancialAccount {

    private Long id;
    private String title;
    private String description;
    private BigDecimal balance;
    private List<Transaction> transactions;
    private User owner;
    private List<User> collaborators;
    private List<RecurringTransactionOrder> recurringTransactionOrders;
    private FinancialGoal financialGoal;

    public FinancialAccount() {
    }

    public FinancialAccount(Long id, String title, String description, BigDecimal balance, List<Transaction> transactions, User owner, List<User> collaborators, List<RecurringTransactionOrder> recurringTransactionOrders, FinancialGoal financialGoal) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.balance = balance;
        this.transactions = transactions;
        this.owner = owner;
        this.collaborators = collaborators;
        this.recurringTransactionOrders = recurringTransactionOrders;
        this.financialGoal = financialGoal;
    }

    public FinancialAccount(String title, String description) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.balance = null;
        this.transactions = null;
        this.owner = null;
        this.collaborators = null;
        this.recurringTransactionOrders = null;
        this.financialGoal = null;
    }

    public FinancialAccount(String title, String description, User owner) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.balance = null;
        this.transactions = null;
        this.owner = owner;
        this.collaborators = null;
        this.recurringTransactionOrders = null;
        this.financialGoal = null;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<User> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<User> collaborators) {
        this.collaborators = collaborators;
    }

    public List<RecurringTransactionOrder> getRecurringTransactionOrders() {
        return recurringTransactionOrders;
    }

    public void setRecurringTransactionOrders(List<RecurringTransactionOrder> recurringTransactionOrders) {
        this.recurringTransactionOrders = recurringTransactionOrders;
    }

    public FinancialGoal getFinancialGoal() {
        return financialGoal;
    }

    public void setFinancialGoal(FinancialGoal financialGoal) {
        this.financialGoal = financialGoal;
    }
}
