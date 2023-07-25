package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.CategoryList;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class CreateTransactionDialogController implements Initializable {

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

    private final ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();

    //TODO validateInputs
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

    public RadioButton getExpenseRadioButton() {
        return expenseRadioButton;
    }

    public RadioButton getIncomeRadioButton() {
        return incomeRadioButton;
    }

    public TextArea getNoteArea() {
        return noteArea;
    }

    public TextField getTransactionPartnerField() {
        return transactionPartnerField;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
        loadCategoriesOnSelectionOfRadioButton();
        replaceTransactionPartnerLabelOnSelectionOfRadioButton(incomeRadioButton, "Source");
        replaceTransactionPartnerLabelOnSelectionOfRadioButton(expenseRadioButton, "Recipient");
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

    private void replaceTransactionPartnerLabelOnSelectionOfRadioButton(RadioButton radioButton, String labelText) {
        expenseRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                transactionPartnerLabel.setText(labelText);
            }
        });
    }

    private void loadCategoriesOnSelectionOfRadioButton() {
        transactionTypeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle radioButton) {
                if (radioButton.isSelected()) {
                    categoryObservableList.clear();
                    CategoryList categoryList = loadCategories();
                    categoryObservableList.addAll(categoryList.getCategories());
                    categoryComboBox.setItems(categoryObservableList);
                    categoryComboBox.setDisable(false);
                }
            }
        });
    }


    private CategoryList loadCategories() {
        String categoryType = incomeRadioButton.isSelected() ?
                Category.CategoryType.Income.name().toLowerCase() : Category.CategoryType.Expense.name().toLowerCase();
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories/" + categoryType);
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

}
