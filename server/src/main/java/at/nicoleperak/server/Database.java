package at.nicoleperak.server;

import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.FinancialAccountsList;
import at.nicoleperak.shared.User;

import java.sql.*;
import java.util.ArrayList;

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




    public static void insertUser(User user, String passwordHash) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String insert = "INSERT INTO " + USER_TABLE
                + " (" + USER_NAME + "," + USER_EMAIL + "," + USER_PASSWORD_HASH + ") VALUES(" +
                "?," + // USER_NAME
                "?," + // USER_EMAIL
                "?)";  // USER_PASSWORD_HASH
        try {
            conn = DriverManager.getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(insert);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, passwordHash);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    public static boolean userExistsByEmail(String email) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String select = "SELECT " + USER_EMAIL + " FROM " + USER_TABLE
                + " WHERE " + USER_EMAIL + " = ?";
        try {
            conn = DriverManager.getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(select);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    public static User selectUser(String email) throws SQLException, ServerException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user;
        String select = "SELECT * FROM " + USER_TABLE
                + " WHERE " + USER_EMAIL + " = ?";
        try {
            conn = DriverManager.getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(select);
            pstmt.setString(1, email);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getLong(USER_ID), rs.getString(USER_NAME), rs.getString(USER_EMAIL), rs.getString(USER_PASSWORD_HASH));
                return user;
            } else {
                throw new ServerException(404, "User with email " + email + " does not exist");
            }
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }

    public static FinancialAccountsList selectFinancialAccountsOverview(Long id) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<FinancialAccount> financialAccounts = new ArrayList<>();
        String select = "SELECT * FROM " + FINANCIAL_ACCOUNT_TABLE
                + " WHERE " + FINANCIAL_ACCOUNT_OWNER_ID + " = ?";
        try {
            conn = DriverManager.getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(select);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
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
                //TODO EXPAND FOR COLLABORATORS
            }
            return new FinancialAccountsList(financialAccounts);
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }

    }

    public static void insertFinancialAccount(FinancialAccount financialAccount) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        String insert = "INSERT INTO " + FINANCIAL_ACCOUNT_TABLE
                + " (" + FINANCIAL_ACCOUNT_TITLE + "," + FINANCIAL_ACCOUNT_DESCRIPTION + "," + FINANCIAL_ACCOUNT_BALANCE + "," + FINANCIAL_ACCOUNT_OWNER_ID + ") VALUES(" +
                "?," + // TITLE
                "?," + // DESCRIPTION
                "?," + // BALANCE
                "?)";  // OWNER ID
        try {
            conn = DriverManager.getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(insert);
            pstmt.setString(1, financialAccount.getTitle());
            pstmt.setString(2, financialAccount.getDescription());
            pstmt.setBigDecimal(3, financialAccount.getBalance());
            pstmt.setLong(4, financialAccount.getOwner().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                throw e;
            }
        }
    }
}


