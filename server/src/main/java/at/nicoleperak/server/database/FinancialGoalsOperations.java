package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.FinancialGoal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static at.nicoleperak.server.database.DatabaseUtils.*;
import static java.sql.DriverManager.getConnection;

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
            stmt.setBigDecimal(1, goal.getAmount());
            stmt.setLong(2, financialAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create monthly financial goal", e);
        }
    }
}
