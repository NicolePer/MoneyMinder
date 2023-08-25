package at.nicoleperak.server.endpoints;

import at.nicoleperak.server.ServerException;
import at.nicoleperak.shared.User;
import com.password4j.Argon2Function;
import com.password4j.Hash;
import com.password4j.HashChecker;
import com.password4j.Password;
import com.password4j.types.Argon2;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.util.Base64;

import static at.nicoleperak.server.database.UsersOperations.selectUser;

public class AuthUtils {

    private static final Argon2Function hashingFunction = Argon2Function.getInstance(19456, 2, 1, 128, Argon2.ID, 19);

    /**
     * Creates a secure hash of the given plain-text password.
     *
     * @param password The plain-text password.
     * @return The hash of the password.
     */
    public static String createPasswordHash(String password) {
        Hash hash = Password.hash(password).addRandomSalt(12).with(hashingFunction);
        return hash.getResult();
    }

    /**
     * Validates that an incoming request was sent by an authenticated user and returns its user data.
     * Users must authenticate themselves by appending a Basic Authentication header to every HTTP request.
     *
     * @param exchange The HTTP request.
     * @return The data of the successfully authenticated user.
     * @throws ServerException If the authentication was not successful.
     */
    public static User authenticate(HttpExchange exchange) throws ServerException {
        Headers requestHeaders = exchange.getRequestHeaders();
        String authHeaderValue = requestHeaders.getFirst("Authorization");
        authHeaderValue = authHeaderValue.substring(6);
        String decodedCredentials = new String(Base64.getDecoder().decode(authHeaderValue.getBytes()));
        String[] credentials = decodedCredentials.split(":");
        String email = credentials[0];
        String password = credentials[1];
        User currentUser = selectUser(email);
        assertPasswordMatchesPasswordHash(password, currentUser.getPassword());
        currentUser.setPassword(null);
        return currentUser;
    }

    /**
     * Throws an exception if the given password hash was not generated with the given plain-text password.
     *
     * @param password     The plain-text password.
     * @param passwordHash The password hash.
     * @throws ServerException If the password matching check failed.
     */
    private static void assertPasswordMatchesPasswordHash(String password, String passwordHash) throws ServerException {
        HashChecker hashChecker = Password.check(password, passwordHash);
        if (!hashChecker.with(hashingFunction)) {
            throw new ServerException(401, "Password incorrect");
        }
    }


}
