package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.FinancialAccountsList;
import at.nicoleperak.shared.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static at.nicoleperak.server.database.CollaboratorsOperations.*;
import static at.nicoleperak.server.database.DatabaseUtils.*;
import static at.nicoleperak.server.database.FinancialGoalsOperations.*;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.*;
import static at.nicoleperak.server.database.TransactionsOperations.*;
import static at.nicoleperak.server.database.UsersOperations.*;
import static java.sql.DriverManager.getConnection;

public class FinancialAccountsOperations {
    protected static final String FINANCIAL_ACCOUNT_TABLE = "financial_accounts";
    protected static final String FINANCIAL_ACCOUNT_ID = "id";
    protected static final String FINANCIAL_ACCOUNT_TITLE = "title";
    protected static final String FINANCIAL_ACCOUNT_DESCRIPTION = "description";
    protected static final String FINANCIAL_ACCOUNT_BALANCE = "balance";
    protected static final String FINANCIAL_ACCOUNT_OWNER_ID = "owner_user_id";

    public static Long insertFinancialAccount(FinancialAccount financialAccount) throws ServerException {
        String insert = "INSERT INTO " + FINANCIAL_ACCOUNT_TABLE
                + " (" + FINANCIAL_ACCOUNT_TITLE + "," + FINANCIAL_ACCOUNT_DESCRIPTION
                + "," + FINANCIAL_ACCOUNT_OWNER_ID + ") VALUES(" +
                "?," + // 1 TITLE
                "?," + // 2 DESCRIPTION
                "?)";  // 3 OWNER ID
        String[] returnId = {FINANCIAL_ACCOUNT_ID};
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert, returnId)) {
            stmt.setString(1, financialAccount.getTitle());
            stmt.setString(2, financialAccount.getDescription());
            stmt.setLong(3, financialAccount.getOwner().getId());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create financial account", e);
        }
        return -1L;
    }

    public static FinancialAccount selectFullFinancialAccount(Long financialAccountId) throws ServerException {
        String select = "SELECT f." + FINANCIAL_ACCOUNT_TITLE
                + ", f." + FINANCIAL_ACCOUNT_DESCRIPTION
                + ", f." + FINANCIAL_ACCOUNT_OWNER_ID
                + ", COALESCE(SUM(t." + TRANSACTION_AMOUNT + "),0) AS " + FINANCIAL_ACCOUNT_BALANCE
                + ", u." + USER_NAME + " AS owner_username"
                + ", u." + USER_EMAIL + " AS owner_email"
                + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                + " INNER JOIN " + USER_TABLE + " u ON"
                + " u." + USER_ID + " = f." + FINANCIAL_ACCOUNT_OWNER_ID
                + " LEFT JOIN " + TRANSACTION_TABLE + " t ON"
                + " t." + TRANSACTION_FINANCIAL_ACCOUNT_ID + " = f." + FINANCIAL_ACCOUNT_ID
                + " WHERE f." + FINANCIAL_ACCOUNT_ID + " = ?"
                + " GROUP BY f." + FINANCIAL_ACCOUNT_ID + ", u." + USER_NAME + ", u." + USER_EMAIL;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                FinancialAccount financialAccount = new FinancialAccount();
                if (rs.next()) {
                    financialAccount.setId(financialAccountId);
                    financialAccount.setTitle(rs.getString(FINANCIAL_ACCOUNT_TITLE));
                    financialAccount.setDescription(rs.getString(FINANCIAL_ACCOUNT_DESCRIPTION));
                    financialAccount.setBalance(rs.getBigDecimal(FINANCIAL_ACCOUNT_BALANCE));
                    User owner = new User(rs.getLong(FINANCIAL_ACCOUNT_OWNER_ID),
                            rs.getString("owner_username"),
                            rs.getString("owner_email"),
                            null);
                    financialAccount.setOwner(owner);
                    financialAccount.setTransactions(selectListOfTransactions(financialAccountId));
                    financialAccount.setCollaborators(selectListOfCollaborators(financialAccountId));
                    financialAccount.setRecurringTransactionOrders(selectListOfRecurringTransactionOrders(financialAccountId));
                    financialAccount.setFinancialGoal(selectFinancialGoal(financialAccountId));
                    return financialAccount;
                } else {
                    throw new ServerException(404, "Financial account with id " + financialAccountId + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account details", e);
        }
    }

    public static FinancialAccountsList selectListOfFinancialAccountOverviews(Long id) throws ServerException {
        ArrayList<FinancialAccount> financialAccounts = new ArrayList<>();
        String select = "SELECT f." + FINANCIAL_ACCOUNT_ID
                + ", f." + FINANCIAL_ACCOUNT_TITLE
                + ", f." + FINANCIAL_ACCOUNT_DESCRIPTION
                + ", COALESCE(SUM(t." + TRANSACTION_AMOUNT + "),0) AS " + FINANCIAL_ACCOUNT_BALANCE
                + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                + " LEFT JOIN " + TRANSACTION_TABLE + " t"
                + " ON t." + TRANSACTION_FINANCIAL_ACCOUNT_ID + " = f." + FINANCIAL_ACCOUNT_ID
                + " LEFT JOIN " + COLLABORATOR_TABLE + " c"
                + " ON c." + COLLABORATOR_FINANCIAL_ACCOUNT_ID + " = f." + FINANCIAL_ACCOUNT_ID
                + " WHERE c." + COLLABORATOR_USER_ID + " = ?"
                + " GROUP BY f." + FINANCIAL_ACCOUNT_ID;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    financialAccounts.add(new FinancialAccount(rs.getLong(FINANCIAL_ACCOUNT_ID),
                            rs.getString(FINANCIAL_ACCOUNT_TITLE),
                            rs.getString(FINANCIAL_ACCOUNT_DESCRIPTION),
                            rs.getBigDecimal(FINANCIAL_ACCOUNT_BALANCE),
                            null,
                            null,
                            null,
                            null,
                            null));
                }
                return new FinancialAccountsList(financialAccounts);
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account overview", e);
        }
    }

    public static Long selectOwnerIdOfFinancialAccount(Long financialAccountId) throws ServerException {
        String select = "SELECT " + FINANCIAL_ACCOUNT_OWNER_ID + " FROM " + FINANCIAL_ACCOUNT_TABLE
                + " WHERE " + FINANCIAL_ACCOUNT_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(FINANCIAL_ACCOUNT_OWNER_ID);
                } else {
                    throw new ServerException(404, "Financial account with id " + financialAccountId + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select owner id of financial account", e);
        }
    }

    public static void updateFinancialAccount(FinancialAccount financialAccount, Long financialAccountId) throws ServerException {
        String update = "UPDATE " + FINANCIAL_ACCOUNT_TABLE + " SET "
                + FINANCIAL_ACCOUNT_TITLE + " = ?, "            // 1 TITLE
                + FINANCIAL_ACCOUNT_DESCRIPTION + " = ?, "      // 2 DESCRIPTION
                + FINANCIAL_ACCOUNT_OWNER_ID + " = ? "          // 3 OWNER_ID
                + " WHERE " + FINANCIAL_ACCOUNT_ID + " = ?";    // 4 FINANCIAL_ACCOUNT_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, financialAccount.getTitle());
            stmt.setString(2, financialAccount.getDescription());
            stmt.setLong(3, financialAccount.getOwner().getId());
            stmt.setLong(4, financialAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not update financial account", e);
        }
    }

    public static void deleteFinancialAccount(Long financialAccountId) throws ServerException {
        String deleteAllAssociatedRecurringTransactionOrders = "DELETE FROM " + RECURRING_TRANSACTION_ORDER_TABLE
                + " WHERE " + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID + " = ?";    // stmt1: 1 FINANCIAL_ACCOUNT_ID
        String deleteAllAssociatedTransactions = "DELETE FROM " + TRANSACTION_TABLE
                + " WHERE " + TRANSACTION_FINANCIAL_ACCOUNT_ID + " = ?";                    //stmt2: 1 FINANCIAL_ACCOUNT_ID
        String deleteAllAssociatedCollaborators = "DELETE FROM " + COLLABORATOR_TABLE
                + " WHERE " + COLLABORATOR_FINANCIAL_ACCOUNT_ID + " = ?";                   //stmt3: 1 FINANCIAL_ACCOUNT_ID
        String deleteAssociatedFinancialGoal = "DELETE FROM " + FINANCIAL_GOAL_TABLE
                + " WHERE " + FINANCIAL_GOAL_FINANCIAL_ACCOUNT_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);         //stmt4: 1 FINANCIAL_ACCOUNT_ID
             PreparedStatement stmt1 = conn.prepareStatement(deleteAllAssociatedRecurringTransactionOrders);
             PreparedStatement stmt2 = conn.prepareStatement(deleteAllAssociatedTransactions);
             PreparedStatement stmt3 = conn.prepareStatement(deleteAllAssociatedCollaborators);
             PreparedStatement stmt4 = conn.prepareStatement(deleteAssociatedFinancialGoal)) {
            conn.setAutoCommit(false);
            stmt1.setLong(1, financialAccountId);
            stmt1.executeUpdate();
            stmt2.setLong(1, financialAccountId);
            stmt2.executeUpdate();
            stmt3.setLong(1, financialAccountId);
            stmt3.executeUpdate();
            stmt4.setLong(1, financialAccountId);
            stmt4.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            throw new ServerException(500, "Could not delete financial account with id " + financialAccountId, e);
        }
    }

    public static List<Long> selectListOfSharedFinancialAccountIdsWhereUserIsOwner(Long ownerId) throws ServerException {
        List<Long> financialAccountIds = new ArrayList<>();
        String select = "SELECT DISTINCT f." + FINANCIAL_ACCOUNT_ID + " FROM " + FINANCIAL_ACCOUNT_TABLE + " f"
                + " INNER JOIN " + COLLABORATOR_TABLE + " c"
                + " ON c." + COLLABORATOR_FINANCIAL_ACCOUNT_ID + " = f." + FINANCIAL_ACCOUNT_ID
                + " AND c." + COLLABORATOR_USER_ID + " != ?"                // 1 OWNER_USER_ID
                + " WHERE f." + FINANCIAL_ACCOUNT_OWNER_ID + " = ?";        // 2 OWNER_USER_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, ownerId);
            stmt.setLong(2, ownerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    financialAccountIds.add(rs.getLong(FINANCIAL_ACCOUNT_ID));
                }
                return financialAccountIds;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account ids", e);
        }
    }
}
