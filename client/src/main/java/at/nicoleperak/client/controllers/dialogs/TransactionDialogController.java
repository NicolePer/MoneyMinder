package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.PopulationUtils;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.CategoryList;
import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Validation.*;
import static at.nicoleperak.shared.Category.CategoryType.INCOME;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.event.ActionEvent.ACTION;

public class TransactionDialogController implements Initializable {

    private final ObservableList<Category> categoryObservableList = observableArrayList();
    private Transaction selectedTransaction;
    @FXML
    private Label alertMessageLabel;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField descriptionField;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private RadioButton expenseRadioButton;
    @FXML
    private RadioButton incomeRadioButton;
    @FXML
    private TextArea noteArea;
    @FXML
    private TextField transactionPartnerField;
    @FXML
    private ToggleGroup transactionTypeToggleGroup;
    @FXML
    private Label transactionPartnerLabel;
    @FXML
    private Label headerTextLabel;

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
     * Engages a number of event listeners on the dialog.
     */
    private void engageEventListeners() {
        listenForChangesOnRadioButtonSelection();
    }

    /**
     * Engages a number of event filters on the dialog.
     */
    private void engageEventFilters() {
        validateUserInputsOnFinish();
    }

    /**
     * Sets up an event filter that will be executed when the "Finish" button of the dialog is clicked.
     * Checks whether the user inputs into the dialog are valid and informs the user in case they are not.
     */
    private void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(ButtonType.FINISH);
        finish.addEventFilter(ACTION, f -> {
            LocalDate date = datePicker.getValue();
            String transactionPartner = transactionPartnerField.getText();
            String description = descriptionField.getText();
            String note = noteArea.getText();
            String amountString = convertIntoParsableDecimal(amountField.getText());
            try {
                assertRadioButtonIsSelected(transactionTypeToggleGroup);
                assertCategoryIsSelected(categoryComboBox);
                assertDateIsNotNull(date);
                assertDateIsInPast(date);
                assertUserInputLengthIsValid(amountField.getText(), "amount", 1, 255);
                assertAmountIsBigDecimal(amountString);
                assertUserInputLengthIsValid(transactionPartner, "transaction partner (source / recipient)", 1, 255);
                assertUserInputLengthIsValid(description, "description", 1, 255);
                assertUserInputLengthIsValid(note, "note", 0, 1000);
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
                f.consume();
            }
        });
    }

    /**
     * Sets the variable selectedTransaction to the transaction chosen by the user.
     * Sets the headerText of the dialog to "Edit Transaction" .
     * Inserts the transaction details into the dialog.
     * This method is called when the dialog is used as to edit an existing transaction.
     *
     * @param selectedTransaction The transaction selected by the user.
     */
    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
        headerTextLabel.setText("Edit Transaction");
        insertTransactionDetails();
    }

    /**
     * Inserts the details of the selected transaction into the dialog.
     */
    private void insertTransactionDetails() {
        if (selectedTransaction.getCategory().getType().equals(INCOME)) {
            incomeRadioButton.setSelected(true);
        } else {
            expenseRadioButton.setSelected(true);
        }
        categoryComboBox.getSelectionModel().select(selectedTransaction.getCategory());
        datePicker.setValue(selectedTransaction.getDate());
        amountField.setText(selectedTransaction.getAmount().abs().toString());
        transactionPartnerField.setText(selectedTransaction.getTransactionPartner());
        descriptionField.setText(selectedTransaction.getDescription());
        noteArea.setText(selectedTransaction.getNote());
    }

    /**
     * Sets up an event listener on the selection of a toggle group of radio buttons.
     * Triggers the population of the category ComboBox and replaces the transactionPartnerLabel.
     */
    private void listenForChangesOnRadioButtonSelection() {
        transactionTypeToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, selectedToggle) -> {
            if (selectedToggle.isSelected()) {
                populateCategoryComboBox();
                replaceTransactionPartnerLabel((RadioButton) selectedToggle);
            }
        });
    }

    /**
     * Populates the Category ComboBox with categories depending on the CategoryType selected by the user.
     * Enables the ComboBox.
     */
    private void populateCategoryComboBox() {
        categoryObservableList.clear();
        CategoryList categoryList = PopulationUtils.populateCategories(incomeRadioButton);
        categoryObservableList.addAll(categoryList.getCategories());
        categoryComboBox.setItems(categoryObservableList);
        categoryComboBox.setDisable(false);
    }

    /**
     * Replaces the content of the transactionPartnerLabel depending on a RadioButton.
     *
     * @param radioButton RadioButton that determines the content of the transactionPartnerLabel.
     */
    private void replaceTransactionPartnerLabel(RadioButton radioButton) {
        String labelText = null;
        if (radioButton.equals(incomeRadioButton)) {
            labelText = "Source";
        } else if (radioButton.equals(expenseRadioButton)) {
            labelText = "Recipient";
        }
        transactionPartnerLabel.setText(labelText);
    }

    public TextField getAmountField() {
        return amountField;
    }

    public ComboBox<Category> getCategoryComboBox() {
        return categoryComboBox;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextField getDescriptionField() {
        return descriptionField;
    }

    public TextArea getNoteArea() {
        return noteArea;
    }

    public TextField getTransactionPartnerField() {
        return transactionPartnerField;
    }
}
