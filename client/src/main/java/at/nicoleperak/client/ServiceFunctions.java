package at.nicoleperak.client;

import at.nicoleperak.shared.User;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static at.nicoleperak.client.Client.getUserCredentials;
import static java.net.http.HttpClient.newHttpClient;
import static java.net.http.HttpRequest.BodyPublishers;
import static java.net.http.HttpRequest.newBuilder;
import static java.net.http.HttpResponse.BodyHandlers;
import static java.util.Base64.getEncoder;

public class ServiceFunctions {
    public static final Jsonb jsonb = JsonbBuilder.create();
    private static final String SERVER_URI = "http://localhost:4712/";

    /**
     * Sends an HTTP POST request with the provided JSON body to the specified path on the server.
     *
     * @param path          The path to which the POST request is sent.
     * @param jsonString    The JSON body of the POST request.
     * @param authenticated Indicates whether the request requires authentication.
     * @throws ClientException If an error occurs during the request or if the response status code is not 201.
     */
    public static void post(String path, String jsonString, boolean authenticated) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = newHttpClient();
            HttpRequest request;
            if (authenticated) {
                request = newBuilder(uri)
                        .POST(BodyPublishers.ofString(jsonString))
                        .header("Authorization", getBasicAuthenticationHeader())
                        .build();
            } else {
                request = newBuilder(uri)
                        .POST(BodyPublishers.ofString(jsonString))
                        .build();
            }
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            int statusCode = response.statusCode();
            if (statusCode != 201) {
                String jsonResponse = new String(response.body());
                String errorMessage = jsonb.fromJson(jsonResponse, String.class);
                throw new ClientException(errorMessage);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ClientException("An unexpected error occurred", e);
        }
    }

    /**
     * Sends an HTTP GET request to the specified path on the server.
     *
     * @param path The path to which the GET request is sent.
     * @return The JSON response content from the GET request.
     * @throws ClientException If an error occurs during the request or if the response status code is not 200.
     */
    public static String get(String path) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = newHttpClient();
            HttpRequest request = newBuilder(uri)
                    .GET()
                    .header("Authorization", getBasicAuthenticationHeader())
                    .build();
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            int statusCode = response.statusCode();
            String jsonResponse = new String(response.body());
            if (statusCode == 200) {
                return jsonResponse;
            } else {
                String errorMessage = jsonb.fromJson(jsonResponse, String.class);
                throw new ClientException(statusCode + ": " + errorMessage);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ClientException("An unexpected error occurred", e);
        }
    }

    /**
     * Sends an HTTP PUT request with the provided JSON body to the specified path on the server.
     *
     * @param path       The path to which the PUT request is sent.
     * @param jsonString The JSON body of the PUT request.
     * @throws ClientException If an error occurs during the request or if the response status code is not 200.
     */
    public static void put(String path, String jsonString) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = newHttpClient();
            HttpRequest request = newBuilder(uri)
                    .PUT(BodyPublishers.ofString(jsonString))
                    .header("Authorization", getBasicAuthenticationHeader())
                    .build();
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            int statusCode = response.statusCode();
            if (statusCode != 200) {
                String jsonResponse = new String(response.body());
                String errorMessage = jsonb.fromJson(jsonResponse, String.class);
                throw new ClientException(errorMessage);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ClientException("An unexpected error occurred", e);
        }
    }

    /**
     * Sends an HTTP DELETE request to the specified path on the server.
     *
     * @param path The path to which the DELETE request is sent.
     * @throws ClientException If an error occurs during the request or if the response status code is not 204.
     */
    public static void delete(String path) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = newHttpClient();
            HttpRequest request = newBuilder(uri)
                    .DELETE()
                    .header("Authorization", getBasicAuthenticationHeader())
                    .build();
            HttpResponse<byte[]> response = client.send(request, BodyHandlers.ofByteArray());
            int statusCode = response.statusCode();
            if (statusCode != 204) {
                String jsonResponse = new String(response.body());
                String errorMessage = jsonb.fromJson(jsonResponse, String.class);
                throw new ClientException(errorMessage);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ClientException("An unexpected error occurred", e);
        }
    }

    /**
     * Creates the Basic Authentication header using the user's credentials.
     *
     * @return The Basic Authentication header.
     */
    private static String getBasicAuthenticationHeader() {
        User userCredentials = getUserCredentials();
        String credentials = userCredentials.getEmail() + ":" + userCredentials.getPassword();
        return "Basic " + getEncoder().encodeToString(credentials.getBytes());
    }
}

