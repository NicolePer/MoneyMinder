package at.nicoleperak.client;

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
}
