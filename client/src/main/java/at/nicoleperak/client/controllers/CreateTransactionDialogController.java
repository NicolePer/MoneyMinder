package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.CategoryList;
import at.nicoleperak.shared.FinancialAccount;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.util.ResourceBundle;

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

    private ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

        incomeRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                transactionPartnerLabel.setText("Source");
            }
        });

        expenseRadioButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                transactionPartnerLabel.setText("Recipient");

            }
        });
    }
}
