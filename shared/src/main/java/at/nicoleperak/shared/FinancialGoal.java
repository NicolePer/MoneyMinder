package at.nicoleperak.shared;

import java.math.BigDecimal;

public class FinancialGoal {

    private Long id;
    private BigDecimal amount;

    public FinancialGoal(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public FinancialGoal() {
    }
}
