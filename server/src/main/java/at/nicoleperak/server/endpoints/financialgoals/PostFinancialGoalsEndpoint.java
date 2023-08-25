package at.nicoleperak.server.endpoints.financialgoals;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialGoal;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.database.FinancialGoalsOperations.insertFinancialGoal;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;
import static java.lang.Long.parseLong;

public class PostFinancialGoalsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code POST /financial-accounts/<accountId>/financial-goals}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code POST /financial-accounts/<accountId>/financial-goals}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 3 && pathSegments[2].equals("financial-goals");
    }

    /**
     * Creates a new financial goal for a financial account.
     * Responds with status code {@code 201} in case the creation was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the creation.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        postFinancialGoal(exchange, financialAccountId);
        setResponse(exchange, 201, "");

    }

    /**
     * Creates a new financial goal for the given financial account.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the creation.
     */
    private static void postFinancialGoal(HttpExchange exchange, Long financialAccountId) throws ServerException {
        try {
            User currentUser = authenticate(exchange);
            assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            FinancialGoal goal = jsonb.fromJson(jsonString, FinancialGoal.class);
            insertFinancialGoal(goal, financialAccountId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
