package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static at.nicoleperak.server.database.DatabaseUtils.*;
import static at.nicoleperak.server.database.UsersOperations.*;
import static java.sql.DriverManager.getConnection;

public class CollaboratorsOperations {

    protected static final String COLLABORATOR_TABLE = "collaborators";
    protected static final String COLLABORATOR_FINANCIAL_ACCOUNT_ID = "financial_account_id";
    protected static final String COLLABORATOR_USER_ID = "user_id";

    /**
     * Adds the given user as a collaborator to the given financial account.
     *
     * @param userId             ID of the user.
     * @param financialAccountId ID of the financial account.
     * @throws ServerException If the transaction could not be created.
     */
    public static void insertCollaborator(Long userId, Long financialAccountId) throws ServerException {
        String insert = "INSERT INTO " + COLLABORATOR_TABLE
                + " (" + COLLABORATOR_FINANCIAL_ACCOUNT_ID + "," + COLLABORATOR_USER_ID
                + ") VALUES(" +
                "?," + // 1 FINANCIAL_ACCOUNT_ID
                "?)";  // 2 COLLABORATOR_USER_ID
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(insert)) {
            stmt.setLong(1, financialAccountId);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not create collaborator", e);
        }
    }

    /**
     * Gets the IDs of all users that collaborate on the given financial account.
     *
     * @param financialAccountId The ID of the financial account.
     * @return List of user IDs.
     * @throws ServerException If the database could not be queried successfully.
     */
    public static List<Long> selectCollaboratorIds(Long financialAccountId) throws ServerException {
        String select = "SELECT " + COLLABORATOR_USER_ID + " FROM " + COLLABORATOR_TABLE
                + " WHERE " + COLLABORATOR_FINANCIAL_ACCOUNT_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Long> userIds = new ArrayList<>();
                while (rs.next()) {
                    userIds.add(rs.getLong(COLLABORATOR_USER_ID));
                }
                return userIds;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select collaborator ids of financial account", e);
        }
    }

    /**
     * Gets the data of all users that collaborate on the given financial account.
     *
     * @param financialAccountId The ID of the financial account.
     * @return List of users.
     * @throws ServerException If the database could not be queried successfully.
     */
    public static List<User> selectListOfCollaborators(Long financialAccountId) throws ServerException {
        String select = "SELECT c." + COLLABORATOR_USER_ID
                + ", u." + USER_NAME
                + ", u." + USER_EMAIL
                + " FROM " + COLLABORATOR_TABLE + " c"
                + " INNER JOIN " + USER_TABLE + " u ON"
                + " u." + USER_ID + " = c." + COLLABORATOR_USER_ID
                + " WHERE c." + COLLABORATOR_FINANCIAL_ACCOUNT_ID + " = ?"
                + " ORDER BY u." + USER_NAME + ", " + USER_EMAIL;
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(select)) {
            stmt.setLong(1, financialAccountId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<User> collaborators = new ArrayList<>();
                while (rs.next()) {
                    collaborators.add(new User(
                            rs.getLong(COLLABORATOR_USER_ID),
                            rs.getString(USER_NAME),
                            rs.getString(USER_EMAIL),
                            null));
                }
                return collaborators;
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Could not select collaborator ids of financial account", e);
        }
    }

    /**
     * Removes the given user from the collaborators of the given financial account.
     *
     * @param collaboratorUserId The ID of the user to be removed.
     * @param financialAccountId The ID of the financial account.
     * @throws ServerException If the removal could not be executed successfully.
     */
    public static void deleteCollaborator(Long collaboratorUserId, Long financialAccountId) throws ServerException {
        String delete = "DELETE FROM " + COLLABORATOR_TABLE +
                " WHERE " + COLLABORATOR_USER_ID + " = ?" +
                " AND " + COLLABORATOR_FINANCIAL_ACCOUNT_ID + " = ?";
        try (Connection conn = getConnection(CONNECTION, DB_USERNAME, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(delete)) {
            stmt.setLong(1, collaboratorUserId);
            stmt.setLong(2, financialAccountId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ServerException(500, "Could not delete collaborator", e);
        }
    }
}
