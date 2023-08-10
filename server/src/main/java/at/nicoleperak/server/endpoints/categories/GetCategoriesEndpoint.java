package at.nicoleperak.server.endpoints.categories;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.CategoryList;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.database.CategoryOperations.selectCategoryList;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;

public class GetCategoriesEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 1 && pathSegments[0].equals("categories");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        authenticate(exchange);
        CategoryList categoryList = getCategories();
        String jsonResponse = jsonb.toJson(categoryList);
        setResponse(exchange, 200, jsonResponse);
    }

    private CategoryList getCategories() throws ServerException {
        return selectCategoryList();
    }
}
