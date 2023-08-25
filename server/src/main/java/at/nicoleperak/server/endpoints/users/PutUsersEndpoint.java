package at.nicoleperak.server.endpoints.users;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertUserIdEqualsCurrentUserId;
import static at.nicoleperak.server.database.UsersOperations.updateUser;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.AuthUtils.createPasswordHash;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.PUT;
import static java.lang.Long.parseLong;

public class PutUsersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code PUT /users/<userId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code PUT /users/<userId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == PUT
                && pathSegments.length == 2 && pathSegments[0].equals("users");
    }

    /**
     * Updates the data of an existing user.
     * Responds with status code {@code 200} in case the update was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the update.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long userId = parseLong(getPathSegments(exchange)[1]);
        editUser(exchange, userId);
        setResponse(exchange, 200, "");
    }

    /**
     * Updates the given user.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the update.
     */
    private void editUser(HttpExchange exchange, Long userId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            assertUserIdEqualsCurrentUserId(userId, currentUser.getId());
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            User editedUser = jsonb.fromJson(jsonString, User.class);
            if (editedUser.getPassword() != null) {
                String passwordHash = createPasswordHash(editedUser.getPassword());
                updateUser(editedUser, passwordHash, userId);
            } else {
                updateUser(editedUser, userId);
            }
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
