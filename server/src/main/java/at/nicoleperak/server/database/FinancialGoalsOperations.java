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

    /**
     * Inserts a new financial goal into the database.
     *
     * @param goal               Data of the new goal.
     * @param financialAccountId D of the financial account the goal should be added to.
     * @throws ServerException If the goal could not be created.
     */
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

    /**
     * Gets the data of the financial goal of the given financial account.
     *
     * @param financialAccountId The ID of the financial account.
     * @return The financial goal.
     * @throws ServerException If the database could not be queried successfully.
     */
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


    /**
     * Gets the ID of the financial account that the given financial goal is associated with.
     *
     * @param goalId The ID of the goal.
     * @return The ID of the financial account.
     * @throws ServerException If the database could not be queried successfully.
     */
    public static Long selectFinancialAccountId(Long goalId) throws ServerException {
        String select = "SELECT " + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID
                + " FROM " + FINANCIAL_GOAL_TABLE
                + " WHERE " + FINANCIAL_GOAL_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, goalId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(TRANSACTION_FINANCIAL_ACCOUNT_ID);
                } else {
                    throw new ServerException(404, "Financial goal with id " + goalId + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account id of financial goal", e);
        }
    }


    /**
     * Updates the data of an existing financial goal in the database.
     *
     * @param goal The new goal data.
     * @param goalId The ID of the goal to be updated.
     * @throws ServerException If the goal data could not be updated successfully.
     */
    public static void updateFinancialGoal(FinancialGoal goal, Long goalId) throws ServerException {
        String update = "UPDATE " + FINANCIAL_GOAL_TABLE + " SET "
                + FINANCIAL_GOAL_AMOUNT + " = ? "            // 1 AMOUNT
                + " WHERE " + FINANCIAL_GOAL_ID + " = ?";     // 2 FINANCIAL_GOAL_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setBigDecimal(1, goal.getGoalAmount());
            stmt.setLong(2, goalId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not update financial goal", e);
        }
    }

    /**
     * Deletes a goal from the database.
     *
     * @param goalId The ID of the goal to be deleted.
     * @throws ServerException If the deletion could not be executed successfully.
     */
    public static void deleteFinancialGoal(Long goalId) throws ServerException {
        String delete = "DELETE FROM " + FINANCIAL_GOAL_TABLE +
                " WHERE " + FINANCIAL_GOAL_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setLong(1, goalId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not delete financial goal", e);
        }
    }

}
