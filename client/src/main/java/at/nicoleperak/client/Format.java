package at.nicoleperak.client;

import at.nicoleperak.shared.Category;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static at.nicoleperak.shared.Category.CategoryType.EXPENSE;

public class Format {

    /**
     * Formats a BigDecimal balance value to a standardized currency display format.
     *
     * @param balance The balance amount to be formatted.
     * @return A String of the formatted balance with the currency symbol.
     */
    public static String formatBalance(BigDecimal balance) {
        balance = balance.setScale(2, RoundingMode.DOWN);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        df.setGroupingSize(3);
        return df.format(balance) + " €";
    }

    /**
     * Formats an amount string based on its category type, adding a negative sign for expenses.
     *
     * @param amount   The amount string to be formatted.
     * @param category The Category object indicating the type of transaction.
     * @return A formatted amount string with an optional negative sign.
     */
    public static String formatAmount(String amount, Category category) {
        StringBuilder sb = new StringBuilder();
        if (category.getType().equals(EXPENSE)) {
            sb.append("-");
        }
        sb.append(amount);
        return sb.toString();
    }

    /**
     * Converts a comma-separated decimal representation to a parsable format.
     *
     * @param amount The decimal amount string to be converted.
     * @return The converted amount string with commas replaced by periods.
     */
    public static String convertIntoParsableDecimal(String amount) {
        return amount.replace(",", ".");
    }

}
