package at.nicoleperak.server.endpoints.collaborators;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.CollaboratorsOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;

public class DeleteCollaboratorsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code DELETE /financial-accounts/<accountId>/collaborators/<categoryType>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code DELETE /financial-accounts/<accountId>/collaborators/<categoryType>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 4 && pathSegments[2].equals("collaborators");
    }

    /**
     * Removes a user from the collaborators of a financial account.
     * Responds with status code {@code 204} in case the removal was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the removal.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = Long.parseLong(getPathSegments(exchange)[1]);
        Long collaboratorUserId = Long.parseLong(getPathSegments(exchange)[3]);
        deleteCollaborator(exchange, financialAccountId, collaboratorUserId);
        setResponse(exchange, 204, "");
    }

    /**
     * Removes the given user from the collaborators of the given financial account.
     *
     * @param exchange           The HTTP request.
     * @param collaboratorUserId The ID of the user to be removed.
     * @param financialAccountId The ID of the financial account.
     * @throws ServerException If the removal could not be executed successfully.
     */
    private static void deleteCollaborator(HttpExchange exchange, Long financialAccountId, Long collaboratorUserId) throws ServerException {
        User currentUser = authenticate(exchange);
        assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
        CollaboratorsOperations.deleteCollaborator(collaboratorUserId, financialAccountId);
    }
}
