package at.nicoleperak.server.endpoints.categories;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.server.endpoints.Endpoint;
import at.nicoleperak.server.endpoints.HttpMethod;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.CategoryList;
import com.sun.net.httpserver.HttpExchange;

import static at.nicoleperak.server.databaseoperations.CategoryTableOperations.selectCategoryList;
import static at.nicoleperak.server.endpoints.AuthUtils.authenticate;
import static at.nicoleperak.server.endpoints.EndpointUtils.*;
import static at.nicoleperak.server.endpoints.HttpMethod.GET;

public class GetCategoriesByTypeEndpoint implements Endpoint {
    @Override
    public boolean canHandle(HttpExchange exchange) {
        HttpMethod requestMethod = getRequestMethod(exchange);
        String[] pathSegments = getPathSegments(exchange);
        return requestMethod == GET
                && pathSegments.length == 2 && pathSegments[0].equals("categories");
    }

    @Override
    public void handle(HttpExchange exchange) throws ServerException {
        authenticate(exchange);
        Category.CategoryType categoryType = Category.CategoryType.valueOf(getPathSegments(exchange)[1]);
        CategoryList categoryList = getCategories(categoryType);
        String jsonResponse = jsonb.toJson(categoryList);
        setResponse(exchange, 200, jsonResponse);
    }

    private CategoryList getCategories(Category.CategoryType categoryType) throws ServerException {
        return selectCategoryList(categoryType);
    }
}
