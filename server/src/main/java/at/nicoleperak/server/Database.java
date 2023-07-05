package at.nicoleperak.server;

import at.nicoleperak.shared.User;

import java.sql.*;

public class Database {
    private static final String DB_LOCATION = "//docker.for.mac.localhost:5432/postgres";
    private static final String CONNECTION = "jdbc:postgresql:" + DB_LOCATION;
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "password1234";
    private static final String USER_TABLE = "users";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "username";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD_HASH = "password_hash";

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
        String select = "SELECT " + USER_EMAIL + " FROM " + USER_TABLE
                + " WHERE " + USER_EMAIL + " = ?";
        try {
            conn = DriverManager.getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
            pstmt = conn.prepareStatement(select);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
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


