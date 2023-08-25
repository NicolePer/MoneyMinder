package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.controls.CollaboratorBoxController;
import at.nicoleperak.shared.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class CollaboratorBoxFactory {

    /**
     * Constructs a collaborator box for displaying collaborator information.
     *
     * @param collaborator       The User object representing the collaborator.
     * @param financialAccountId The ID of the financial account associated with the collaborator.
     * @param isOwner            Indicates whether the current user is the owner of the financial account.
     * @param loader             The FXMLLoader instance used for loading the collaborator box layout.
     * @return A GridPane containing collaborator information.
     * @throws IOException If there is an issue regarding the FXML loading process.
     */
    public static GridPane buildCollaboratorBox(User collaborator, Long financialAccountId, boolean isOwner, FXMLLoader loader) throws IOException {
        GridPane collaboratorBox = loader.load();
        CollaboratorBoxController controller = loader.getController();
        controller
                .getCollaboratorUserNameLabel()
                .setText(collaborator.getUsername());
        controller
                .getCollaboratorEmailLabel()
                .setText(collaborator.getEmail());
        controller
                .setCollaborator(collaborator);
        controller
                .setFinancialAccountId(financialAccountId);
        if (isOwner) {
            controller
                    .getDeleteCollaboratorIcon().setVisible(true);
        }
        return collaboratorBox;
    }
}
