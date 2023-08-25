package at.nicoleperak.server.endpoints.users;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.UsersOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertUserIdEqualsCurrentUserId;
import static at.nicoleperak.server.Validation.assertUserIsNotOwnerOfAnySharedFinancialAccounts;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;
import static java.lang.Long.parseLong;

public class DeleteUsersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code DELETE /users/<userId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code DELETE /users/<userId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("users");
    }

    /**
     * Deletes a user.
     * Responds with status code {@code 204} in case the deletion was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the deletion.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long userId = parseLong(getPathSegments(exchange)[1]);
        deleteUser(exchange, userId);
        setResponse(exchange, 204, "");
    }

    /**
     * Deletes a user.
     *
     * @param exchange The HTTP request.
     * @param userId   The ID of the user to be deleted.
     * @throws ServerException If an error occurred during the deletion.
     */
    private void deleteUser(HttpExchange exchange, Long userId) throws ServerException {
        User currentUser = authenticate(exchange);
        assertUserIdEqualsCurrentUserId(userId, currentUser.getId());
        assertUserIsNotOwnerOfAnySharedFinancialAccounts(userId);
        UsersOperations.deleteUser(userId);
    }
}
