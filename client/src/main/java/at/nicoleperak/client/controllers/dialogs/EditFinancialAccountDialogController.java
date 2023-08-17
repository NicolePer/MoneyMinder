package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.Validation;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static javafx.event.ActionEvent.ACTION;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.FINISH;

public class EditFinancialAccountDialogController implements Initializable {

    FinancialAccount financialAccount;

    @FXML
    private Label alertMessageLabel;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextField financialAccountDescriptionField;

    @FXML
    private TextField financialAccountTitleField;

    @FXML
    private ComboBox<User> ownerComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
        onOwnerComboBoxSelectionChanged();
    }

    private void insertFinancialAccount() {
        financialAccountDescriptionField.setText(financialAccount.getDescription());
        financialAccountTitleField.setText(financialAccount.getTitle());
        ObservableList<User> collaborators = FXCollections.observableArrayList(financialAccount.getCollaborators());
        ownerComboBox.setItems(collaborators);
        ownerComboBox.getSelectionModel().select(financialAccount.getOwner());
    }

    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(FINISH);
        finish.addEventFilter(ACTION, f -> {
            String title = financialAccountTitleField.getText();
            String description = financialAccountDescriptionField.getText();
            try {
                assertUserInputLengthIsValid(title, "title", 1, 255);
                assertUserInputLengthIsValid(description, "description", 0, 255);
                Validation.assertOwnerIsSelected(ownerComboBox);
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
                f.consume();
            }
        });
    }

    public void onOwnerComboBoxSelectionChanged() {
        ownerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)
                -> new Alert(CONFIRMATION,
                """
                        If you change the owner of the financial account, you will no longer be able to
                        \tedit the financial account,\s
                        \tset/remove monthly goals and\s
                        \tadd/remove collaborators.

                        You will remain collaborator of this financial account.""").showAndWait());
    }

    public Label getAlertMessageLabel() {
        return alertMessageLabel;
    }

    public DialogPane getDialogPane() {
        return dialogPane;
    }

    public TextField getFinancialAccountDescriptionField() {
        return financialAccountDescriptionField;
    }

    public TextField getFinancialAccountTitleField() {
        return financialAccountTitleField;
    }

    public ComboBox<User> getOwnerComboBox() {
        return ownerComboBox;
    }

    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
        insertFinancialAccount();
    }

}
