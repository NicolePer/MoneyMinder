package at.nicoleperak.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class TransactionTileController {

    @FXML
    private Label transactionAmountLabel;

    @FXML
    private Label transactionDateLabel;

    @FXML
    private Label transactionDescriptionLabel;

    @FXML
    private Label transactionPartnerLabel;

    @FXML
    private GridPane transactionTile;

    public Label getTransactionAmountLabel() {
        return transactionAmountLabel;
    }

    public Label getTransactionDateLabel() {
        return transactionDateLabel;
    }

    public Label getTransactionDescriptionLabel() {
        return transactionDescriptionLabel;
    }


    public Label getTransactionPartnerLabel() {
        return transactionPartnerLabel;
    }


}
