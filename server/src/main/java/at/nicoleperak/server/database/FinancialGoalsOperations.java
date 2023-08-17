package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.FinancialGoal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static at.nicoleperak.server.database.DatabaseUtils.*;
import static at.nicoleperak.server.database.TransactionsOperations.*;
import static java.sql.DriverManager.getConnection;
import static java.time.LocalDate.now;

public class FinancialGoalsOperations {

    protected static final String FINANCIAL_GOAL_TABLE = "financial_goals";
    protected static final String FINANCIAL_GOAL_ID = "id";
    protected static final String FINANCIAL_GOAL_AMOUNT = "amount";
    protected static final String FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID = "financial_account_id";

    public static void insertFinancialGoal(FinancialGoal goal, Long financialAccountId) throws ServerException {
        String insert = "INSERT INTO " + FINANCIAL_GOAL_TABLE
                + " (" + FINANCIAL_GOAL_AMOUNT + "," + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID + ") VALUES(" +
                "?," + // 1 AMOUNT
                "?)";  // 2 FINANCIAL_ACCOUNT_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setBigDecimal(1, goal.getGoalAmount());
            stmt.setLong(2, financialAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create monthly financial goal", e);
        }
    }

    public static FinancialGoal selectFinancialGoal(Long financialAccountId) throws ServerException {
        String select = "SELECT g." + FINANCIAL_GOAL_ID
                + ", g." + FINANCIAL_GOAL_AMOUNT
                + ", g." + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID
                + ", COALESCE(SUM(t." + TRANSACTION_AMOUNT + "),0) AS current_expenses"
                + " FROM " + FINANCIAL_GOAL_TABLE + " g"
                + " LEFT JOIN " + TRANSACTION_TABLE + " t"
                + " ON g." + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID
                + " = t." + TRANSACTION_FINANCIAL_ACCOUNT_ID
                + " AND EXTRACT(MONTH FROM t." + TRANSACTION_DATE + ") = ?"     // 1 CURRENT MONTH
                + " AND EXTRACT(YEAR FROM t." + TRANSACTION_DATE + ") = ?"      // 2 CURRENT YEAR
                + " AND t." + TRANSACTION_AMOUNT + " < 0"
                + " WHERE g." + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID + " = ?"    // 3 FINANCIAL ACCOUNT ID
                + " GROUP BY g." + FINANCIAL_GOAL_ID + ", g." + FINANCIAL_GOAL_AMOUNT + ", g." + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setInt(1, now().getMonthValue());
            stmt.setInt(2, now().getYear());
            stmt.setLong(3, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new FinancialGoal(
                            rs.getLong(FINANCIAL_GOAL_ID),
                            rs.getBigDecimal(FINANCIAL_GOAL_AMOUNT),
                            rs.getBigDecimal("current_expenses")
                    );
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial goal", e);
        }
    }
}
