package at.nicoleperak.server.endpoints;

import at.nicoleperak.server.ServerException;
import com.sun.net.httpserver.HttpExchange;

public interface Endpoint {

    /**
     * Checks if this endpoint is responsible for an incoming HTTP request.
     * {@link #handle(HttpExchange)} should only be invoked if this returns {@code true}.
     *
     * @param exchange The HTTP request.
     * @return True if this endpoint can handle the given request. False in any other case.
     */
    boolean canHandle(HttpExchange exchange);

    /**
     * Handles the execution of the given HTTP request.
     * Should only be invoked if {@link #canHandle(HttpExchange)} returns {@code true}.
     *
     * @param exchange The HTTP exchange.
     * @throws ServerException If an error occurred during execution.
     */
    void handle(HttpExchange exchange) throws ServerException;

}
