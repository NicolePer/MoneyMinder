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

public class DeleteTransactionsEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("transactions");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long transactionId = Long.parseLong(getPathSegments(exchange)[1]);
        deleteTransaction(exchange, transactionId);
        setResponse(exchange, 204, "");
    }

    private void deleteTransaction(HttpExchange exchange, Long transactionId) throws ServerException {
        User currentUser = authenticate(exchange);
        Long financialAccountId = TransactionsOperations.selectFinancialAccountId(transactionId);
        assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
        TransactionsOperations.deleteTransaction(transactionId);
    }
}
