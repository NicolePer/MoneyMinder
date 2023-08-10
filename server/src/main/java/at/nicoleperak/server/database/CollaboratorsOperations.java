package at.nicoleperak.server.database;

import at.nicoleperak.server.ServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static at.nicoleperak.server.database.DatabaseUtils.*;
import static java.sql.DriverManager.getConnection;

public class CollaboratorsOperations {

    protected static final String COLLABORATOR_TABLE = "collaborators";
    protected static final String COLLABORATOR_ID = "id";
    protected static final String COLLABORATOR_FINANCIAL_ACCOUNT_ID = "financial_account_id";
    protected static final String COLLABORATOR_USER_ID = "user_id";

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

    public static List<Long> selectCollaboratorIdsOfFinancialAccount(Long financialAccountId) throws ServerException {
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
}
