package at.nicoleperak.server;

import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.categories.GetCategoriesByTypeEndpoint;
import at.nicoleperak.server.endpoints.categories.GetCategoriesEndpoint;
import at.nicoleperak.server.endpoints.collaborators.DeleteCollaboratorsEndpoint;
import at.nicoleperak.server.endpoints.collaborators.PostCollaboratorsEndpoint;
import at.nicoleperak.server.endpoints.financialaccounts.*;
import at.nicoleperak.server.endpoints.financialgoals.DeleteFinancialGoalsEndpoint;
import at.nicoleperak.server.endpoints.financialgoals.PostFinancialGoalsEndpoint;
import at.nicoleperak.server.endpoints.financialgoals.PutFinancialGoalsEndpoint;
import at.nicoleperak.server.endpoints.recurringtransactionorders.DeleteRecurringTransactionOrdersEndpoint;
import at.nicoleperak.server.endpoints.recurringtransactionorders.PostRecurringTransactionOrdersEndpoint;
import at.nicoleperak.server.endpoints.recurringtransactionorders.PutRecurringTransactionOrdersEndpoint;
import at.nicoleperak.server.endpoints.transactions.DeleteTransactionsEndpoint;
import at.nicoleperak.server.endpoints.transactions.PostTransactionsEndpoint;
import at.nicoleperak.server.endpoints.transactions.PutTransactionsEndpoint;
import at.nicoleperak.server.endpoints.users.DeleteUsersEndpoint;
import at.nicoleperak.server.endpoints.users.GetUsersEndpoint;
import at.nicoleperak.server.endpoints.users.PostUsersEndpoint;
import at.nicoleperak.server.endpoints.users.PutUsersEndpoint;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.List;
import java.util.Optional;

import static at.nicoleperak.server.endpoints.EndpointUtils.jsonb;
import static at.nicoleperak.server.endpoints.EndpointUtils.setResponse;

public class EndpointsHandler implements HttpHandler {
    private final List<Endpoint> endpoints = List.of(
            new PostUsersEndpoint(),
            new GetUsersEndpoint(),
            new PostFinancialAccountsEndpoint(),
            new GetFinancialAccountsListEndpoint(),
            new GetFinancialAccountsEndpoint(),
            new GetCategoriesEndpoint(),
            new GetCategoriesByTypeEndpoint(),
            new PostTransactionsEndpoint(),
            new PutTransactionsEndpoint(),
            new DeleteTransactionsEndpoint(),
            new PostCollaboratorsEndpoint(),
            new DeleteCollaboratorsEndpoint(),
            new PostRecurringTransactionOrdersEndpoint(),
            new DeleteRecurringTransactionOrdersEndpoint(),
            new PutRecurringTransactionOrdersEndpoint(),
            new PostFinancialGoalsEndpoint(),
            new DeleteFinancialGoalsEndpoint(),
            new PutFinancialGoalsEndpoint(),
            new PutFinancialAccountsEndpoint(),
            new DeleteFinancialAccountsEndpoint(),
            new PutUsersEndpoint(),
            new DeleteUsersEndpoint()
    );

    /**
     * Handles all incoming HTTP requests.
     *
     * @param exchange the exchange containing the request from the
     *                 client and used to send the response
     */
    @Override
    public void handle(HttpExchange exchange) {
        try {
            Optional<Endpoint> applicableEndpoint = endpoints.stream()
                    .filter(endpoint -> endpoint.canHandle(exchange))
                    .findFirst();
            if (applicableEndpoint.isPresent()) {
                applicableEndpoint.get().handle(exchange);
            } else {
                throw new ServerException(400, "Unsupported endpoint");
            }
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            String errorMessage = "An unexpected error occurred";
            int statusCode = 500;
            if (e instanceof ServerException serverException) {
                statusCode = serverException.getStatusCode();
                errorMessage = serverException.getMessage();
            }
            setResponse(exchange, statusCode, jsonb.toJson(errorMessage));
        }
    }
}
