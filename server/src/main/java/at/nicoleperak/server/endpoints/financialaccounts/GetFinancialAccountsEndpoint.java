package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsCollaborator;
import static at.nicoleperak.server.database.FinancialAccountsOperations.selectFullFinancialAccount;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;
import static java.lang.Long.parseLong;

public class GetFinancialAccountsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code GET /financial-accounts/<accountId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code GET /financial-accounts/<accountId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 2 && pathSegments[0].equals("financial-accounts");
    }

    /**
     * Responds with the detailed data of a financial account.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the execution.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        User authenticatedUser = authenticate(exchange);
        assertAuthenticatedUserIsCollaborator(authenticatedUser.getId(), financialAccountId);
        FinancialAccount financialAccount = getFinancialAccount(financialAccountId);
        String jsonResponse = jsonb.toJson(financialAccount);
        setResponse(exchange, 200, jsonResponse);
    }

    /**
     * Returns the data of the financial account with the given ID.
     *
     * @param financialAccountId The ID of the financial account.
     * @return The financial account.
     * @throws ServerException If the account could not be queried.
     */
    private FinancialAccount getFinancialAccount(Long financialAccountId) throws ServerException {
        return selectFullFinancialAccount(financialAccountId);
    }
}
