package at.nicoleperak.server.endpoints.recurringtransactionorders;

import at.nicoleperak.server.RecurringTransactionsExecutorService;
import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.RecurringTransactionOrder;
import at.nicoleperak.shared.User;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

import static at.nicoleperak.server.Validation.assertAuthenticatedUserIsCollaborator;
import static at.nicoleperak.server.database.RecurringTransactionOrdersOperations.insertOrder;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.POST;
import static java.lang.Long.parseLong;

public class PostRecurringTransactionOrdersEndpoint implements Endpoint {

    /**
     * Checks if the given request was sent to {@code POST /financial-accounts/<accountId>/recurring-transactions}
     *
     * @param exchange The HTTP request.
     * @return True if the request was sent to {@code POST /financial-accounts/<accountId>/recurring-transactions}. False in any other case.
     */
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == POST
                && pathSegments.length == 3 && pathSegments[2].equals("recurring-transactions");
    }

    /**
     * Creates a new recurring transaction order for a financial account.
     * Responds with status code {@code 201} in case the creation was successful.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during the creation.
     */
    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        Long financialAccountId = parseLong(getPathSegments(exchange)[1]);
        createNewRecurringTransactionOrder(exchange, financialAccountId);
        setResponse(exchange, 201, "");
    }

    /**
     * Creates a new recurring transaction order for the given financial account.
     *
     * @param exchange           The HTTP request.
     * @param financialAccountId ID of the financial account the order should be added to.
     * @throws ServerException If an error occurred during the creation.
     */
    private void createNewRecurringTransactionOrder(HttpExchange exchange, Long financialAccountId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            assertAuthenticatedUserIsCollaborator(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            RecurringTransactionOrder order = jsonb.fromJson(jsonString, RecurringTransactionOrder.class);
            insertOrder(order, financialAccountId);
            RecurringTransactionsExecutorService.executeIfOrderIsOutstanding(order);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read request body", e);
        }
    }
}
