package at.nicoleperak.client;

import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static at.nicoleperak.client.FXMLLocation.WELCOME_SCREEN;

public class Client extends Application {
    private static User loggedInUser;
    private static User userCredentials;
    private static Stage stage;
    private static FinancialAccount selectedFinancialAccount;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            stage = primaryStage;
            Scene scene = loadScene(WELCOME_SCREEN);
            primaryStage.setScene(scene);
            primaryStage.setTitle("MoneyMinder");
            primaryStage.show();
            scene.getRoot().requestFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Scene loadScene(FXMLLocation fxmlLocation) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource(fxmlLocation.getLocation()));
        Parent root = loader.load();
        return new Scene(root);
    }

    public static Scene loadScene(FXMLLocation fxmlLocation, FXMLLoader loader) throws IOException {
        loader.setLocation(Client.class.getResource(fxmlLocation.getLocation()));
        Parent root = loader.load();
        return new Scene(root);
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

    public static Stage getStage() {
        return stage;
    }

    public static FinancialAccount getSelectedFinancialAccount() {
        return selectedFinancialAccount;
    }

    public static void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        Client.selectedFinancialAccount = selectedFinancialAccount;
    }
}