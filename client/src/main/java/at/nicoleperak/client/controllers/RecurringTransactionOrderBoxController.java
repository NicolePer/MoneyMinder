package at.nicoleperak.client.controllers;

import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class RecurringTransactionOrderBoxController {

    private RecurringTransactionOrder order;

    private Long financialAccountId;
    @FXML
    private ImageView deleteRecurringTransactionOrderIcon;

    @FXML
    private ImageView editRecurringTransactionOrderIcon;

    @FXML
    private Label orderDescriptionLabel;

    @FXML
    private Label orderIntervalLabel;

    @FXML
    private GridPane recurringTransactionOrderBox;

    @FXML
    void onDeleteRecurringTransactionOrderClicked(MouseEvent event) {

    }

    @FXML
    void onEditRecurringTransactionOrderIconClicked(MouseEvent event) {

    }

    public Label getOrderDescriptionLabel() {
        return orderDescriptionLabel;
    }

    public Label getOrderIntervalLabel() {
        return orderIntervalLabel;
    }

    public void setOrder(RecurringTransactionOrder order) {
        this.order = order;
    }

    public RecurringTransactionOrder getOrder() {
        return order;
    }

    public void setFinancialAccountId(Long financialAccountId) {
        this.financialAccountId = financialAccountId;
    }
}
