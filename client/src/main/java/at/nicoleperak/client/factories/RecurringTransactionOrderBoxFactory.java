package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.RecurringTransactionOrderBoxController;
import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class RecurringTransactionOrderBoxFactory {

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
