package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.Transaction;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static at.nicoleperak.server.database.CategoryOperations.*;
import static at.nicoleperak.server.database.DatabaseUtils.*;
import static at.nicoleperak.server.database.TransactionsOperations.*;
import static at.nicoleperak.shared.RecurringTransactionOrder.Interval;
import static java.sql.DriverManager.getConnection;

@SuppressWarnings("CallToPrintStackTrace")
public class RecurringTransactionOrdersOperations {

    protected static final String RECURRING_TRANSACTION_ORDER_TABLE = "recurring_transaction_orders";
    protected static final String RECURRING_TRANSACTION_ORDER_ID = "id";
    protected static final String RECURRING_TRANSACTION_ORDER_DESCRIPTION = "description";
    protected static final String RECURRING_TRANSACTION_ORDER_AMOUNT = "amount";
    protected static final String RECURRING_TRANSACTION_ORDER_PARTNER = "transaction_partner";
    protected static final String RECURRING_TRANSACTION_ORDER_CATEGORY_ID = "category_id";
    protected static final String RECURRING_TRANSACTION_ORDER_NOTE = "note";
    protected static final String RECURRING_TRANSACTION_ORDER_NEXT_DATE = "next_date";
    protected static final String RECURRING_TRANSACTION_ORDER_END_DATE = "end_date";
    protected static final String RECURRING_TRANSACTION_ORDER_INTERVAL = "order_interval";
    protected static final String RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID = "financial_account_id";

    public static void insertOrder(RecurringTransactionOrder order, Long financialAccountId) throws ServerException {
        String insert = "INSERT INTO " + RECURRING_TRANSACTION_ORDER_TABLE
                + " (" + RECURRING_TRANSACTION_ORDER_DESCRIPTION + "," + RECURRING_TRANSACTION_ORDER_AMOUNT + ","
                + RECURRING_TRANSACTION_ORDER_PARTNER + "," + RECURRING_TRANSACTION_ORDER_CATEGORY_ID + ","
                + RECURRING_TRANSACTION_ORDER_NOTE + "," + RECURRING_TRANSACTION_ORDER_NEXT_DATE + ","
                + RECURRING_TRANSACTION_ORDER_END_DATE + "," + RECURRING_TRANSACTION_ORDER_INTERVAL + ","
                + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID + ") VALUES(" +
                "?," + // 1 DESCRIPTION
                "?," + // 2 AMOUNT
                "?," + // 3 TRANSACTION PARTNER
                "?," + // 4 CATEGORY_ID
                "?," + // 5 NOTE
                "?," + // 6 NEXT_DATE
                "?," + // 7 END DATE
                "?," + // 8 INTERVAL
                "?)";  // 9 FINANCIAL_ACCOUNT_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setString(1, order.getDescription());
            stmt.setBigDecimal(2, order.getAmount());
            stmt.setString(3, order.getTransactionPartner());
            stmt.setLong(4, order.getCategory().getId());
            stmt.setString(5, order.getNote());
            stmt.setDate(6, Date.valueOf(order.getNextDate()));
            if (order.getEndDate() == null) {
                stmt.setDate(7, null);
            } else {
                stmt.setDate(7, Date.valueOf(order.getEndDate()));
            }
            stmt.setInt(8, order.getInterval().ordinal());
            stmt.setLong(9, financialAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create recurring transaction order" + order, e);
        }
    }

    public static List<RecurringTransactionOrder> selectListOfRecurringTransactionOrders(Long financialAccountId) throws ServerException {
        String select = "SELECT r." + RECURRING_TRANSACTION_ORDER_ID + ","
                + RECURRING_TRANSACTION_ORDER_DESCRIPTION + ","
                + RECURRING_TRANSACTION_ORDER_AMOUNT + ","
                + RECURRING_TRANSACTION_ORDER_NOTE + ","
                + RECURRING_TRANSACTION_ORDER_PARTNER + ","
                + RECURRING_TRANSACTION_ORDER_NEXT_DATE + ","
                + RECURRING_TRANSACTION_ORDER_END_DATE + ","
                + RECURRING_TRANSACTION_ORDER_INTERVAL + ","
                + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID + ","
                + RECURRING_TRANSACTION_ORDER_CATEGORY_ID
                + ", c." + CATEGORY_TITLE + " AS category_title"
                + ", c." + CATEGORY_TYPE
                + " FROM " + RECURRING_TRANSACTION_ORDER_TABLE + " r"
                + " INNER JOIN " + CATEGORY_TABLE + " c ON"
                + " c." + CATEGORY_ID + " = r." + RECURRING_TRANSACTION_ORDER_CATEGORY_ID
                + " WHERE r." + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID + " = ?"
                + " ORDER BY " + RECURRING_TRANSACTION_ORDER_INTERVAL + ", " + RECURRING_TRANSACTION_ORDER_DESCRIPTION;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<RecurringTransactionOrder> orders = new ArrayList<>();
                while (rs.next()) {
                    LocalDate endDate;
                    if (rs.getDate(RECURRING_TRANSACTION_ORDER_END_DATE) != null) {
                        endDate = rs.getDate(RECURRING_TRANSACTION_ORDER_END_DATE).toLocalDate();
                    } else {
                        endDate = LocalDate.MAX;
                    }
                    orders.add(new RecurringTransactionOrder(
                            rs.getLong(RECURRING_TRANSACTION_ORDER_ID),
                            rs.getString(RECURRING_TRANSACTION_ORDER_DESCRIPTION),
                            rs.getBigDecimal(RECURRING_TRANSACTION_ORDER_AMOUNT),
                            new Category(rs.getLong(RECURRING_TRANSACTION_ORDER_CATEGORY_ID),
                                    rs.getString("category_title"),
                                    Category.CategoryType.values()[rs.getShort(CATEGORY_TYPE)]),
                            rs.getString(RECURRING_TRANSACTION_ORDER_PARTNER),
                            rs.getString(RECURRING_TRANSACTION_ORDER_NOTE),
                            rs.getDate(RECURRING_TRANSACTION_ORDER_NEXT_DATE).toLocalDate(),
                            endDate,
                            Interval.values()[rs.getShort(RECURRING_TRANSACTION_ORDER_INTERVAL)])
                    );
                }
                return orders;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select recurring transaction orders", e);
        }
    }

    public static Long selectFinancialAccountId(Long orderId) throws ServerException {
        String select = "SELECT " + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID
                + " FROM " + RECURRING_TRANSACTION_ORDER_TABLE
                + " WHERE " + RECURRING_TRANSACTION_ORDER_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID);
                } else {
                    throw new ServerException(404, "Recurring transaction order with id " + orderId + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select financial account id of recurring transaction order", e);
        }
    }

    public static void deleteOrder(Long orderId) throws ServerException {
        String delete = "DELETE FROM " + RECURRING_TRANSACTION_ORDER_TABLE +
                " WHERE " + RECURRING_TRANSACTION_ORDER_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setLong(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not delete transaction", e);
        }
    }

    public static void updateOrder(RecurringTransactionOrder order, Long orderId) throws ServerException {
        String update = "UPDATE " + RECURRING_TRANSACTION_ORDER_TABLE + " SET "
                + RECURRING_TRANSACTION_ORDER_DESCRIPTION + " = ?, "    // 1 DESCRIPTION
                + RECURRING_TRANSACTION_ORDER_AMOUNT + " = ?, "         // 2 AMOUNT
                + RECURRING_TRANSACTION_ORDER_NOTE + " = ?, "           // 3 NOTE
                + RECURRING_TRANSACTION_ORDER_PARTNER + " = ?, "        // 4 TRANSACTION_PARTNER
                + RECURRING_TRANSACTION_ORDER_CATEGORY_ID + " = ?, "    // 5 CATEGORY_ID
                + RECURRING_TRANSACTION_ORDER_NEXT_DATE + " = ?, "      // 6 NEXT_DATE
                + RECURRING_TRANSACTION_ORDER_END_DATE + " = ?, "       // 7 END_DATE
                + RECURRING_TRANSACTION_ORDER_INTERVAL + " = ? "        // 8 INTERVAL
                + " WHERE " + RECURRING_TRANSACTION_ORDER_ID + " = ?";  // 9 RECURRING_TRANSACTION_ORDER_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, order.getDescription());
            stmt.setBigDecimal(2, order.getAmount());
            stmt.setString(3, order.getNote());
            stmt.setString(4, order.getTransactionPartner());
            stmt.setLong(5, order.getCategory().getId());
            stmt.setDate(6, Date.valueOf(order.getNextDate()));
            if (order.getEndDate() == null) {
                stmt.setDate(7, null);
            } else {
                stmt.setDate(7, Date.valueOf(order.getEndDate()));
            }
            stmt.setInt(8, order.getInterval().ordinal());
            stmt.setLong(9, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not update " + order, e);
        }
    }

    public static List<RecurringTransactionOrder> selectListOfOutstandingOrders() throws ServerException {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Vienna"));
        LocalDate today = now.toLocalDate();
        List<RecurringTransactionOrder> outstandingOrders = new ArrayList<>();
        String select = "SELECT r." + RECURRING_TRANSACTION_ORDER_ID + ","
                + RECURRING_TRANSACTION_ORDER_DESCRIPTION + ","
                + RECURRING_TRANSACTION_ORDER_AMOUNT + ","
                + RECURRING_TRANSACTION_ORDER_NOTE + ","
                + RECURRING_TRANSACTION_ORDER_PARTNER + ","
                + RECURRING_TRANSACTION_ORDER_NEXT_DATE + ","
                + RECURRING_TRANSACTION_ORDER_END_DATE + ","
                + RECURRING_TRANSACTION_ORDER_INTERVAL + ","
                + RECURRING_TRANSACTION_ORDER_FINANCIAL_ACCOUNT_ID + ","
                + RECURRING_TRANSACTION_ORDER_CATEGORY_ID
                + ", c." + CATEGORY_TITLE + " AS category_title"
                + ", c." + CATEGORY_TYPE
                + " FROM " + RECURRING_TRANSACTION_ORDER_TABLE + " r"
                + " INNER JOIN " + CATEGORY_TABLE + " c ON"
                + " c." + CATEGORY_ID + " = r." + RECURRING_TRANSACTION_ORDER_CATEGORY_ID
                + " WHERE r." + RECURRING_TRANSACTION_ORDER_NEXT_DATE + " <= ?"
                + " ORDER BY " + RECURRING_TRANSACTION_ORDER_NEXT_DATE;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setDate(1, Date.valueOf(today));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate endDate;
                    if (rs.getDate(RECURRING_TRANSACTION_ORDER_END_DATE) != null) {
                        endDate = rs.getDate(RECURRING_TRANSACTION_ORDER_END_DATE).toLocalDate();
                    } else {
                        endDate = LocalDate.MAX;
                    }
                    outstandingOrders.add(new RecurringTransactionOrder(
                            rs.getLong(RECURRING_TRANSACTION_ORDER_ID),
                            rs.getString(RECURRING_TRANSACTION_ORDER_DESCRIPTION),
                            rs.getBigDecimal(RECURRING_TRANSACTION_ORDER_AMOUNT),
                            new Category(rs.getLong(RECURRING_TRANSACTION_ORDER_CATEGORY_ID),
                                    rs.getString("category_title"),
                                    Category.CategoryType.values()[rs.getShort(CATEGORY_TYPE)]),
                            rs.getString(RECURRING_TRANSACTION_ORDER_PARTNER),
                            rs.getString(RECURRING_TRANSACTION_ORDER_NOTE),
                            rs.getDate(RECURRING_TRANSACTION_ORDER_NEXT_DATE).toLocalDate(),
                            endDate,
                            Interval.values()[rs.getShort(RECURRING_TRANSACTION_ORDER_INTERVAL)])
                    );
                }
                return outstandingOrders;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select list of outstanding recurring transaction orders", e);
        }
    }

    public static void insertTransactionAndUpdateOrder(RecurringTransactionOrder order, Transaction transaction, Long financialAccountId) {
        String update = "UPDATE " + RECURRING_TRANSACTION_ORDER_TABLE + " SET "
                + RECURRING_TRANSACTION_ORDER_NEXT_DATE + " = ? "        // 1 NEXT_DATE
                + " WHERE " + RECURRING_TRANSACTION_ORDER_ID + " = ?";   // 2 RECURRING_TRANSACTION_ORDER_ID
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
             PreparedStatement updateStmt = conn.prepareStatement(update);
             PreparedStatement insertStmt = conn.prepareStatement(insert)) {
            conn.setAutoCommit(false);
            updateStmt.setDate(1, Date.valueOf(order.getNextDate()));
            updateStmt.setLong(2, order.getId());
            updateStmt.executeUpdate();
            insertStmt.setString(1, transaction.getDescription());
            insertStmt.setBigDecimal(2, transaction.getAmount());
            insertStmt.setDate(3, Date.valueOf(transaction.getDate()));
            insertStmt.setString(4, transaction.getTransactionPartner());
            insertStmt.setLong(5, transaction.getCategory().getId());
            insertStmt.setString(6, transaction.getNote());
            insertStmt.setBoolean(7, transaction.isAddedAutomatically());
            insertStmt.setLong(8, financialAccountId);
            insertStmt.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertTransactionAndDeleteOrder(RecurringTransactionOrder order, Transaction transaction, Long financialAccountId) {
        String delete = "DELETE FROM " + RECURRING_TRANSACTION_ORDER_TABLE
                + " WHERE " + RECURRING_TRANSACTION_ORDER_ID + " = ?";   // 1 RECURRING_TRANSACTION_ORDER_ID
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
             PreparedStatement deleteStmt = conn.prepareStatement(delete);
             PreparedStatement insertStmt = conn.prepareStatement(insert)) {
            conn.setAutoCommit(false);
            deleteStmt.setLong(1, order.getId());
            deleteStmt.executeUpdate();
            insertStmt.setString(1, transaction.getDescription());
            insertStmt.setBigDecimal(2, transaction.getAmount());
            insertStmt.setDate(3, Date.valueOf(transaction.getDate()));
            insertStmt.setString(4, transaction.getTransactionPartner());
            insertStmt.setLong(5, transaction.getCategory().getId());
            insertStmt.setString(6, transaction.getNote());
            insertStmt.setBoolean(7, transaction.isAddedAutomatically());
            insertStmt.setLong(8, financialAccountId);
            insertStmt.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
