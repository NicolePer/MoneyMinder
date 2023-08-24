package at.nicoleperak.shared;

import java.math.BigDecimal;

@SuppressWarnings("unused")
public class FinancialGoal {

    private Long id;
    private BigDecimal goalAmount;
    private BigDecimal currentMonthsExpenses;

    public FinancialGoal(Long id, BigDecimal goalAmount, BigDecimal currentMonthsExpenses) {
        this.id = id;
        this.goalAmount = goalAmount;
        this.currentMonthsExpenses = currentMonthsExpenses;
    }

    public FinancialGoal() {
    }

    @Override
    public String toString() {
        return "FinancialGoal{" +
                "id=" + id +
                ", goalAmount=" + goalAmount +
                ", currentMonthsExpenses=" + currentMonthsExpenses +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount(BigDecimal goalAmount) {
        this.goalAmount = goalAmount;
    }

    public BigDecimal getCurrentMonthsExpenses() {
        return currentMonthsExpenses;
    }

    public void setCurrentMonthsExpenses(BigDecimal currentMonthsExpenses) {
        this.currentMonthsExpenses = currentMonthsExpenses;
    }
}
