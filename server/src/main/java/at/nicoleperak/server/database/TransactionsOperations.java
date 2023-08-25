package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static at.nicoleperak.server.database.CategoriesOperations.*;
import static at.nicoleperak.server.database.DatabaseUtils.*;
import static java.sql.DriverManager.getConnection;

public class TransactionsOperations {

    protected static final String TRANSACTION_TABLE = "transactions";
    protected static final String TRANSACTION_ID = "id";
    protected static final String TRANSACTION_DESCRIPTION = "description";
    protected static final String TRANSACTION_AMOUNT = "amount";
    protected static final String TRANSACTION_DATE = "transaction_date";
    protected static final String TRANSACTION_PARTNER = "transaction_partner";
    protected static final String TRANSACTION_CATEGORY_ID = "category_id";
    protected static final String TRANSACTION_NOTE = "note";
    protected static final String TRANSACTION_ADDED_AUTOMATICALLY = "added_automatically";
    protected static final String TRANSACTION_FINANCIAL_ACCOUNT_ID = "financial_account_id";

    /**
     * Gets the data of all transactions of the given financial account.
     *
     * @param financialAccountId The ID of the financial account.
     * @return List of transactions.
     * @throws ServerException If the database could not be queried successfully.
     */
    public static List<Transaction> selectListOfTransactions(Long financialAccountId) throws ServerException {
        String select = "SELECT t." + TRANSACTION_ID + " AS transaction_id, "
                + TRANSACTION_DESCRIPTION + ", " + TRANSACTION_AMOUNT + ", "
                + TRANSACTION_DATE + ", " + TRANSACTION_PARTNER + ", "
                + TRANSACTION_NOTE + ", " + TRANSACTION_ADDED_AUTOMATICALLY
                + ", t." + TRANSACTION_CATEGORY_ID
                + ", c." + CATEGORY_TITLE + " AS category_title"
                + ", c." + CATEGORY_TYPE
                + " FROM " + TRANSACTION_TABLE + " t"
                + " INNER JOIN " + CATEGORY_TABLE + " c ON"
                + " c." + CATEGORY_ID + " = t." + TRANSACTION_CATEGORY_ID
                + " WHERE t." + TRANSACTION_FINANCIAL_ACCOUNT_ID + " = ?"
                + " ORDER BY " + TRANSACTION_DATE + " DESC"
                + ", t. " + TRANSACTION_ID + " DESC";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Transaction> transactionList = new ArrayList<>();
                while (rs.next()) {
                    transactionList.add(new Transaction(rs.getLong("transaction_id"),
                            rs.getString(TRANSACTION_DESCRIPTION),
                            rs.getBigDecimal(TRANSACTION_AMOUNT),
                            rs.getDate(TRANSACTION_DATE).toLocalDate(),
                            new Category(rs.getLong(TRANSACTION_CATEGORY_ID),
                                    rs.getString("category_title"),
                                    Category.CategoryType.values()[rs.getShort(CATEGORY_TYPE)]),
                            rs.getString(TRANSACTION_PARTNER),
                            rs.getString(TRANSACTION_NOTE),
                            rs.getBoolean(TRANSACTION_ADDED_AUTOMATICALLY)));
                }
                return transactionList;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select list of transactions", e);
        }
    }

    /**
     * Inserts a new transaction into the database.
     *
     * @param transaction        Data of the new transaction.
     * @param financialAccountId ID of the financial account the transaction should be added to.
     * @throws ServerException If the transaction could not be created.
     */
    public static void insertTransaction(Transaction transaction, Long financialAccountId) throws ServerException {
        String insert = "INSERT INTO " + TRANSACTION_TABLE
                + " (" + TRANSACTION_DESCRIPTION + "," + TRANSACTION_AMOUNT + ","
                + TRANSACTION_DATE + "," + TRANSACTION_PARTNER + ","
                + TRANSACTION_CATEGORY_ID + "," + TRANSACTION_NOTE + ","
                + TRANSACTION_ADDED_AUTOMATICALLY + "," + TRANSACTION_FINANCIAL_ACCOUNT_ID
                + ") VALUES(" +
                "?," + // 1 DESCRIPTION
                "?," + // 2 AMOUNT
                "?," + // 3 DATE
                "?," + // 4 TRANSACTION PARTNER
                "?," + // 5 CATEGORY_ID
                "?," + // 6 NOTE
                "?," + // 7 ADDED_AUTOMATICALLY
                "?)";  // 8 FINANCIAL_ACCOUNT_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, transaction.getDescription());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setDate(3, Date.valueOf(transaction.getDate()));
            stmt.setString(4, transaction.getTransactionPartner());
            stmt.setLong(5, transaction.getCategory().getId());
            stmt.setString(6, transaction.getNote());
            stmt.setBoolean(7, transaction.isAddedAutomatically());
            stmt.setLong(8, financialAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create transaction" + transaction, e);
        }
    }

    /**
     * Gets the ID of the financial account that the given transaction is associated with.
     *
     * @param transactionId The ID of the transaction.
     * @return The ID of the financial account.
     * @throws ServerException If the database could not be queried successfully.
     */
    public static Long selectFinancialAccountId(Long transactionId) throws ServerException {
        String select = "SELECT " + TRANSACTION_FINANCIAL_ACCOUNT_ID + " FROM " + TRANSACTION_TABLE
                + " WHERE " + TRANSACTION_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(TRANSACTION_FINANCIAL_ACCOUNT_ID);
                } else {
                    throw new ServerException(404, "Transaction with id " + transactionId + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account id of transaction", e);
        }
    }

    /**
     * Updates the data of an existing transaction in the database.
     *
     * @param transaction The new transaction data.
     * @param transactionId The ID of the transaction to be updated.
     * @throws ServerException If the transaction data could not be updated successfully.
     */
    public static void updateTransaction(Transaction transaction, Long transactionId) throws ServerException {
        String update = "UPDATE " + TRANSACTION_TABLE + " SET "
                + TRANSACTION_DESCRIPTION + " = ?, "            // 1 DESCRIPTION
                + TRANSACTION_AMOUNT + " = ?, "                 // 2 AMOUNT
                + TRANSACTION_DATE + " = ?, "                   // 3 DATE
                + TRANSACTION_PARTNER + " = ?,"                 // 4 TRANSACTION PARTNER
                + TRANSACTION_CATEGORY_ID + "= ?, "             // 5 CATEGORY ID
                + TRANSACTION_NOTE + "= ? "                     // 6 NOTE
                + " WHERE " + TRANSACTION_ID + " = ?";          // 7 TRANSACTION ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, transaction.getDescription());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setDate(3, Date.valueOf(transaction.getDate()));
            stmt.setString(4, transaction.getTransactionPartner());
            stmt.setLong(5, transaction.getCategory().getId());
            stmt.setString(6, transaction.getNote());
            stmt.setLong(7, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not update " + transaction, e);
        }
    }

    /**
     * Deletes a transaction from the database.
     *
     * @param transactionId The ID of the transaction to be deleted.
     * @throws ServerException If the deletion could not be executed successfully.
     */
    public static void deleteTransaction(Long transactionId) throws ServerException {
        String delete = "DELETE FROM " + TRANSACTION_TABLE +
                " WHERE " + TRANSACTION_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setLong(1, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not delete transaction", e);
        }
    }

}
