package at.nicoleperak.client.controllers;

import at.nicoleperak.client.*;
import at.nicoleperak.client.factories.PieChartDataFactory;
import at.nicoleperak.shared.*;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

import static at.nicoleperak.client.Client.*;
import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.*;
import static at.nicoleperak.client.Format.*;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsOverviewScreen;
import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.factories.CollaboratorBoxFactory.buildCollaboratorBox;
import static at.nicoleperak.client.factories.TransactionFactory.buildTransaction;
import static at.nicoleperak.client.factories.TransactionTileFactory.buildTransactionTile;
import static at.nicoleperak.shared.Category.CategoryType.Expense;
import static at.nicoleperak.shared.Category.CategoryType.Income;

public class FinancialAccountDetailsScreenController implements Initializable {

    private static final Jsonb jsonb = JsonbBuilder.create();
    private FinancialAccount selectedFinancialAccount;
    private Category.CategoryType selectedType;
    private Category selectedCategory;
    private LocalDate selectedDateFrom;
    private LocalDate selectedDateTo;
    ObservableList<Category> categoryObservableList = FXCollections.observableArrayList();
    ObservableList<Category.CategoryType> typeObservableList = FXCollections.observableArrayList();

    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private Label balanceLabel;


    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private TitledPane collaboratorsTitledPane;

    @FXML
    private VBox collaboratorsPane;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ImageView downloadIcon;

    @FXML
    private Button resetFiltersButton;

    @FXML
    private Label financialAccountTitleLabel;

    @FXML
    private ImageView goBackButton;

    @FXML
    private ImageView addCollaboratorIcon;

    @FXML
    private TextField collaboratorEmailTextField;

    @FXML
    private DatePicker dateFromDatePicker;

    @FXML
    private DatePicker dateToDatePicker;

    @FXML
    private GridPane goalStatusBar;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuButton menuButton;

    @FXML
    private PieChart pieChart;

    @FXML
    private Label collaboratorAlertMessageLabel;

    @FXML
    private ToggleGroup pieChartToggleGroup;

    @FXML
    private RadioButton incomeRadioButton;

    @FXML
    private RadioButton expensesRadioButton;

    @FXML
    private ImageView searchIcon;

    @FXML
    private Accordion sideBarAccordion;

    @FXML
    private Button newTransactionButton;
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<Category.CategoryType> transactionTypeComboBox;

    @FXML
    private VBox transactionsPane;

    @FXML
    private Label userLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.selectedFinancialAccount = getSelectedFinancialAccount();
        loadSelectedFinancialAccountDetails();
        showTransactions(selectedFinancialAccount.getTransactions());
        setLabels();
        setComboBoxes();
        setPieChart();
        resetPieChartOnChangesOfPieChartToggleGroup();
        setBarChart(6);
        showCollaborators();
    }


    @FXML
    protected void onGoBackButtonClicked(MouseEvent event) {
        redirectToFinancialAccountsOverviewScreen();
    }

    @FXML
    void onNewTransactionButtonClicked(ActionEvent event) {
        showCreateTransactionDialog();
    }

    @FXML
    void onCategorySelected(ActionEvent event) {
        selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        filterTransactions();
    }

    @FXML
    void onTypeSelected(ActionEvent event) {
        categoryComboBox.getSelectionModel().clearSelection();
        selectedCategory = null;
        CategoryList filteredCategoryList = loadCategories(transactionTypeComboBox.getSelectionModel().getSelectedItem());
        categoryObservableList.setAll(filteredCategoryList.getCategories());
        selectedType = transactionTypeComboBox.getValue();
        filterTransactions();
    }

    @FXML
    void onDateSelected(ActionEvent event) {
        DatePicker datePicker = (DatePicker) event.getSource();
        try {
            Validation.assertDateIsInPast(datePicker.getValue());
            selectedDateFrom = Objects.requireNonNullElse(dateFromDatePicker.getValue(), LocalDate.MIN.plusDays(1));
            selectedDateTo = Objects.requireNonNullElse(dateToDatePicker.getValue(), LocalDate.MAX.minusDays(1));
            filterTransactions();
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onResetFiltersButtonClicked(ActionEvent event) {
        reloadFinancialAccountDetailsScreen();
    }

    @FXML
    void onEnterKeyPressedInSearchBar(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            searchTransactions();
        }
    }

    @FXML
    void onLogoutMenuItemClicked(ActionEvent event) {
        redirectToWelcomeScreen();
        setLoggedInUser(null);
        setUserCredentials(null);
    }

    @FXML
    void onSearchIconClicked(MouseEvent event) {
        searchTransactions();
    }

    @FXML
    void onAddCollaboratorIconClicked(MouseEvent event) {
        addCollaborator();
    }

    @FXML
    void onEnterKeyPressedInCollaboratorsEmailTextField(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            addCollaborator();
        }
    }

    private void addCollaborator() {
        collaboratorAlertMessageLabel.setText("");
        String collaboratorEmail = collaboratorEmailTextField.getText();
        try {
            Validation.assertEmailIsValid(collaboratorEmail);
            ServiceFunctions.post("financial_accounts/" + selectedFinancialAccount.getId() + "/collaborators", jsonb.toJson(collaboratorEmail), true);
            new Alert(Alert.AlertType.INFORMATION, "User successfully added as collaborator").showAndWait();
            //TODO style Alert
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            collaboratorAlertMessageLabel.setText(e.getMessage());
        }
    }

    private void searchTransactions() {
        String query = searchField.getText();
        List<Transaction> filteredTransactionList = new ArrayList<>();
        List<String> searchTerms = List.of(query.toLowerCase().split("[\\s.,+]+"));
        for (Transaction transaction : selectedFinancialAccount.getTransactions()) {
            String transactionNote = Objects.requireNonNullElse(transaction.getNote(), "");
            if (searchTerms.stream().anyMatch(transaction.getDescription().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transaction.getTransactionPartner().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transaction.getCategory().getTitle().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transactionNote.toLowerCase()::contains)) {
                filteredTransactionList.add(transaction);
            }
        }
        transactionsPane.getChildren().clear();
        showTransactions(filteredTransactionList);
    }

    private void filterTransactions() {
        List<Transaction> filteredTransactionList = selectedFinancialAccount.getTransactions().stream()
                .filter(transaction -> {
                    if (selectedType != null) {
                        return transaction.getCategory().getType().equals(selectedType);
                    } else return true;
                })
                .filter(transaction -> {
                    if (selectedCategory != null) {
                        return transaction.getCategory().getId().equals(selectedCategory.getId());
                    } else return true;
                })
                .filter(transaction -> {
                    if (selectedDateTo != null) {
                        return transaction.getDate().isBefore(selectedDateTo.plusDays(1));
                    } else return true;

                })
                .filter(transaction -> {
                    if (selectedDateFrom != null) {
                        return transaction.getDate().isAfter(selectedDateFrom.minusDays(1));
                    } else return true;
                })
                .toList();
        transactionsPane.getChildren().clear();
        showTransactions(filteredTransactionList);
    }

    private void loadSelectedFinancialAccountDetails() {
        try {
            String jsonResponse = ServiceFunctions.get("financial-accounts/" + selectedFinancialAccount.getId());
            selectedFinancialAccount = jsonb.fromJson(jsonResponse, FinancialAccount.class);
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showTransactions(List<Transaction> transactions) {
        try {
            for (Transaction transaction : transactions) {
                FXMLLoader transactionTileLoader = new FXMLLoader();
                transactionTileLoader.setLocation(getClass().getResource(TRANSACTION_TILE.getLocation()));
                Parent transactionTile = buildTransactionTile(transaction, transactionTileLoader, transactionsPane);
                transactionsPane.getChildren().add(transactionTile);
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            // TODO for later: Alertlabel?
        }
    }

    private void showCollaborators() {
        List<User> collaborators = selectedFinancialAccount.getCollaborators();
        Long ownerId = selectedFinancialAccount.getOwner().getId();
        boolean isOwner = getLoggedInUser().getId().equals(ownerId);
        if(!isOwner){
            collaboratorsPane.getChildren().clear();
        }
        try {
            for (User collaborator : collaborators) {
                if (isOwner && collaborator.getId().equals(ownerId)) {
                    continue;
                }
                FXMLLoader collaboratorBoxLoader = new FXMLLoader();
                collaboratorBoxLoader.setLocation(getClass().getResource(COLLABORATOR_BOX.getLocation()));
                GridPane collaboratorBox = buildCollaboratorBox(collaborator, selectedFinancialAccount.getId(), isOwner, collaboratorBoxLoader);
                collaboratorsPane.getChildren().add(collaboratorBox);
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            // TODO for later: Alertlabel?
        }
    }


    private void showCreateTransactionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(TRANSACTION_FORM.getLocation()));
            DialogPane createFinancialAccountDialogPane = loader.load();
            TransactionDialogController formController = loader.getController();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(createFinancialAccountDialogPane);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.FINISH) {
                Transaction transaction = buildTransaction(formController);
                try {
                    ServiceFunctions.post("financial_accounts/" + selectedFinancialAccount.getId() + "/transactions", jsonb.toJson(transaction), true);
                    reloadFinancialAccountDetailsScreen();
                } catch (ClientException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                }
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    public static void reloadFinancialAccountDetailsScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            getStage().setScene(scene);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }


    private CategoryList loadCategories() {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories");
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

    private CategoryList loadCategories(Category.CategoryType categoryType) {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories/" + categoryType.name());
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

    private void setLabels() {
        financialAccountTitleLabel.setText(selectedFinancialAccount.getTitle().toUpperCase());
        balanceLabel.setText(formatBalance(selectedFinancialAccount.getBalance()));
        userLabel.setText(getLoggedInUser().getUsername());
    }

    private void setComboBoxes() {
        CategoryList categoryList = loadCategories();
        categoryObservableList.addAll(categoryList.getCategories());
        typeObservableList.addAll(categoryList.getCategories()
                .stream()
                .map(Category::getType)
                .distinct().toList());
        categoryComboBox.setItems(categoryObservableList);
        transactionTypeComboBox.setItems(typeObservableList);
    }

    private void setPieChart() {
        Category.CategoryType categoryType = pieChartToggleGroup.getSelectedToggle()
                .equals(incomeRadioButton) ? Income : Expense;
        pieChart.setData(PieChartDataFactory.buildPieChartData(selectedFinancialAccount.getTransactions(), categoryType));
    }

    private void resetPieChartOnChangesOfPieChartToggleGroup() {
        pieChartToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> setPieChart());
    }

    private void setBarChart(int numberOfMonths) {
        ObservableList<String> months = FXCollections.observableArrayList();
        List<Transaction> transactionList = selectedFinancialAccount.getTransactions();
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        for (int i = 0; i < numberOfMonths; i++) {
            int currentMonthValue = LocalDate.now().getMonthValue() - i;
            List<Transaction> monthTransactionList = transactionList.stream()
                    .filter(transaction ->
                            transaction.getDate().getMonthValue() == currentMonthValue)
                    .toList();
            BigDecimal sumIncome = new BigDecimal(0);
            BigDecimal sumExpenses = new BigDecimal(0);
            months.add(Month.of(currentMonthValue).getDisplayName(TextStyle.FULL, Locale.US));
            for (Transaction transaction : monthTransactionList) {
                BigDecimal amount = transaction.getAmount().abs();
                if (transaction.getCategory().getType().equals(Income)) {
                    sumIncome = sumIncome.add(amount);
                } else {
                    sumExpenses = sumExpenses.add(amount);
                }
            }
            incomeSeries.getData().add(new XYChart.Data<>(Month.of(currentMonthValue).getDisplayName(TextStyle.FULL, Locale.US), sumIncome.doubleValue()));
            expenseSeries.getData().add(new XYChart.Data<>(Month.of(currentMonthValue).getDisplayName(TextStyle.FULL, Locale.US), sumExpenses.doubleValue()));
        }
        barChart.getData().addAll(incomeSeries, expenseSeries);
        FXCollections.reverse(months);
        categoryAxis.setCategories(months);
    }
}
