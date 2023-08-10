package at.nicoleperak.server.endpoints;

import at.nicoleperak.server.ServerException;
import com.sun.net.httpserver.HttpExchange;

public interface Endpoint {

    boolean canHandle(HttpExchange exchange);

    void handle(HttpExchange exchange) throws ServerException;

}
