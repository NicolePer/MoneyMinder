package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.FinancialAccountsOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;
import static java.lang.Long.parseLong;

public class DeleteFinancialAccountsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code DELETE /financial-accounts/<accountId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code DELETE /financial-accounts/<accountId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("financial-accounts");
    }

    /**
     * Deletes a financial account.
     * Responds with status code {@code 204} in case the deletion was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the deletion.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        deleteFinancialAccount(exchange, financialAccountId);
        setResponse(exchange, 204, "");
    }

    /**
     * Deletes a financial account.
     *
     * @param exchange           The HTTP request.
     * @param financialAccountId The ID of the financial account to be deleted.
     * @throws ServerException If an error occurred during the deletion.
     */
    private void deleteFinancialAccount(HttpExchange exchange, Long financialAccountId) throws ServerException {
        User currentUser = authenticate(exchange);
        assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
        FinancialAccountsOperations.deleteFinancialAccount(financialAccountId);
    }
}
