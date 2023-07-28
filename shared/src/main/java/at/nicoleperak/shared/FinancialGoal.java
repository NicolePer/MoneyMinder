package at.nicoleperak.shared;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class FinancialGoal {

    private Long id;
    private BigDecimal amount;

    public FinancialGoal(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public FinancialGoal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
