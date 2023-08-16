package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static javafx.scene.control.Alert.AlertType.*;

public class CollaboratorBoxController {

    private User collaborator;

    private Long financialAccountId;

    @FXML
    private ImageView deleteCollaboratorIcon;

    @FXML
    private GridPane collaboratorBox;

    @FXML
    private Label collaboratorEmailLabel;

    @FXML
    private Label collaboratorUserNameLabel;

    @FXML
    void onDeleteCollaboratorIconClicked(MouseEvent event) {
        removeCollaborator();
    }

    private void removeCollaborator() {
        try {
            delete("financial-accounts/" + financialAccountId + "/collaborators/" + collaborator.getId());
            new Alert(INFORMATION, collaborator.getEmail() + " successfully removed as collaborator").showAndWait();
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
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
