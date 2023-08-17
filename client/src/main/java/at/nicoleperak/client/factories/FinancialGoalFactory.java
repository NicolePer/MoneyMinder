package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.dialogs.SetMonthlyGoalDialogController;
import at.nicoleperak.shared.FinancialGoal;

import java.math.BigDecimal;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;

public class FinancialGoalFactory {

    public static FinancialGoal buildFinancialGoal(SetMonthlyGoalDialogController controller) {
        String amountString = convertIntoParsableDecimal(controller.getGoalTextField().getText());
        BigDecimal amount = new BigDecimal(amountString);
        return new FinancialGoal(null, amount, null);
    }
}
