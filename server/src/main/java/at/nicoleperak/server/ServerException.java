package at.nicoleperak.server;

public class ServerException extends Exception {
    private final int statusCode;

    public ServerException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public ServerException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
