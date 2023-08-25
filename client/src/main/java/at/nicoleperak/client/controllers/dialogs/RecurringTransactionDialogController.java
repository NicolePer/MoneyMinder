package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.PopulationUtils;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.CategoryList;
import at.nicoleperak.shared.RecurringTransactionOrder;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Validation.*;
import static at.nicoleperak.shared.Category.CategoryType.INCOME;
import static at.nicoleperak.shared.RecurringTransactionOrder.Interval;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.ButtonType.FINISH;

public class RecurringTransactionDialogController implements Initializable {

    private final ObservableList<Category> categoryObservableList = observableArrayList();
    private RecurringTransactionOrder selectedRecurringTransaction;
    @FXML
    private Label nextDateLabel;
    @FXML
    private Label alertMessageLabel;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<Category> categoryComboBox;
    @FXML
    private TextField descriptionField;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private RadioButton expenseRadioButton;
    @FXML
    private Label headerTextLabel;
    @FXML
    private RadioButton incomeRadioButton;
    @FXML
    private ComboBox<Interval> intervalComboBox;
    @FXML
    private DatePicker nextDatePicker;
    @FXML
    private TextArea noteArea;
    @FXML
    private TextField transactionPartnerField;
    @FXML
    private Label transactionPartnerLabel;
    @FXML
    private ToggleGroup transactionTypeToggleGroup;

    /**
     * Upon initialization, engages event filters and event listeners for the dialog. Populates the Interval ComboBox.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engageEventFilters();
        populateIntervalComboBox();
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
    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(FINISH);
        finish.addEventFilter(ActionEvent.ACTION, f -> {
            LocalDate nextDate = nextDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String transactionPartner = transactionPartnerField.getText();
            String description = descriptionField.getText();
            String note = noteArea.getText();
            String amountString = convertIntoParsableDecimal(amountField.getText());
            try {
                assertRadioButtonIsSelected(transactionTypeToggleGroup);
                assertCategoryIsSelected(categoryComboBox);
                assertIntervalIsSelected(intervalComboBox);
                assertDateIsNotNull(nextDate);
                if (endDate != null) {
                    assertDateIsInTheFuture(endDate);
                }
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

    /**
     * Sets the variable selectedRecurringTransaction to the recurring transaction order chosen by the user.
     * Sets the headerText of the dialog to "Edit Recurring Transaction".
     * Inserts the recurring transaction order details into the dialog.
     * This method is called when the dialog is used as to edit an existing recurring transaction order.
     *
     * @param selectedRecurringTransaction The recurring transaction order selected by the user.
     */
    public void setSelectedRecurringTransaction(RecurringTransactionOrder selectedRecurringTransaction) {
        this.selectedRecurringTransaction = selectedRecurringTransaction;
        headerTextLabel.setText("Edit Recurring Transaction Order");
        nextDateLabel.setText("Next Date");
        insertRecurringTransactionOrder();
    }

    /**
     * Populates the Interval ComboBox with a list of interval values.
     */
    private void populateIntervalComboBox() {
        final ObservableList<Interval> intervalList
                = observableArrayList(List.of(Interval.values()));
        intervalComboBox.setItems(intervalList);
    }


    /**
     * Inserts the details of the selected recurring transaction order into the dialog.
     */
    private void insertRecurringTransactionOrder() {
        if (selectedRecurringTransaction.getCategory().getType().equals(INCOME)) {
            incomeRadioButton.setSelected(true);
        } else {
            expenseRadioButton.setSelected(true);
        }
        categoryComboBox.getSelectionModel().select(selectedRecurringTransaction.getCategory());
        intervalComboBox.setValue(selectedRecurringTransaction.getInterval());
        nextDatePicker.setValue(selectedRecurringTransaction.getNextDate());
        if (!selectedRecurringTransaction.getEndDate().equals(LocalDate.MAX)) {
            endDatePicker.setValue(selectedRecurringTransaction.getEndDate());
        }
        amountField.setText(selectedRecurringTransaction.getAmount().abs().toString());
        transactionPartnerField.setText(selectedRecurringTransaction.getTransactionPartner());
        descriptionField.setText(selectedRecurringTransaction.getDescription());
        noteArea.setText(selectedRecurringTransaction.getNote());
    }

    public Label getAlertMessageLabel() {
        return alertMessageLabel;
    }

    public TextField getAmountField() {
        return amountField;
    }

    public ComboBox<Category> getCategoryComboBox() {
        return categoryComboBox;
    }

    public TextField getDescriptionField() {
        return descriptionField;
    }

    public DatePicker getEndDatePicker() {
        return endDatePicker;
    }

    public ComboBox<Interval> getIntervalComboBox() {
        return intervalComboBox;
    }

    public DatePicker getNextDatePicker() {
        return nextDatePicker;
    }

    public TextArea getNoteArea() {
        return noteArea;
    }

    public TextField getTransactionPartnerField() {
        return transactionPartnerField;
    }
}
