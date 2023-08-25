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
import static at.nicoleperak.client.FXMLLocation.*;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;

public class Redirection {

    /**
     * Redirects user to financial accounts overview screen.
     */
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

    /**
     * Redirects user to financial account details screen.
     */
    public static void redirectToFinancialAccountsDetailsScreen() {
        try {
            Stage stage = getStage();
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            stage.setScene(scene);
            centerStage(stage);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Redirects user to welcome screen and displays message to user on welcome screen.
     *
     * @param successMessage    Message to be displayed on welcome screen.
     * @param alertMessageLabel Label to display errorMessage if error occurs.
     */
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

    /**
     * Redirects user to welcome screen and displays message to user on welcome screen.
     *
     * @param successMessage Message to be displayed on welcome screen.
     */
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

    /**
     * Redirects user to welcome screen.
     */
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

    /**
     * Centers the JavaFX Stage on the primary screen.
     *
     * @param stage The Stage to be centered.
     */
    private static void centerStage(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }
}
