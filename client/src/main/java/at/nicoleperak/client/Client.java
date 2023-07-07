package at.nicoleperak.client;

import at.nicoleperak.shared.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    public static User loggedInUser;
    public static User userCredentials;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/welcome-screen.fxml"));
        try {
            Parent root = loader.load();
            WelcomeScreenFXMLController controller = loader.getController();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("MoneyMinder");
            primaryStage.show();
            root.requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
      Client.loggedInUser = loggedInUser;
    }

    public static User getUserCredentials() {
        return userCredentials;
    }

    public static void setUserCredentials(User userCredentials) {
        Client.userCredentials = userCredentials;
    }
}