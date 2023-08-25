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
import static java.lang.Long.parseLong;

public class DeleteRecurringTransactionOrdersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code DELETE /recurring-transaction-orders/<orderId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code DELETE /recurring-transaction-orders/<orderId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == DELETE
                && pathSegments.length == 2 && pathSegments[0].equals("recurring-transaction-orders");
    }

    /**
     * Deletes a recurring transaction order.
     * Responds with status code {@code 204} in case the deletion was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the deletion.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long orderId = parseLong(getPathSegments(exchange)[1]);
        deleteOrder(exchange, orderId);
        setResponse(exchange, 204, "");
    }

    /**
     * Deletes a recurring transaction order.
     *
     * @param exchange The HTTP request.
     * @param orderId  The ID of the order to be deleted.
     * @throws ServerException If an error occurred during the deletion.
     */
    private void deleteOrder(HttpExchange exchange, Long orderId) throws ServerException {
        User currentUser = authenticate(exchange);
        Long financialAccountId = RecurringTransactionOrdersOperations.selectFinancialAccountId(orderId);
        assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
        RecurringTransactionOrdersOperations.deleteOrder(orderId);
    }
}
