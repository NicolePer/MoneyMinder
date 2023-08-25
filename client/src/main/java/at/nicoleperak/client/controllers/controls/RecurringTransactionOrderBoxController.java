package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.RecurringTransactionDialogController;
import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.RECURRING_TRANSACTION_FORM;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderSuccessAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.RecurringTransactionOrderFactory.buildRecurringTransactionOrder;
import static javafx.scene.control.ButtonType.FINISH;

public class RecurringTransactionOrderBoxController {
    private RecurringTransactionOrder order;
    @FXML
    private Label orderDescriptionLabel;
    @FXML
    private Label orderIntervalLabel;

    @FXML
    void onDeleteRecurringTransactionOrderClicked() {
        removeRecurringTransactionOrder();
    }

    @FXML
    void onEditRecurringTransactionOrderIconClicked() {
        showEditRecurringTransactionOrderDialog();
    }

    /**
     * Sends delete-request to server to remove the recurring transaction order from a financial account,
     * following the user's confirmation.
     * Then displays success message to user and reloads the screen.
     */
    private void removeRecurringTransactionOrder() {
        if (userHasConfirmedActionWhenAskedForConfirmation(
                "Are you sure you want to delete this order?")) {
            try {
                delete("recurring-transaction-orders/" + order.getId());
                showMoneyMinderSuccessAlert("Recurring transaction order \""
                        + order.getDescription() + "\" successfully deleted");
                reloadFinancialAccountDetailsScreen();
            } catch (ClientException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    /**
     * Shows the "Edit Recurring Transaction" dialog to the user.
     * Upon finish, updates the recurring transaction order.
     */
    private void showEditRecurringTransactionOrderDialog() {
        try {
            FXMLLoader loader = RECURRING_TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            RecurringTransactionDialogController controller = loader.getController();
            controller.setSelectedRecurringTransaction(order);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                updateRecurringTransactionOrder(controller);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Takes user inputs from dialog and creates new RecurringTransactionOrder object.
     * Sends put-request to server. Then shows success message to user and reloads the screen.
     *
     * @param controller The FXML controller of the dialog that gathered the input from the user.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void updateRecurringTransactionOrder(RecurringTransactionDialogController controller) throws ClientException {
        RecurringTransactionOrder editedOrder = buildRecurringTransactionOrder(controller);
        put("recurring-transaction-orders/" + order.getId(), jsonb.toJson(editedOrder));
        showMoneyMinderSuccessAlert("Changes saved");
        reloadFinancialAccountDetailsScreen();
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
}
