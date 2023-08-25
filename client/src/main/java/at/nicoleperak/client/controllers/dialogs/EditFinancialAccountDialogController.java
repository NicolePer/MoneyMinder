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
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderWarningAlert;
import static javafx.event.ActionEvent.ACTION;
import static javafx.scene.control.ButtonType.FINISH;

public class EditFinancialAccountDialogController implements Initializable {
    private FinancialAccount financialAccount;
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

    /**
     * Upon initialization, engages event listeners and event filters for the dialog.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engageEventFilters();
        engageEventListeners();
    }

    /**
     * Sets the financialAccount to be edited and inserts the details of the account into the dialog.
     *
     * @param financialAccount Financial account to be edited.
     */
    public void setFinancialAccount(FinancialAccount financialAccount) {
        this.financialAccount = financialAccount;
        insertFinancialAccount();
    }

    /**
     * Engages a number of event filters on the dialog.
     */
    private void engageEventFilters() {
        validateUserInputsOnFinish();
    }

    /**
     * Engages a number of event listeners on the dialog.
     */
    private void engageEventListeners() {
        listenForChangesOnOwnerComboBoxSelection();
    }

    /**
     * Sets up an event filter that will be executed when the "Finish" button of the dialog is clicked.
     * Checks whether the user inputs into the dialog are valid and informs the user in case they are not.
     */
    private void validateUserInputsOnFinish() {
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

    /**
     * Sets up an event listener on the selection of the ownerComboBox.
     * Displays warning to user.
     */
    private void listenForChangesOnOwnerComboBoxSelection() {
        ownerComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.getId().equals(financialAccount.getOwner().getId())) {
                showMoneyMinderWarningAlert(
                        """
                                If you change the owner of the financial account, you will no longer be able to
                                \tedit the financial account,\s
                                \tset/remove monthly goals and\s
                                \tadd/remove collaborators.
                                        
                                You will remain collaborator of this financial account.""");
            }
        });
    }

    /**
     * Inserts the details of a selected financial account into the dialog.
     */
    private void insertFinancialAccount() {
        financialAccountDescriptionField.setText(financialAccount.getDescription());
        financialAccountTitleField.setText(financialAccount.getTitle());
        ObservableList<User> collaborators = FXCollections.observableArrayList(financialAccount.getCollaborators());
        ownerComboBox.setItems(collaborators);
        ownerComboBox.getSelectionModel().select(financialAccount.getOwner());
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

}
