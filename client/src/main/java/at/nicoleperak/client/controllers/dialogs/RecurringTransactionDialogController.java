package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
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
import static at.nicoleperak.client.LoadingUtils.loadCategories;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
        loadCategoriesOnSelectionOfRadioButton();
        setIntervalComboBox();
        replaceTransactionPartnerLabelOnSelectionOfRadioButton(incomeRadioButton, "Source");
        replaceTransactionPartnerLabelOnSelectionOfRadioButton(expenseRadioButton, "Recipient");
    }

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

    private void replaceTransactionPartnerLabelOnSelectionOfRadioButton(RadioButton radioButton, String labelText) {
        radioButton.selectedProperty().addListener((observableValue, aBoolean, t1) ->
                transactionPartnerLabel.setText(labelText));
    }

    private void loadCategoriesOnSelectionOfRadioButton() {
        transactionTypeToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, radioButton) -> {
            if (radioButton.isSelected()) {
                categoryObservableList.clear();
                CategoryList categoryList = loadCategories(incomeRadioButton);
                categoryObservableList.addAll(categoryList.getCategories());
                categoryComboBox.setItems(categoryObservableList);
                categoryComboBox.setDisable(false);
            }
        });
    }

    public void setSelectedRecurringTransaction(RecurringTransactionOrder selectedRecurringTransaction) {
        this.selectedRecurringTransaction = selectedRecurringTransaction;
        headerTextLabel.setText("Edit Recurring Transaction Order");
        nextDateLabel.setText("Next Date");
        insertRecurringTransactionOrder();
    }

    private void setIntervalComboBox() {
        final ObservableList<Interval> intervalList
                = observableArrayList(List.of(Interval.values()));
        intervalComboBox.setItems(intervalList);
    }

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
