package at.nicoleperak.client;

import at.nicoleperak.client.controllers.screens.WelcomeScreenController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

import static at.nicoleperak.client.Client.getStage;
import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;
import static at.nicoleperak.client.FXMLLocation.WELCOME_SCREEN;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;

public class Redirection {

    public static void redirectToFinancialAccountsOverviewScreen() {
        try {
            Stage stage = getStage();
            Scene scene = loadScene(FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN);
            stage.setScene(scene);
            centerStage(stage);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    public static void redirectToWelcomeScreen(String successMessage, Label alertMessageLabel) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Scene scene = loadScene(WELCOME_SCREEN, loader);
            WelcomeScreenController welcomeScreen = loader.getController();
            welcomeScreen.setWelcomeScreenAlertMessageLabelText(successMessage);
            getStage().setScene(scene);
        } catch (IOException e) {
            alertMessageLabel.setText(e.getMessage());
        }
    }

    public static void redirectToWelcomeScreen(String successMessage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Scene scene = loadScene(WELCOME_SCREEN, loader);
            WelcomeScreenController welcomeScreen = loader.getController();
            welcomeScreen.setWelcomeScreenAlertMessageLabelText(successMessage);
            Stage stage = getStage();
            stage.setScene(scene);
            centerStage(stage);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    public static void redirectToWelcomeScreen() {
        try {
            Stage stage = getStage();
            FXMLLoader loader = new FXMLLoader();
            Scene scene = loadScene(WELCOME_SCREEN, loader);
            stage.setScene(scene);
            centerStage(stage);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private static void centerStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }
}
