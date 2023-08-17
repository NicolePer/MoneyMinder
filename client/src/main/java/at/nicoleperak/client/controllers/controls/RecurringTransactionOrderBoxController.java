package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.RecurringTransactionDialogController;
import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.RECURRING_TRANSACTION_FORM;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.RecurringTransactionOrderFactory.buildRecurringTransactionOrder;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.ButtonType.FINISH;

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
        removeRecurringTransactionOrder();
    }

    @FXML
    void onEditRecurringTransactionOrderIconClicked(MouseEvent event) {
        showEditRecurringTransactionOrderDialog();

    }

    private void removeRecurringTransactionOrder() {
        try {
            delete("recurring-transaction-orders/" + order.getId());
            new Alert(INFORMATION, "Recurring transaction order \""
                    + order.getDescription() + "\" successfully deleted").showAndWait();
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }
    private void showEditRecurringTransactionOrderDialog() {
        try {
            FXMLLoader loader = RECURRING_TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            RecurringTransactionDialogController controller = loader.getController();
            controller.setSelectedRecurringTransaction(order);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH){
                RecurringTransactionOrder editedOrder = buildRecurringTransactionOrder(controller);
                putEditedRecurringTransactionOrder(editedOrder);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void putEditedRecurringTransactionOrder(RecurringTransactionOrder editedOrder) {
        try {
            put("recurring-transaction-orders/" + order.getId(), jsonb.toJson(editedOrder));
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    public Label getOrderDescriptionLabel() {
        return orderDescriptionLabel;
    }

    public Label getOrderIntervalLabel() {
        return orderIntervalLabel;
    }

    public RecurringTransactionOrder getOrder() {
        return order;
    }

    public void setOrder(RecurringTransactionOrder order) {
        this.order = order;
    }

    public void setFinancialAccountId(Long financialAccountId) {
        this.financialAccountId = financialAccountId;
    }
}
