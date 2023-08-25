package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.dialogs.CreateFinancialAccountDialogController;
import at.nicoleperak.client.controllers.dialogs.EditFinancialAccountDialogController;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;

public class FinancialAccountFactory {

    /**
     * Constructs a new FinancialAccount using data from the provided dialog controller.
     *
     * @param controller The controller of the dialog gathering financial account information.
     * @return A new FinancialAccount instance with the title and description from the dialog.
     */
    public static FinancialAccount buildFinancialAccount(CreateFinancialAccountDialogController controller) {
        String title = controller.getFinancialAccountTitleField().getText();
        String description = controller.getFinancialAccountDescriptionField().getText();
        return new FinancialAccount(title, description);
    }

    /**
     * Constructs a new FinancialAccount using data from the provided dialog controller.
     *
     * @param controller The controller of the dialog gathering financial account information.
     * @return A new FinancialAccount instance with the title, description, and owner from the dialog.
     */
    public static FinancialAccount buildFinancialAccount(EditFinancialAccountDialogController controller) {
        String title = controller.getFinancialAccountTitleField().getText();
        String description = controller.getFinancialAccountDescriptionField().getText();
        User owner = controller.getOwnerComboBox().getSelectionModel().getSelectedItem();
        return new FinancialAccount(title, description, owner);
    }
}
