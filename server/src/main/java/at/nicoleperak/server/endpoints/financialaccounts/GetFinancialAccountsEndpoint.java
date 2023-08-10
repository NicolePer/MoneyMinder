package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.databaseoperations.FinancialAccountsTableOperations.selectFullFinancialAccount;
import static at.nicoleperak.server.endpoints.AuthUtils.assertAuthenticatedUserIsOwnerOrCollaborator;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;
import static java.lang.Long.parseLong;

public class GetFinancialAccountsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 2 && pathSegments[0].equals("financial-accounts");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        User authenticatedUser = authenticate(exchange);
        assertAuthenticatedUserIsOwnerOrCollaborator(authenticatedUser.getId(), financialAccountId);
        FinancialAccount financialAccount = getFinancialAccount(financialAccountId);
        String jsonResponse = jsonb.toJson(financialAccount);
        setResponse(exchange, 200, jsonResponse);
    }

    private FinancialAccount getFinancialAccount(Long userId) throws ServerException {
        return selectFullFinancialAccount(userId);
    }
}
