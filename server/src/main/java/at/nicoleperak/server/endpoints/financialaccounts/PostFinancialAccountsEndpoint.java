package at.nicoleperak.server.endpoints.financialaccounts;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.CollaboratorsOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.database.FinancialAccountsOperations.insertFinancialAccount;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;

public class PostFinancialAccountsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code POST /financial-accounts}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code POST /financial-accounts}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 1 && pathSegments[0].equals("financial-accounts");
    }

    /**
     * Creates a new financial account.
     * Responds with status code {@code 201} in case the creation was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the creation.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        createNewFinancialAccount(exchange);
        setResponse(exchange, 201, "");
    }

    /**
     * Creates a new financial account.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the creation.
     */
    private void createNewFinancialAccount(HttpExchange exchange) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            FinancialAccount financialAccount = jsonb.fromJson(jsonString, FinancialAccount.class);
            financialAccount.setOwner(currentUser);
            Long financialAccountId = insertFinancialAccount(financialAccount);
            if (financialAccountId.equals(-1L)){
                throw new ServerException(500, "Could not create financial account");
            }
            CollaboratorsOperations.insertCollaborator(currentUser.getId(),financialAccountId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
