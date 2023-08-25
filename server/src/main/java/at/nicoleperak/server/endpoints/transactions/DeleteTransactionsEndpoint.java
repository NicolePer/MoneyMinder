package at.nicoleperak.server.endpoints.transactions;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.TransactionsOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsCollaborator;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;
import static java.lang.Long.parseLong;

public class DeleteTransactionsEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code DELETE /transactions/<orderId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code DELETE /transactions/<orderId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("transactions");
    }

    /**
     * Deletes a financial transaction.
     * Responds with status code {@code 204} in case the deletion was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the deletion.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long transactionId = parseLong(getPathSegments(exchange)[1]);
        deleteTransaction(exchange, transactionId);
        setResponse(exchange, 204, "");
    }

    /**
     * Deletes a financial transaction.
     *
     * @param exchange      The HTTP request.
     * @param transactionId The ID of the transaction to be deleted.
     * @throws ServerException If an error occurred during the deletion.
     */
    private void deleteTransaction(HttpExchange exchange, Long transactionId) throws ServerException {
        User currentUser = authenticate(exchange);
        Long financialAccountId = TransactionsOperations.selectFinancialAccountId(transactionId);
        assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
        TransactionsOperations.deleteTransaction(transactionId);
    }
}
