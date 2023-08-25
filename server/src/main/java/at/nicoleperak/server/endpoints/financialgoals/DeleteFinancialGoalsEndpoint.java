package at.nicoleperak.server.endpoints.financialgoals;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.FinancialGoalsOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.database.FinancialGoalsOperations.deleteFinancialGoal;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;
import static java.lang.Long.parseLong;

public class DeleteFinancialGoalsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code DELETE /financial-goals/<goalId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code DELETE /financial-goals/<goalId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("financial-goals");
    }

    /**
     * Deletes a financial goal.
     * Responds with status code {@code 204} in case the deletion was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the deletion.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long goalId = parseLong(getPathSegments(exchange)[1]);
        deleteGoal(exchange, goalId);
        setResponse(exchange, 204, "");
    }

    /**
     * Deletes the given financial code.
     *
     * @param exchange The HTTP request.
     * @param goalId   The ID of the financial goal to be deleted.
     * @throws ServerException If an error occurred during the deletion.
     */
    private void deleteGoal(HttpExchange exchange, Long goalId) throws ServerException {
        User currentUser = authenticate(exchange);
        Long financialAccountId = FinancialGoalsOperations.selectFinancialAccountId(goalId);
        assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
        deleteFinancialGoal(goalId);
    }
}
