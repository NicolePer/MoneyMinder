package at.nicoleperak.server.endpoints.transactions;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.Transaction;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsCollaborator;
import static at.nicoleperak.server.database.TransactionsOperations.selectFinancialAccountId;
import static at.nicoleperak.server.database.TransactionsOperations.updateTransaction;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.PUT;
import static java.lang.Long.parseLong;

public class PutTransactionsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code PUT /transactions/<orderId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code PUT /transactions/<orderId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == PUT
                && pathSegments.length == 2 && pathSegments[0].equals("transactions");
    }

    /**
     * Updates an existing financial transaction.
     * Responds with status code {@code 200} in case the update was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the update.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long transactionId = parseLong(getPathSegments(exchange)[1]);
        editTransaction(exchange, transactionId);
        setResponse(exchange, 200, "");
    }

    /**
     * Updates the given transaction.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the update.
     */
    private void editTransaction(HttpExchange exchange, Long transactionId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            Long financialAccountId = selectFinancialAccountId(transactionId);
            assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            Transaction transaction = jsonb.fromJson(jsonString, Transaction.class);
            updateTransaction(transaction, transactionId);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
