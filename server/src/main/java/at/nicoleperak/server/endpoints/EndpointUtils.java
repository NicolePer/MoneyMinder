package at.nicoleperak.server.endpoints;

import com.sun.net.httpserver.HttpExchange;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class EndpointUtils {

    public static final Jsonb jsonb = JsonbBuilder.create();

    public static HttpMethod getRequestMethod(HttpExchange exchange) {
        String requestMethod = exchange.getRequestMethod();
        return HttpMethod.valueOf(requestMethod.toUpperCase());
    }

    public static String[] getPathSegments(HttpExchange exchange) {
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path.split("/");
    }

    public static void setResponse(HttpExchange exchange, int statusCode, String jsonString) {
        exchange.getResponseHeaders().set("Content-type", "application/json; charset=UTF-8");
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, statusCode != 204 ? bytes.length : -1);
            if (statusCode != 204) {
                os.write(bytes);
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }
}
