package at.nicoleperak.server.endpoints.collaborators;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.UsersOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.Validation.assertUserIsNotAlreadyCollaborator;
import static at.nicoleperak.server.database.CollaboratorsOperations.insertCollaborator;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;

public class PostCollaboratorsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code POST /financial-accounts/<accountId>/collaborators}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code POST /financial-accounts/<accountId>/collaborators}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 3 && pathSegments[2].equals("collaborators");
    }

    /**
     * Adds a user as a collaborator to a financial account.
     * Responds with status code {@code 201} in case the addition was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the addition.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = Long.parseLong(getPathSegments(exchange)[1]);
        postCollaborator(exchange, financialAccountId);
        setResponse(exchange, 201, "");
    }

    /**
     * Adds a user as a collaborator to the given financial account.
     *
     * @param exchange           The HTTP request.
     * @param financialAccountId The ID of the financial account.
     * @throws ServerException If an error occurred during the addition.
     */
    private static void postCollaborator(HttpExchange exchange, Long financialAccountId) throws ServerException {
        try {
            User currentUser = authenticate(exchange);
            assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            String userEmail = jsonb.fromJson(jsonString, String.class);
            User collaborator = UsersOperations.selectUser(userEmail);
            assertUserIsNotAlreadyCollaborator(collaborator.getId(), financialAccountId);
            insertCollaborator(collaborator.getId(), financialAccountId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
