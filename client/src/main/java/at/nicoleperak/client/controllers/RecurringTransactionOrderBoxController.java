package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static at.nicoleperak.client.ServiceFunctions.delete;
import static at.nicoleperak.client.controllers.FinancialAccountDetailsScreenController.*;
import static javafx.scene.control.Alert.AlertType.*;

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
        try {
            delete("recurring-transaction-orders/" + order.getId());
            new Alert(INFORMATION, "Recurring transaction order \""
                    + order.getDescription() + "\" successfully deleted").showAndWait();
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
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
