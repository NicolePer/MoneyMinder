package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static at.nicoleperak.server.database.CollaboratorsOperations.*;
import static at.nicoleperak.server.database.DatabaseUtils.*;
import static at.nicoleperak.server.database.FinancialAccountsOperations.*;
import static at.nicoleperak.server.database.FinancialGoalsOperations.FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID;
import static at.nicoleperak.server.database.FinancialGoalsOperations.FINANCIAL_GOAL_TABLE;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.RECURRING_TRANSACTION_ORDER_TABLE;
import static at.nicoleperak.server.database.TransactionsOperations.TRANSACTION_FINANCIAL_ACCOUNT_ID;
import static at.nicoleperak.server.database.TransactionsOperations.TRANSACTION_TABLE;
import static java.sql.DriverManager.getConnection;

public class UsersOperations {
    protected static final String USER_TABLE = "users";
    protected static final String USER_ID = "id";
    protected static final String USER_NAME = "username";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_PASSWORD_HASH = "password_hash";

    public static void insertUser(User user, String passwordHash) throws ServerException {

        String insert = "INSERT INTO " + USER_TABLE
                + " (" + USER_NAME + "," + USER_EMAIL + "," + USER_PASSWORD_HASH + ") VALUES(" +
                "?," + // 1 USER_NAME
                "?," + // 2 USER_EMAIL
                "?)";  // 3 USER_PASSWORD_HASH
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, passwordHash);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create user", e);
        }
    }

    public static boolean userExistsByEmail(String email) throws ServerException {
        String select = "SELECT " + USER_EMAIL + " FROM " + USER_TABLE
                + " WHERE " + USER_EMAIL + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select user", e);
        }
    }

    public static User selectUser(String email) throws ServerException {
        String select = "SELECT * FROM " + USER_TABLE
                + " WHERE " + USER_EMAIL + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getLong(USER_ID), rs.getString(USER_NAME), rs.getString(USER_EMAIL), rs.getString(USER_PASSWORD_HASH));
                } else {
                    throw new ServerException(404, "User with email " + email + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select user", e);
        }
    }

    public static void updateUser(User user, String passwordHash, Long userId) throws ServerException {
        String update = "UPDATE " + USER_TABLE + " SET "
                + USER_NAME + " = ?, "            // 1 USER_NAME
                + USER_EMAIL + " = ?, "           // 2 USER_EMAIL
                + USER_PASSWORD_HASH + " = ? "    // 3 USER_PASSWORD_HASH
                + " WHERE " + USER_ID + " = ?";   // 4 USER_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, passwordHash);
            stmt.setLong(4, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not update user", e);
        }
    }

    public static void updateUser(User user, Long userId) throws ServerException {
        String update = "UPDATE " + USER_TABLE + " SET "
                + USER_NAME + " = ?, "           // 1 USER_NAME
                + USER_EMAIL + " = ? "           // 2 USER_EMAIL
                + " WHERE " + USER_ID + " = ?";  // 3 USER_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setLong(3, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not update user", e);
        }
    }

    public static void deleteUser(Long userId) throws ServerException {
        String deleteUserFromCollaboratorsTable =
                "DELETE FROM " + COLLABORATOR_TABLE
                        + " WHERE " + COLLABORATOR_USER_ID + " = ?";                                //stmt1: 1 USER_ID
        String deleteAllRecurringTransactionOrdersFromFinancialAccountsWhereUserIsOwner =
                "DELETE FROM " + RECURRING_TRANSACTION_ORDER_TABLE + " r"
                        + " WHERE r." + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID
                        + " IN (SELECT f." + FINANCIAL_ACCOUNT_ID
                        + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                        + " WHERE f." + FINANCIAL_ACCOUNT_OWNER_ID + " = ?)";                         //stmt2: 1 USER_ID
        String deleteAllTransactionsFromFinancialAccountsWhereUserIsOwner =
                "DELETE FROM " + TRANSACTION_TABLE + " t"
                        + " WHERE t." + TRANSACTION_FINANCIAL_ACCOUNT_ID
                        + " IN (SELECT f." + FINANCIAL_ACCOUNT_ID
                        + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                        + " WHERE f." + FINANCIAL_ACCOUNT_OWNER_ID + " = ?)";                         //stmt3: 1 USER_ID
        String deleteAlCollaboratorsOfFinancialAccountsWhereUserIsOwner =
                "DELETE FROM " + COLLABORATOR_TABLE + " c"
                        + " WHERE c." + COLLABORATOR_FINANCIAL_ACCOUNT_ID
                        + " IN (SELECT f." + FINANCIAL_ACCOUNT_ID
                        + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                        + " WHERE f." + FINANCIAL_ACCOUNT_OWNER_ID + " = ?)";                         //stmt4: 1 USER_ID
        String deleteAllFinancialGoalsFromFinancialAccountsWhereUserIsOwner =
                "DELETE FROM " + FINANCIAL_GOAL_TABLE + " g"
                        + " WHERE g." + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID
                        + " IN (SELECT f." + FINANCIAL_ACCOUNT_ID
                        + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                        + " WHERE f." + FINANCIAL_ACCOUNT_OWNER_ID + " = ?)";                         //stmt5: 1 USER_ID
        String deleteAllFinancialAccountsWhereUserIsOwner =
                "DELETE FROM " + FINANCIAL_ACCOUNT_TABLE
                        + " WHERE " + FINANCIAL_ACCOUNT_OWNER_ID + " = ?";                         //stmt6: 1 USER_ID
        String deleteUser =
                "DELETE FROM " + USER_TABLE
                        + " WHERE " + USER_ID + " = ?";                                                     //stmt7: 1 USER_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt1 = conn.prepareStatement(deleteUserFromCollaboratorsTable);
             PreparedStatement stmt2 = conn.prepareStatement(deleteAllRecurringTransactionOrdersFromFinancialAccountsWhereUserIsOwner);
             PreparedStatement stmt3 = conn.prepareStatement(deleteAllTransactionsFromFinancialAccountsWhereUserIsOwner);
             PreparedStatement stmt4 = conn.prepareStatement(deleteAlCollaboratorsOfFinancialAccountsWhereUserIsOwner);
             PreparedStatement stmt5 = conn.prepareStatement(deleteAllFinancialGoalsFromFinancialAccountsWhereUserIsOwner);
             PreparedStatement stmt6 = conn.prepareStatement(deleteAllFinancialAccountsWhereUserIsOwner);
             PreparedStatement stmt7 = conn.prepareStatement(deleteUser)) {
            conn.setAutoCommit(false);
            stmt1.setLong(1, userId);
            stmt1.executeUpdate();
            stmt2.setLong(1, userId);
            stmt2.executeUpdate();
            stmt3.setLong(1, userId);
            stmt3.executeUpdate();
            stmt4.setLong(1, userId);
            stmt4.executeUpdate();
            stmt5.setLong(1, userId);
            stmt5.executeUpdate();
            stmt6.setLong(1, userId);
            stmt6.executeUpdate();
            stmt7.setLong(1, userId);
            stmt7.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new ServerException(500, "Could not delete user", e);
        }
    }
}
