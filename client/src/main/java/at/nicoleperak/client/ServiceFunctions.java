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
import java.util.Base64;

public class ServiceFunctions {
    private static final String SERVER_URI = "http://localhost:4712/";
    private static final Jsonb jsonb = JsonbBuilder.create();

    public static void post(String path, String jsonString, boolean authenticated) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request;
            if (authenticated) {
                request = HttpRequest.newBuilder(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                        .header("Authorization", getBasicAuthenticationHeader())
                        .build();
            } else {
                request = HttpRequest.newBuilder(uri)
                        .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                        .build();
            }
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
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

    public static String get(String path) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .GET()
                    .header("Authorization", getBasicAuthenticationHeader())
                    .build();
            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
            int statusCode = response.statusCode();
            String jsonResponse = new String(response.body());
            if (statusCode == 200) {
                return jsonResponse;
            } else {
                String errorMessage = jsonb.fromJson(jsonResponse, String.class);
                throw new ClientException(errorMessage);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ClientException("An unexpected error occurred", e);
        }
    }

    private static String getBasicAuthenticationHeader() {
        User userCredentials = Client.getUserCredentials();
        String credentials = userCredentials.getEmail() + ":" + userCredentials.getPassword();
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}

