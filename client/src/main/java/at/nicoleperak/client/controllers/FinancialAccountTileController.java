package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import java.io.IOException;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNT_DETAILS_SCREEN;

public class FinancialAccountTileController extends GridPane {

    @FXML
    private Label financialAccountBalanceLabel;

    @FXML
    private GridPane financialAccountTile;

    @FXML
    private Label financialAccountTitleLabel;

    @FXML
    protected void onFinancialAccountTileClicked(MouseEvent event) {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public Label getFinancialAccountBalanceLabel() {
        return financialAccountBalanceLabel;
    }

    public Label getFinancialAccountTitleLabel() {
        return financialAccountTitleLabel;
    }
}
