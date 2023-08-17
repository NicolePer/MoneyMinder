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
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("financial-goals");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long goalId = parseLong(getPathSegments(exchange)[1]);
        deleteGoal(exchange, goalId);
        setResponse(exchange, 204, "");
    }

    private void deleteGoal(HttpExchange exchange, Long goalId) throws ServerException {
        User currentUser = authenticate(exchange);
        Long financialAccountId = FinancialGoalsOperations.selectFinancialAccountId(goalId);
        assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
        deleteFinancialGoal(goalId);
    }
}
