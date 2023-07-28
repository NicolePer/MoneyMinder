package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Category.CategoryType;
import at.nicoleperak.shared.CategoryList;
import at.nicoleperak.shared.Transaction;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Validation.*;
import static at.nicoleperak.shared.Category.CategoryType.Expense;
import static at.nicoleperak.shared.Category.CategoryType.Income;

public class TransactionDialogController implements Initializable {

    private static final Jsonb jsonb = JsonbBuilder.create();

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
    private ColumnConstraints labelColumnConstraints;

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

    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();


    public TextField getAmountField() {
        return amountField;
    }

    public ComboBox<?> getCategoryComboBox() {
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

    private Transaction selectedTransaction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
        loadCategoriesOnSelectionOfRadioButton();
        replaceTransactionPartnerLabelOnSelectionOfRadioButton(incomeRadioButton, "Source");
        replaceTransactionPartnerLabelOnSelectionOfRadioButton(expenseRadioButton, "Recipient");
    }

    private void insertTransactionDetails() {
        if (selectedTransaction.getCategory().getType().equals(Income)) {
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

    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(ButtonType.FINISH);
        finish.addEventFilter(ActionEvent.ACTION, f -> {
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

    @SuppressWarnings("unused")
    private void replaceTransactionPartnerLabelOnSelectionOfRadioButton(RadioButton radioButton, String labelText) {
        radioButton.selectedProperty().addListener((observableValue, aBoolean, t1) ->
                transactionPartnerLabel.setText(labelText));
    }

    private void loadCategoriesOnSelectionOfRadioButton() {
        transactionTypeToggleGroup.selectedToggleProperty().addListener((observableValue, toggle, radioButton) -> {
            if (radioButton.isSelected()) {
                categoryObservableList.clear();
                CategoryList categoryList = loadCategories();
                categoryObservableList.addAll(categoryList.getCategories());
                categoryComboBox.setItems(categoryObservableList);
                categoryComboBox.setDisable(false);
            }
        });
    }

    private CategoryList loadCategories() {
        CategoryType categoryType = incomeRadioButton.isSelected() ? Income : Expense;
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories/" + categoryType.name());
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

    public void setSelectedTransaction(Transaction selectedTransaction) {
        this.selectedTransaction = selectedTransaction;
        headerTextLabel.setText("Edit Transaction");
        insertTransactionDetails();
    }

}
