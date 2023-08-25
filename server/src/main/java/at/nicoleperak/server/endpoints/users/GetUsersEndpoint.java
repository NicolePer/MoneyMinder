package at.nicoleperak.server.endpoints.users;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;

public class GetUsersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code GET /users}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code GET /users}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 1 && pathSegments[0].equals("users");
    }

    /**
     * Responds with the detailed data of the user that sent the request.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the execution.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        User authenticatedUser = authenticate(exchange);
        String jsonResponse = jsonb.toJson(authenticatedUser);
        setResponse(exchange, 200, jsonResponse);
    }
}
