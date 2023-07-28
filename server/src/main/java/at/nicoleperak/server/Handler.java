package at.nicoleperak.server;

import at.nicoleperak.shared.*;
import at.nicoleperak.shared.Category.CategoryType;
import com.password4j.*;
import com.password4j.types.Argon2;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

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
            } else if (requestMethod.equalsIgnoreCase("PUT")) {
                handlePut(exchange, paths);
            }else {
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

    private void handlePut(HttpExchange exchange, String[] paths) throws ServerException {
        int statusCode = 200;
        String jsonResponse = "";
        if (paths.length == 2 && paths[0].equals("transactions")) {
            Long transactionId = Long.parseLong(paths[1]);
            editTransaction(exchange, transactionId);
        } else {
            throw new ServerException(400, "URI not supported");
        }
        setResponse(exchange, statusCode, jsonResponse);
    }

    private void handlePost(HttpExchange exchange, String[] paths) throws ServerException {
        int statusCode = 200;
        String jsonResponse = "";
        if (paths.length == 1 && paths[0].equals("users")) {
            signUpNewUser(exchange);
        } else if (paths.length == 1 && paths[0].equals("financial-accounts")) {
            createNewFinancialAccount(exchange);
        } else if (paths.length == 3 && paths[2].equals("transactions")) {
            Long financialAccountId = Long.parseLong(paths[1]);
            createNewTransaction(exchange, financialAccountId);
        } else {
            throw new ServerException(400, "URI not supported");
        }
        setResponse(exchange, statusCode, jsonResponse);
    }

    private void handleGet(HttpExchange exchange, String[] paths) throws ServerException {
        int statusCode = 200;
        User authenticatedUser;
        String jsonResponse = "";
        if (paths.length == 1 && paths[0].equals("users")) {
            authenticatedUser = authenticate(exchange);
            jsonResponse = jsonb.toJson(authenticatedUser);
        } else if (paths.length == 1 && paths[0].equals("financial-accounts")) {
            authenticatedUser = authenticate(exchange);
            FinancialAccountsList financialAccountsList = getFinancialAccountsList(authenticatedUser.getId());
            jsonResponse = jsonb.toJson(financialAccountsList);
        } else if (paths.length == 2 && paths[0].equals("financial-accounts")) {
            Long financialAccountId = Long.parseLong(paths[1]);
            authenticatedUser = authenticate(exchange);
            assertAuthenticatedUserIsOwnerOrCollaborator(authenticatedUser.getId(), financialAccountId);
            FinancialAccount financialAccount = getFinancialAccount(financialAccountId);
            jsonResponse = jsonb.toJson(financialAccount);
        } else if (paths.length == 1 && paths[0].equals("categories")) {
            authenticate(exchange);
            CategoryList categoryList = getCategories();
            jsonResponse = jsonb.toJson(categoryList);
        } else if (paths.length == 2 && paths[0].equals("categories")) {
            authenticate(exchange);
            CategoryType categoryType = CategoryType.valueOf(paths[1]);
            CategoryList categoryList = getCategories(categoryType);
            jsonResponse = jsonb.toJson(categoryList);
        }
        setResponse(exchange, statusCode, jsonResponse);
    }


    private void assertAuthenticatedUserIsOwnerOrCollaborator(Long userId, Long financialAccountId) throws ServerException {
        try {
            List<Long> userIds = Database.selectOwnerAndCollaboratorsIdsOfFinancialAccount(financialAccountId);
            if (!userIds.contains(userId)) {
                throw new ServerException(401, "User is not authorized to access this financial account");
            }
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        }
    }

    private void editTransaction(HttpExchange exchange, Long transactionId) throws ServerException {
        User currentUser = authenticate(exchange);
        // TODO Change Path?
        try {
            Long financialAccountId = Database.selectFinancialAccountId(transactionId);
            assertAuthenticatedUserIsOwnerOrCollaborator(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            Transaction transaction = jsonb.fromJson(jsonString, Transaction.class);
            Database.updateTransaction(transaction, transactionId);

        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read response body", e);
        }
    }

    private void createNewTransaction(HttpExchange exchange, Long financialAccountId) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            assertAuthenticatedUserIsOwnerOrCollaborator(currentUser.getId(), financialAccountId);
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            Transaction transaction = jsonb.fromJson(jsonString, Transaction.class);
            Database.insertTransaction(transaction, financialAccountId);
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read response body", e);
        }
    }

    private void createNewFinancialAccount(HttpExchange exchange) throws ServerException {
        User currentUser = authenticate(exchange);
        try {
            String jsonString = new String(exchange.getRequestBody().readAllBytes());
            FinancialAccount financialAccount = jsonb.fromJson(jsonString, FinancialAccount.class);
            financialAccount.setOwner(currentUser);
            //TODO for later: set user as Collaborator
            Database.insertFinancialAccount(financialAccount);
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        } catch (IOException e) {
            throw new ServerException(400, "Could not read response body", e);
        }
    }

    private void signUpNewUser(HttpExchange exchange) throws ServerException {
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

    private FinancialAccountsList getFinancialAccountsList(Long userId) throws ServerException {
        try {
            return Database.selectListOfFinancialAccountOverviews(userId);
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        }
    }

    private FinancialAccount getFinancialAccount(Long userId) throws ServerException {
        try {
            return Database.selectFullFinancialAccount(userId);
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        }
    }

    private CategoryList getCategories(CategoryType categoryType) throws ServerException {
        try {
            return Database.selectCategoryList(categoryType);
        } catch (SQLException e) {
            throw new ServerException(500, "Database error", e);
        }
    }

    private CategoryList getCategories() throws ServerException {
        try {
            return Database.selectCategoryList();
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
