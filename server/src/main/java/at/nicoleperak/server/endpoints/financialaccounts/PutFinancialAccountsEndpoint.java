package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsOwner;
import static at.nicoleperak.server.database.FinancialAccountsOperations.updateFinancialAccount;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.PUT;
import static java.lang.Long.parseLong;

public class PutFinancialAccountsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == PUT
                && pathSegments.length == 2 && pathSegments[0].equals("financial-accounts");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        editFinancialAccount(exchange, financialAccountId);
        setResponse(exchange, 200, "");
    }

    private void editFinancialAccount(HttpExchange exchange, Long financialAccountId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            assertAuthenticatedUserIsOwner(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            FinancialAccount financialAccount = jsonb.fromJson(jsonString, FinancialAccount.class);
            updateFinancialAccount(financialAccount, financialAccountId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
