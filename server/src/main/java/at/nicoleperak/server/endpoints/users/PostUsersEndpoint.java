package at.nicoleperak.server.endpoints.users;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.database.UsersOperations.insertUser;
import static at.nicoleperak.server.database.UsersOperations.userExistsByEmail;
import static at.nicoleperak.server.endpoints.AuthUtils.createPasswordHash;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;

public class PostUsersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code POST /users}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code POST /users}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 1 && pathSegments[0].equals("users");
    }

    /**
     * Signs up a new user.
     * Responds with status code {@code 201} in case the addition was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the creation.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        signUpNewUser(exchange);
        setResponse(exchange, 201, "");
    }

    /**
     * Signs up a new user.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the signup.
     */
    private void signUpNewUser(HttpExchange exchange) throws ServerException {
        try {
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            User user = jsonb.fromJson(jsonString, User.class);
            if (userExistsByEmail(user.getEmail())) {
                throw new ServerException(409, "A user with this email address already exists");
            }
            String passwordHash = createPasswordHash(user.getPassword());
            insertUser(user, passwordHash);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
