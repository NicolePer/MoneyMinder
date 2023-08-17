module client {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires shared;
    requires java.net.http;
    exports at.nicoleperak.client;
    opens at.nicoleperak.client to javafx.fxml;
    exports at.nicoleperak.client.factories;
    opens at.nicoleperak.client.factories to javafx.fxml;
    exports at.nicoleperak.client.controllers.controls;
    opens at.nicoleperak.client.controllers.controls to javafx.fxml;
    exports at.nicoleperak.client.controllers.dialogs;
    opens at.nicoleperak.client.controllers.dialogs to javafx.fxml;
    exports at.nicoleperak.client.controllers.screens;
    opens at.nicoleperak.client.controllers.screens to javafx.fxml;
}