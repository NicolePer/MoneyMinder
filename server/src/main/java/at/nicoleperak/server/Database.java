package at.nicoleperak.server;

import at.nicoleperak.shared.*;
import at.nicoleperak.shared.Category.CategoryType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

public class Database {
    private static final String DB_LOCATION = "//localhost:5432/postgres";
    private static final String CONNECTION = "jdbc:postgresql:" + DB_LOCATION;
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "password1234";
    private static final String USER_TABLE = "users";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD_HASH = "password_hash";
    private static final String FINANCIAL_ACCOUNT_TABLE = "financial_accounts";
    private static final String FINANCIAL_ACCOUNT_ID = "id";
    private static final String FINANCIAL_ACCOUNT_TITLE = "title";
    private static final String FINANCIAL_ACCOUNT_DESCRIPTION = "description";
    private static final String FINANCIAL_ACCOUNT_BALANCE = "balance";
    private static final String FINANCIAL_ACCOUNT_OWNER_ID = "owner_user_id";
    private static final String TRANSACTION_TABLE = "transactions";
    private static final String TRANSACTION_ID = "id";
    private static final String TRANSACTION_DESCRIPTION = "description";
    private static final String TRANSACTION_AMOUNT = "amount";
    private static final String TRANSACTION_DATE = "transaction_date";
    private static final String TRANSACTION_PARTNER = "transaction_partner";
    private static final String TRANSACTION_CATEGORY_ID = "category_id";
    private static final String TRANSACTION_NOTE = "note";
    private static final String TRANSACTION_ADDED_AUTOMATICALLY = "added_automatically";
    private static final String TRANSACTION_FINANCIAL_ACCOUNT_ID = "financial_account_id";
    private static final String CATEGORY_TABLE = "categories";
    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_TITLE = "title";
    private static final String CATEGORY_TYPE = "category_type";


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

    private static List<Transaction> selectListOfTransactions(Long financialAccountId) throws ServerException {
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
                                    CategoryType.values()[rs.getShort(CATEGORY_TYPE)]),
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

    public static CategoryList selectCategoryList(CategoryType categoryType) throws ServerException {
        ArrayList<Category> categories = new ArrayList<>();
        String select = "SELECT * FROM " + CATEGORY_TABLE
                + " WHERE " + CATEGORY_TYPE + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setInt(1, categoryType.ordinal());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(
                            new Category(rs.getLong(CATEGORY_ID),
                                    rs.getString(CATEGORY_TITLE),
                                    CategoryType.values()[rs.getShort(CATEGORY_TYPE)]));
                }
                return new CategoryList(categories);
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select category list", e);
        }
    }

    public static CategoryList selectCategoryList() throws ServerException {
        ArrayList<Category> categories = new ArrayList<>();
        String select = "SELECT * FROM " + CATEGORY_TABLE;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(
                            new Category(rs.getLong(CATEGORY_ID),
                                    rs.getString(CATEGORY_TITLE),
                                    CategoryType.values()[rs.getShort(CATEGORY_TYPE)]));
                }
                return new CategoryList(categories);
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select category list", e);
        }
    }

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



