package at.nicoleperak.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;

public class Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        System.out.println(uri + " " + requestMethod);
        String path = uri.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        String[] paths = path.split("/");
    }
}
