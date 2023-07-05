module client {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires shared;
    requires java.net.http;
    exports at.nicoleperak.client;
    opens at.nicoleperak.client to javafx.fxml;
}