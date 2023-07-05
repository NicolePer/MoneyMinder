package at.nicoleperak.server;

import at.nicoleperak.shared.User;
import com.password4j.*;
import com.password4j.types.Argon2;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class Handler implements HttpHandler {

    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            URI uri = exchange.getRequestURI();
            System.out.println(uri + " " + requestMethod);
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            String[] paths = path.split("/");
            if (requestMethod.equalsIgnoreCase("POST")) {
                post(exchange, paths);
            } else {
                throw new ServerException(400, "Unsupported HTTP-Method");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String jsonString = "An unexpected error occurred";
            int statusCode = 500; // INTERNAL SERVER ERROR
            if (e instanceof ServerException serverException) {
                statusCode = serverException.getStatusCode();
                jsonString = jsonb.toJson(serverException.getMessage());
            }
            setResponse(exchange, statusCode, jsonString);
        }
    }

    private void setResponse(HttpExchange exchange, int statusCode, String jsonString) {
        System.out.println("\tstatusCode = " + statusCode + "\treponsebody = '" + jsonString + "‘");
        exchange.getResponseHeaders().set("Content-type", "application/json; charset=UTF-8");
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, bytes.length);
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void post(HttpExchange exchange, String[] paths) throws ServerException {
        int statusCode = 200;
        String jsonResponse = "";
        if (paths.length == 1 && paths[0].equals("users")) {
            try {
                String jsonString = new String(exchange.getRequestBody().readAllBytes());
                User user = jsonb.fromJson(jsonString, User.class);
                if (Database.userExistsByEmail(user.getEmail())) {
                    throw new ServerException(409, "A user with this email address already exists");
                }
                Argon2Function argon2 = Argon2Function.getInstance(19456, 2, 1, 128, Argon2.ID, 19);
                Hash hash = Password.hash(user.getPassword()).addRandomSalt(12).with(argon2);
                Database.insertUser(user, hash.getResult());
            } catch (SQLException e) {
                throw new ServerException(500, "Database error", e);
            } catch (IOException e) {
                throw new ServerException(400, "Could not read response body", e);
            }
        } else {
            throw new ServerException(400, "URI not supported");
        }
        setResponse(exchange, statusCode, jsonResponse);
    }
}
