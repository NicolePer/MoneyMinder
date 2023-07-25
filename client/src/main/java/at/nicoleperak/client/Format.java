package at.nicoleperak.client;

import at.nicoleperak.client.controllers.CreateTransactionDialogController;
import at.nicoleperak.shared.Category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Format {

    public static String formatBalance(BigDecimal balance) {
        balance = balance.setScale(2, RoundingMode.DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingSize(3);
        return df.format(balance) + " €";
    }

    public static String formatAmount(String amount, Category category) {
        StringBuilder sb = new StringBuilder();
        if (category.getType().equals(Category.CategoryType.Expense)) {
            sb.append("-");
        }
        sb.append(amount);
        return sb.toString();
    }

    public static String convertIntoParsableDecimal(String amount) {
        return amount.replace(",", ".");
    }

}
