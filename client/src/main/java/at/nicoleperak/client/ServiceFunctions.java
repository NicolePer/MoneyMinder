package at.nicoleperak.client;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServiceFunctions {
    private static final String SERVER_URI = "http://localhost:4712/";
    private static final Jsonb jsonb = JsonbBuilder.create();

    public static void post(String path, String jsonString) throws ClientException {
        String uriS = SERVER_URI + path;
        try {
            URI uri = new URI(uriS);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                    .build();
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
}

