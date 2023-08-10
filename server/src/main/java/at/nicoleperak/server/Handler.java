package at.nicoleperak.server;

import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.categories.GetCategoriesByTypeEndpoint;
import at.nicoleperak.server.endpoints.categories.GetCategoriesEndpoint;
import at.nicoleperak.server.endpoints.financialaccounts.GetFinancialAccountsEndpoint;
import at.nicoleperak.server.endpoints.financialaccounts.GetFinancialAccountsListEndpoint;
import at.nicoleperak.server.endpoints.financialaccounts.PostFinancialAccountsEndpoint;
import at.nicoleperak.server.endpoints.transactions.DeleteTransactionsEndpoint;
import at.nicoleperak.server.endpoints.transactions.PostTransactionsEndpoint;
import at.nicoleperak.server.endpoints.transactions.PutTransactionsEndpoint;
import at.nicoleperak.server.endpoints.users.GetUsersEndpoint;
import at.nicoleperak.server.endpoints.users.PostUsersEndpoint;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.List;
import java.util.Optional;

import static at.nicoleperak.server.endpoints.EndpointUtils.jsonb;
import static at.nicoleperak.server.endpoints.EndpointUtils.setResponse;

public class Handler implements HttpHandler {
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
            new DeleteTransactionsEndpoint()
    );

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
            String jsonString = "An unexpected error occurred";
            int statusCode = 500;
            if (e instanceof ServerException serverException) {
                statusCode = serverException.getStatusCode();
                jsonString = jsonb.toJson(serverException.getMessage());
            }
            setResponse(exchange, statusCode, jsonString);
        }
    }
}
