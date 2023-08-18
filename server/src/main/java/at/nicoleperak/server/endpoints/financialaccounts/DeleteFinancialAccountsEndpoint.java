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
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("financial-accounts");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        deleteFinancialAccount(exchange, financialAccountId);
        setResponse(exchange, 204, "");
    }

    private void deleteFinancialAccount(HttpExchange exchange, Long financialAccountId) throws ServerException {
        User currentUser = authenticate(exchange);
        assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
        FinancialAccountsOperations.deleteFinancialAccount(financialAccountId);
    }
}
