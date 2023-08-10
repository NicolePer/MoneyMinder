package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static at.nicoleperak.server.database.DatabaseUtils.*;
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

}
