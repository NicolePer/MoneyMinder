package at.nicoleperak.server;

public class Database {
    private static final String DB_LOCATION = "//docker.for.mac.localhost:5432/postgres";
    private static final String CONNECTION = "jdbc:postgresql:" + DB_LOCATION;
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "password1234";

}
