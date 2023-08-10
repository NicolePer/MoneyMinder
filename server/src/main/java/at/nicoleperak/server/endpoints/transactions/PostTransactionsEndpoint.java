package at.nicoleperak.server.endpoints.transactions;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.Transaction;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.databaseoperations.TransactionsTableOperations.insertTransaction;
import static at.nicoleperak.server.endpoints.AuthUtils.assertAuthenticatedUserIsOwnerOrCollaborator;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;

public class PostTransactionsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 3 && pathSegments[2].equals("transactions");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = Long.parseLong(getPathSegments(exchange)[1]);
        createNewTransaction(exchange, financialAccountId);
        setResponse(exchange, 201, "");
    }

    private void createNewTransaction(HttpExchange exchange, Long financialAccountId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            assertAuthenticatedUserIsOwnerOrCollaborator(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            Transaction transaction = jsonb.fromJson(jsonString, Transaction.class);
            insertTransaction(transaction, financialAccountId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
