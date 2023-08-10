package at.nicoleperak.server;

import java.util.List;

import static at.nicoleperak.server.database.CollaboratorsOperations.selectCollaboratorIdsOfFinancialAccount;
import static at.nicoleperak.server.database.FinancialAccountsOperations.selectOwnerIdOfFinancialAccount;

public class Validation {

    public static void assertAuthenticatedUserIsCollaborator(Long userId, Long financialAccountId) throws ServerException {
        List<Long> userIds = selectCollaboratorIdsOfFinancialAccount(financialAccountId);
        if (!userIds.contains(userId)) {
            throw new ServerException(401, "User is not authorized to access this financial account");
        }
    }
    public static void assertAuthenticatedUserIsOwner(Long userId, Long financialAccountId) throws ServerException {
        Long ownerId = selectOwnerIdOfFinancialAccount(financialAccountId);
        if(!userId.equals(ownerId)){
            throw new ServerException(401, "User is not owner of the financial account");
        }
    }

    public static void assertUserIsNotAlreadyCollaborator(Long userId, Long financialAccountId) throws ServerException {
        List<Long> userIds = selectCollaboratorIdsOfFinancialAccount(financialAccountId);
        if (userIds.contains(userId)) {
            throw new ServerException(401, "User is already collaborator");
        }
    }

}
