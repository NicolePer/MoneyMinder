package at.nicoleperak.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class FinancialAccountTileController extends GridPane {

    @FXML
    private Label financialAccountBalanceLabel;

    @FXML
    private GridPane financialAccountTile;

    @FXML
    private Label financialAccountTitleLabel;

    public Label getFinancialAccountBalanceLabel() {
        return financialAccountBalanceLabel;
    }

    public Label getFinancialAccountTitleLabel() {
        return financialAccountTitleLabel;
    }

    @FXML
    protected void onFinancialAccountTileClicked(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/financial-account-details-screen.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = (Stage) financialAccountTile.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
