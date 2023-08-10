package at.nicoleperak.server.databaseoperations;

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

import static at.nicoleperak.server.databaseoperations.DatabaseUtils.*;
import static at.nicoleperak.server.databaseoperations.TransactionsTableOperations.*;
import static at.nicoleperak.server.databaseoperations.UsersTableOperations.*;
import static java.sql.DriverManager.getConnection;

public class FinancialAccountsTableOperations {
    private static final String FINANCIAL_ACCOUNT_TABLE = "financial_accounts";
    private static final String FINANCIAL_ACCOUNT_ID = "id";
    private static final String FINANCIAL_ACCOUNT_TITLE = "title";
    private static final String FINANCIAL_ACCOUNT_DESCRIPTION = "description";
    private static final String FINANCIAL_ACCOUNT_BALANCE = "balance";
    private static final String FINANCIAL_ACCOUNT_OWNER_ID = "owner_user_id";

    public static void insertFinancialAccount(FinancialAccount financialAccount) throws ServerException {
        String insert = "INSERT INTO " + FINANCIAL_ACCOUNT_TABLE
                + " (" + FINANCIAL_ACCOUNT_TITLE + "," + FINANCIAL_ACCOUNT_DESCRIPTION
                + "," + FINANCIAL_ACCOUNT_OWNER_ID + ") VALUES(" +
                "?," + // 1 TITLE
                "?," + // 2 DESCRIPTION
                "?)";  // 3 OWNER ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, financialAccount.getTitle());
            stmt.setString(2, financialAccount.getDescription());
            stmt.setLong(3, financialAccount.getOwner().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create financial account", e);
        }
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
                    //TODO for later: Expand for Collaborators, Financial Goals, Recurring TransactionOrders
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
                + " WHERE f." + FINANCIAL_ACCOUNT_OWNER_ID + " = ?"
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
                    //TODO for later: Expand for Collaborators
                }
                return new FinancialAccountsList(financialAccounts);
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account overview", e);
        }
    }

    public static List<Long> selectOwnerAndCollaboratorsIdsOfFinancialAccount(Long financialAccountId) throws ServerException {
        String select = "SELECT " + FINANCIAL_ACCOUNT_OWNER_ID + " FROM " + FINANCIAL_ACCOUNT_TABLE
                + " WHERE " + FINANCIAL_ACCOUNT_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Long> userIds = new ArrayList<>();
                while (rs.next()) {
                    userIds.add(rs.getLong(FINANCIAL_ACCOUNT_OWNER_ID));
                    //TODO for later: Expand for Collaborators
                }
                return userIds;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select user ids of financial account", e);
        }
    }
}
