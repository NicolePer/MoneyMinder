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
import static at.nicoleperak.server.database.CollaboratorsOperations.*;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;

public class PostCollaboratorsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 3 && pathSegments[2].equals("collaborators");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = Long.parseLong(getPathSegments(exchange)[1]);
        postCollaborator(exchange, financialAccountId);
        setResponse(exchange, 201, "");
    }

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
