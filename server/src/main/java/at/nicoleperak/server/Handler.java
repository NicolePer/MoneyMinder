package at.nicoleperak.server;

import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.FinancialAccountsList;
import at.nicoleperak.shared.User;
import com.password4j.*;
import com.password4j.types.Argon2;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;

public class Handler implements HttpHandler {
    private final Argon2Function hashingFunction = Argon2Function.getInstance(19456, 2, 1, 128, Argon2.ID, 19);
    private final Jsonb jsonb = JsonbBuilder.create();


    @Override
    public void handle(HttpExchange exchange) {
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
                handlePost(exchange, paths);
            } else if (requestMethod.equalsIgnoreCase("GET")) {
                handleGet(exchange, paths);
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
        System.out.println("\tstatusCode = " + statusCode + "\tresponseBody = '" + jsonString + "‘");
        exchange.getResponseHeaders().set("Content-type", "application/json; charset=UTF-8");
        byte[] bytes = jsonString.getBytes(StandardCharsets.UTF_8);
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, statusCode != 204 ? bytes.length : 1);
            if (statusCode != 204) {
                os.write(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePost(HttpExchange exchange, String[] paths) throws ServerException {
        User currentUser;
        int statusCode = 200;
        String jsonResponse = "";
        if (paths.length == 1 && paths[0].equals("users")) {
            try {
                String jsonString = new String(exchange.getRequestBody().readAllBytes());
                User user = jsonb.fromJson(jsonString, User.class);
                if (Database.userExistsByEmail(user.getEmail())) {
                    throw new ServerException(409, "A user with this email address already exists");
                }
                String passwordHash = createPasswordHash(user.getPassword());
                Database.insertUser(user, passwordHash);
            } catch (SQLException e) {
                throw new ServerException(500, "Database error", e);
            } catch (IOException e) {
                throw new ServerException(400, "Could not read response body", e);
            }
        }
        else if(paths.length == 1 && paths[0].equals("financial-accounts")) {
            currentUser = authenticate(exchange);
            try {
                String jsonString = new String(exchange.getRequestBody().readAllBytes());
                FinancialAccount financialAccount = jsonb.fromJson(jsonString, FinancialAccount.class);
                financialAccount.setOwner(currentUser);
                //TODO set user as Collaborator
                financialAccount.setBalance(new BigDecimal(0));
                Database.insertFinancialAccount(financialAccount);
            } catch (SQLException e) {
                throw new ServerException(500, "Database error", e);
            } catch (IOException e) {
                throw new ServerException(400, "Could not read response body", e);
            }
        }else {
            throw new ServerException(400, "URI not supported");
        }
        setResponse(exchange, statusCode, jsonResponse);
    }

    private void handleGet(HttpExchange exchange, String[] paths) throws ServerException {
        User currentUser;
        int statusCode = 200;
        String jsonResponse = "";
        if (paths.length == 1 && paths[0].equals("users")) {
            currentUser = authenticate(exchange);
            jsonResponse = jsonb.toJson(currentUser);
        }
        else if (paths.length == 1 && paths[0].equals("financial-accounts")) {
            currentUser = authenticate(exchange);
            FinancialAccountsList financialAccountsList = getFinancialAccountsList(currentUser.getId());
            jsonResponse = jsonb.toJson(financialAccountsList);
        }
        setResponse(exchange, statusCode, jsonResponse);
    }

    private FinancialAccountsList getFinancialAccountsList(Long userId) throws ServerException {
        try {
            return Database.selectFinancialAccountsOverview(userId);
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        }
    }

    private String createPasswordHash(String password) {
        Hash hash = Password.hash(password).addRandomSalt(12).with(hashingFunction);
        return hash.getResult();
    }

    private void assertPasswordMatchesPasswordHash(String password, String passwordHash) throws ServerException {
        HashChecker hashChecker = Password.check(password, passwordHash);
        if (!hashChecker.with(hashingFunction)) {
            throw new ServerException(401, "Password incorrect");
        }
    }

    private User authenticate(HttpExchange exchange) throws ServerException {
        Headers requestHeaders = exchange.getRequestHeaders();
        String authHeaderValue = requestHeaders.getFirst("Authorization");
        authHeaderValue = authHeaderValue.substring(6);
        String decodedCredentials = new String(Base64.getDecoder().decode(authHeaderValue.getBytes()));
        String[] credentials = decodedCredentials.split(":");
        String email = credentials[0];
        String password = credentials[1];
        try {
            User currentUser = Database.selectUser(email);
            assertPasswordMatchesPasswordHash(password, currentUser.getPassword());
            currentUser.setPassword(null);
            return currentUser;
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        }
    }
}
