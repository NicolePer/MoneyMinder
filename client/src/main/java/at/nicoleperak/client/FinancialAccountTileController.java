package at.nicoleperak.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class FinancialAccountTileController extends GridPane{

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
}
