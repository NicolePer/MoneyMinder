package at.nicoleperak.server.endpoints.recurringtransactionorders;

import at.nicoleperak.server.RecurringTransactionsExecutorService;
import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.database.RecurringTransactionOrdersOperations;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsCollaborator;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.updateOrder;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.PUT;
import static java.lang.Long.parseLong;

@SuppressWarnings({"CallToPrintStackTrace", "RedundantSuppression"})
public class PutRecurringTransactionOrdersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code PUT /recurring-transaction-orders/<orderId>}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code PUT /recurring-transaction-orders/<orderId>}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == PUT
                && pathSegments.length == 2 && pathSegments[0].equals("recurring-transaction-orders");
    }

    /**
     * Updates an existing recurring transaction order.
     * Responds with status code {@code 200} in case the update was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the update.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long orderId = parseLong(getPathSegments(exchange)[1]);
        editRecurringTransactionOrder(exchange, orderId);
        setResponse(exchange, 200, "");
    }

    /**
     * Updates the given recurring transaction order.
     *
     * @param exchange The HTTP request.
     * @throws ServerException If an error occurred during the update.
     */
    private void editRecurringTransactionOrder(HttpExchange exchange, Long orderId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            Long financialAccountId = RecurringTransactionOrdersOperations.selectFinancialAccountId(orderId);
            assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            RecurringTransactionOrder order = jsonb.fromJson(jsonString, RecurringTransactionOrder.class);
            updateOrder(order, orderId);
            RecurringTransactionsExecutorService.executeIfOrderIsOutstanding(order);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
