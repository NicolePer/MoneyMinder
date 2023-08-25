package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccountsList;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.database.FinancialAccountsOperations.selectListOfFinancialAccountOverviews;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;

public class GetFinancialAccountsListEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code GET /financial-accounts}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code GET /financial-accounts}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 1 && pathSegments[0].equals("financial-accounts");
    }

    /**
     * Responds with a list of all financial accounts the current user has access to.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the execution.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        User authenticatedUser = authenticate(exchange);
        FinancialAccountsList financialAccountsList = getFinancialAccountsList(authenticatedUser.getId());
        String jsonResponse = jsonb.toJson(financialAccountsList);
        setResponse(exchange, 200, jsonResponse);
    }

    /**
     * Gets the overview data of all financial accounts that the given user collaborates on.
     *
     * @param userId The ID of the user.
     * @return List of financial accounts.
     * @throws ServerException If the accounts could not be queried.
     */
    private FinancialAccountsList getFinancialAccountsList(Long userId) throws ServerException {
        return selectListOfFinancialAccountOverviews(userId);
    }
}
