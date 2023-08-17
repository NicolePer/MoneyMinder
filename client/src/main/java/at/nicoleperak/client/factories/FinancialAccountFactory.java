package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.dialogs.CreateFinancialAccountDialogController;
import at.nicoleperak.client.controllers.dialogs.EditFinancialAccountDialogController;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;

public class FinancialAccountFactory {
    public static FinancialAccount buildFinancialAccount(CreateFinancialAccountDialogController controller) {
        String title = controller.getFinancialAccountTitleField().getText();
        String description = controller.getFinancialAccountDescriptionField().getText();
        return new FinancialAccount(title, description);
    }

    public static FinancialAccount buildFinancialAccount(EditFinancialAccountDialogController controller) {
        String title = controller.getFinancialAccountTitleField().getText();
        String description = controller.getFinancialAccountDescriptionField().getText();
        User owner = controller.getOwnerComboBox().getSelectionModel().getSelectedItem();
        return new FinancialAccount(title, description, owner);
    }
}
