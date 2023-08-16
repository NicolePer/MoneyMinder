package at.nicoleperak.server.endpoints.recurringtransactionorders;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.RecurringTransactionOrdersOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsCollaborator;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.DELETE;

public class DeleteRecurringTransactionOrder implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("recurring-transaction-orders");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long orderId = Long.parseLong(getPathSegments(exchange)[1]);
        deleteOrder(exchange, orderId);
        setResponse(exchange, 204, "");
    }

    private void deleteOrder(HttpExchange exchange, Long orderId) throws ServerException {
        User currentUser = authenticate(exchange);
        Long financialAccountId = RecurringTransactionOrdersOperations.selectFinancialAccountId(orderId);
        assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
        RecurringTransactionOrdersOperations.deleteOrder(orderId);
    }
}
