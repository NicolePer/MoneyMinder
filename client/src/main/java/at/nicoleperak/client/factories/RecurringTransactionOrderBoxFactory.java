package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.RecurringTransactionOrderBoxController;
import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class RecurringTransactionOrderBoxFactory {

    /**
     * Constructs a box for displaying details of a recurring transaction order.
     *
     * @param order  The RecurringTransactionOrder object containing order information.
     * @param loader The FXMLLoader instance used for loading the recurring transaction order box layout.
     * @return A new recurring transaction order box containing information about the order.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    public static GridPane buildRecurringTransactionOrderBox(RecurringTransactionOrder order, FXMLLoader loader) throws IOException {
        GridPane recurringTransactionBox = loader.load();
        RecurringTransactionOrderBoxController controller = loader.getController();
        controller
                .getOrderDescriptionLabel()
                .setText(order.getDescription());
        controller
                .getOrderIntervalLabel()
                .setText(order.getInterval().getLabel());
        controller
                .setOrder(order);
        return recurringTransactionBox;
    }
}
