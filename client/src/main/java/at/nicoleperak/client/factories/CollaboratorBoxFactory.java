package at.nicoleperak.client.factories;

import at.nicoleperak.client.controllers.CollaboratorBoxController;
import at.nicoleperak.shared.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class CollaboratorBoxFactory {

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
