package at.nicoleperak.server;

import java.util.List;

import static at.nicoleperak.server.database.CollaboratorsOperations.selectCollaboratorIds;
import static at.nicoleperak.server.database.FinancialAccountsOperations.selectListOfSharedFinancialAccountIdsWhereUserIsOwner;
import static at.nicoleperak.server.database.FinancialAccountsOperations.selectOwnerIdOfFinancialAccount;

public class Validation {

    /**
     * Asserts that the given user is among the collaborators of the given financial account.
     *
     * @param userId             The ID of the user.
     * @param financialAccountId The ID of the financial account.
     * @throws ServerException If the user is not among the collaborators of the account.
     */
    public static void assertAuthenticatedUserIsCollaborator(Long userId, Long financialAccountId) throws ServerException {
        List<Long> userIds = selectCollaboratorIds(financialAccountId);
        if (!userIds.contains(userId)) {
            throw new ServerException(403, "User is not authorized to access this financial account");
        }
    }

    /**
     * Asserts that the given user is the owner of the given financial account.
     *
     * @param userId The ID of the user.
     * @param financialAccountId The ID of the financial account.
     * @throws ServerException If the user is not the owner of the account.
     */
    public static void assertAuthenticatedUserIsOwner(Long userId, Long financialAccountId) throws ServerException {
        Long ownerId = selectOwnerIdOfFinancialAccount(financialAccountId);
        if(!userId.equals(ownerId)){
            throw new ServerException(403, "User is not owner of the financial account");
        }
    }

    /**
     * Asserts that the given user is not already among the collaborators of the given financial account.
     *
     * @param userId The ID of the user.
     * @param financialAccountId The ID of the financial account.
     * @throws ServerException If the user is already among the collaborators of the account.
     */
    public static void assertUserIsNotAlreadyCollaborator(Long userId, Long financialAccountId) throws ServerException {
        List<Long> userIds = selectCollaboratorIds(financialAccountId);
        if (userIds.contains(userId)) {
            throw new ServerException(403, "User is already collaborator");
        }
    }

    /**
     * Asserts that the two given users are the same.
     *
     * @param userId The ID of the user.
     * @param currentUserId The ID of the current user.
     * @throws ServerException If the two users are not the same.
     */
    public static void assertUserIdEqualsCurrentUserId(Long userId, Long currentUserId) throws ServerException {
        if (!userId.equals(currentUserId)) {
            throw new ServerException(403, "User is not authorized to access the requested user account");
        }
    }

    /**
     * Asserts that the given user does not own any financial accounts that are shared between multiple collaborators.
     *
     * @param userId The ID of the user.
     * @throws ServerException If the user owns any financial accounts that are shared between multiple collaborators.
     */
    public static void assertUserIsNotOwnerOfAnySharedFinancialAccounts(Long userId) throws ServerException {
        List<Long> sharedFinancialAccountIds = selectListOfSharedFinancialAccountIdsWhereUserIsOwner(userId);
        if (!sharedFinancialAccountIds.isEmpty()) {
            int numberOfAccounts = sharedFinancialAccountIds.size();
            throw new ServerException(403, "User account cannot be deleted, because user is owner of " + numberOfAccounts + " shared financial account(s)");
        }
    }

}
