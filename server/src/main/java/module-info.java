module server {
    requires jdk.httpserver;
    requires shared;
    requires password4j;
    requires java.sql;
    exports at.nicoleperak.server;
}