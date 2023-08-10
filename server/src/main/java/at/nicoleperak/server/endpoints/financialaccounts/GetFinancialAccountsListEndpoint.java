package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccountsList;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Database.*;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.EndpointUtils.getPathSegments;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;

public class GetFinancialAccountsListEndpoint implements Endpoint {

    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 1 && pathSegments[0].equals("financial-accounts");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        User authenticatedUser = authenticate(exchange);
        FinancialAccountsList financialAccountsList = getFinancialAccountsList(authenticatedUser.getId());
        String jsonResponse = jsonb.toJson(financialAccountsList);
        setResponse(exchange, 200, jsonResponse);
    }

    private FinancialAccountsList getFinancialAccountsList(Long userId) throws ServerException {
        return selectListOfFinancialAccountOverviews(userId);
    }
}
