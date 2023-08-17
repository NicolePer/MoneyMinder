package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;

import static at.nicoleperak.client.Client.*;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNT_DETAILS_SCREEN;

public class FinancialAccountTileController extends GridPane {

    @FXML
    private Label financialAccountBalanceLabel;

    @FXML
    private GridPane financialAccountTile;

    @FXML
    private Label financialAccountTitleLabel;

    private FinancialAccount financialAccount;

    @FXML @SuppressWarnings("unused")
    protected void onFinancialAccountTileClicked(MouseEvent event) {
        try {
            setSelectedFinancialAccount(financialAccount);
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            getStage().setScene(scene);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public Label getFinancialAccountBalanceLabel() {
        return financialAccountBalanceLabel;
    }

    public Label getFinancialAccountTitleLabel() {
        return financialAccountTitleLabel;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
    }
}
