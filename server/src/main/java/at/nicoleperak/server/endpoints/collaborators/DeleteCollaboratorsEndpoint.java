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
import static at.nicoleperak.server.endpoints.EndpointUtils.setResponse;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;

public class DeleteCollaboratorsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 4 && pathSegments[2].equals("collaborators");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = Long.parseLong(getPathSegments(exchange)[1]);
        Long collaboratorUserId = Long.parseLong(getPathSegments(exchange)[3]);
        deleteCollaborator(exchange, financialAccountId, collaboratorUserId);
        setResponse(exchange, 204, "");
    }

    private static void deleteCollaborator(HttpExchange exchange, Long financialAccountId, Long collaboratorUserId) throws ServerException {
        User currentUser = authenticate(exchange);
        assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
        CollaboratorsOperations.deleteCollaborator(collaboratorUserId, financialAccountId);
    }
}
