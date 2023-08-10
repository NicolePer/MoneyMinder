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

    public static String createPasswordHash(String password) {
        Hash hash = Password.hash(password).addRandomSalt(12).with(hashingFunction);
        return hash.getResult();
    }

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

    private static void assertPasswordMatchesPasswordHash(String password, String passwordHash) throws ServerException {
        HashChecker hashChecker = Password.check(password, passwordHash);
        if (!hashChecker.with(hashingFunction)) {
            throw new ServerException(401, "Password incorrect");
        }
    }


}
