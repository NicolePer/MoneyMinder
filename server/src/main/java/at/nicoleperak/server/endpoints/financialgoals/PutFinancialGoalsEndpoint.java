package at.nicoleperak.server.endpoints.financialgoals;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.FinancialGoalsOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialGoal;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.database.FinancialGoalsOperations.updateFinancialGoal;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.PUT;
import static java.lang.Long.parseLong;

public class PutFinancialGoalsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code PUT /financial-goals/<goalId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code PUT /financial-goals/<goalId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == PUT
                && pathSegments.length == 2 && pathSegments[0].equals("financial-goals");
    }

    /**
     * Updates the financial goal of a financial account.
     * Responds with status code {@code 200} in case the update was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the update.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long goalId = parseLong(getPathSegments(exchange)[1]);
        editGoal(exchange, goalId);
        setResponse(exchange, 200, "");
    }

    /**
     * Updates the given financial goal.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the update.
     */
    private void editGoal(HttpExchange exchange, Long goalId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            Long financialAccountId = FinancialGoalsOperations.selectFinancialAccountId(goalId);
            assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            FinancialGoal goal = jsonb.fromJson(jsonString, FinancialGoal.class);
            updateFinancialGoal(goal, goalId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
