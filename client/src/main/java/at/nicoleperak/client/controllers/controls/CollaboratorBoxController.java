package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import static at.nicoleperak.client.ServiceFunctions.delete;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderSuccessAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;

public class CollaboratorBoxController {
    private User collaborator;
    private Long financialAccountId;
    @FXML
    private ImageView deleteCollaboratorIcon;
    @FXML
    private Label collaboratorEmailLabel;
    @FXML
    private Label collaboratorUserNameLabel;

    @FXML
    void onDeleteCollaboratorIconClicked() {
        removeCollaborator();
    }

    /**
     * Sends delete-request to server to remove a collaborator from a financial account,
     * following the user's confirmation.
     * Then displays success message to user and reloads the screen.
     */
    private void removeCollaborator() {
        if (userHasConfirmedActionWhenAskedForConfirmation(
                "Are you sure you want to remove \"" + collaborator.getEmail() + "\" as a collaborator?")) {
            try {
                delete("financial-accounts/"
                        + financialAccountId
                        + "/collaborators/"
                        + collaborator.getId());
                showMoneyMinderSuccessAlert(collaborator.getEmail() + " removed as collaborator");
                reloadFinancialAccountDetailsScreen();
            } catch (ClientException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    public Label getCollaboratorEmailLabel() {
        return collaboratorEmailLabel;
    }

    public Label getCollaboratorUserNameLabel() {
        return collaboratorUserNameLabel;
    }

    public void setCollaborator(User collaborator) {
        this.collaborator = collaborator;
    }

    public ImageView getDeleteCollaboratorIcon() {
        return deleteCollaboratorIcon;
    }

    public void setFinancialAccountId(Long financialAccountId) {
        this.financialAccountId = financialAccountId;
    }
}
