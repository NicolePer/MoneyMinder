package at.nicoleperak.server;

import java.util.List;

import static at.nicoleperak.server.database.CollaboratorsOperations.selectCollaboratorIds;
import static at.nicoleperak.server.database.FinancialAccountsOperations.selectListOfSharedFinancialAccountIdsWhereUserIsOwner;
import static at.nicoleperak.server.database.FinancialAccountsOperations.selectOwnerIdOfFinancialAccount;

public class Validation {

    public static void assertAuthenticatedUserIsCollaborator(Long userId, Long financialAccountId) throws ServerException {
        List<Long> userIds = selectCollaboratorIds(financialAccountId);
        if (!userIds.contains(userId)) {
            throw new ServerException(403, "User is not authorized to access this financial account");
        }
    }
    public static void assertAuthenticatedUserIsOwner(Long userId, Long financialAccountId) throws ServerException {
        Long ownerId = selectOwnerIdOfFinancialAccount(financialAccountId);
        if(!userId.equals(ownerId)){
            throw new ServerException(403, "User is not owner of the financial account");
        }
    }

    public static void assertUserIsNotAlreadyCollaborator(Long userId, Long financialAccountId) throws ServerException {
        List<Long> userIds = selectCollaboratorIds(financialAccountId);
        if (userIds.contains(userId)) {
            throw new ServerException(403, "User is already collaborator");
        }
    }

    public static void assertUserIdEqualsCurrentUserId(Long userId, Long currentUserId) throws ServerException {
        if (!userId.equals(currentUserId)) {
            throw new ServerException(403, "User is not authorized to access the requested user account");
        }
    }

    public static void assertUserIsNotOwnerOfAnySharedFinancialAccounts(Long userId) throws ServerException {
        List<Long> sharedFinancialAccountIds = selectListOfSharedFinancialAccountIdsWhereUserIsOwner(userId);
        if (!sharedFinancialAccountIds.isEmpty()) {
            int numberOfAccounts = sharedFinancialAccountIds.size();
            throw new ServerException(403, "User account cannot be deleted, because user is owner of " + numberOfAccounts + " shared financial account(s)");
        }
    }

}
