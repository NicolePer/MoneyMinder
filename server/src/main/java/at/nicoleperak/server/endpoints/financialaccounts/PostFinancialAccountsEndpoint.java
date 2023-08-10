package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.databaseoperations.FinancialAccountsTableOperations.insertFinancialAccount;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;

public class PostFinancialAccountsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 1 && pathSegments[0].equals("financial-accounts");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        createNewFinancialAccount(exchange);
        setResponse(exchange, 201, "");
    }

    private void createNewFinancialAccount(HttpExchange exchange) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            FinancialAccount financialAccount = jsonb.fromJson(jsonString, FinancialAccount.class);
            financialAccount.setOwner(currentUser);
            //TODO for later: set user as Collaborator
            insertFinancialAccount(financialAccount);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
