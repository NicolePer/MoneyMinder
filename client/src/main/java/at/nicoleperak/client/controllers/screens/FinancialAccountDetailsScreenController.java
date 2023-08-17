package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.client.controllers.controls.MonthlyGoalHeaderController;
import at.nicoleperak.client.controllers.controls.MonthlyGoalInfoBoxController;
import at.nicoleperak.client.controllers.dialogs.EditFinancialAccountDialogController;
import at.nicoleperak.client.controllers.dialogs.RecurringTransactionDialogController;
import at.nicoleperak.client.controllers.dialogs.SetMonthlyGoalDialogController;
import at.nicoleperak.client.controllers.dialogs.TransactionDialogController;
import at.nicoleperak.client.factories.PieChartDataFactory;
import at.nicoleperak.shared.*;
import at.nicoleperak.shared.Category.CategoryType;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Client.*;
import static at.nicoleperak.client.FXMLLocation.*;
import static at.nicoleperak.client.Format.formatBalance;
import static at.nicoleperak.client.LoadingUtils.loadCategories;
import static at.nicoleperak.client.LoadingUtils.loadSelectedFinancialAccountDetails;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsOverviewScreen;
import static at.nicoleperak.client.Redirection.redirectToWelcomeScreen;
import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.ServiceFunctions.post;
import static at.nicoleperak.client.Validation.assertDateIsInPast;
import static at.nicoleperak.client.Validation.assertEmailIsValid;
import static at.nicoleperak.client.factories.CollaboratorBoxFactory.buildCollaboratorBox;
import static at.nicoleperak.client.factories.FinancialAccountFactory.buildFinancialAccount;
import static at.nicoleperak.client.factories.FinancialGoalFactory.buildFinancialGoal;
import static at.nicoleperak.client.factories.RecurringTransactionOrderBoxFactory.buildRecurringTransactionOrderBox;
import static at.nicoleperak.client.factories.RecurringTransactionOrderFactory.buildRecurringTransactionOrder;
import static at.nicoleperak.client.factories.TransactionFactory.buildTransaction;
import static at.nicoleperak.client.factories.TransactionTileFactory.buildTransactionTile;
import static at.nicoleperak.shared.Category.CategoryType.Expense;
import static at.nicoleperak.shared.Category.CategoryType.Income;
import static java.time.LocalDate.*;
import static java.time.format.TextStyle.FULL;
import static java.util.Locale.US;
import static java.util.Objects.requireNonNullElse;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.reverse;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import static javafx.scene.control.ButtonType.FINISH;
import static javafx.scene.input.KeyCode.ENTER;

public class FinancialAccountDetailsScreenController implements Initializable {

    ObservableList<Category> categoryObservableList = observableArrayList();
    ObservableList<CategoryType> typeObservableList = observableArrayList();
    private FinancialAccount selectedFinancialAccount;
    private CategoryType selectedType;
    private Category selectedCategory;
    private LocalDate selectedDateFrom;
    private LocalDate selectedDateTo;

    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private Label balanceLabel;

    @FXML
    private VBox recurringTransactionOrdersPane;

    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private VBox headerVBox;

    @FXML
    private Button editAccountButton;

    @FXML
    private VBox collaboratorsPane;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ImageView downloadIcon;

    @FXML
    private Label financialAccountTitleLabel;

    @FXML
    private TextField collaboratorEmailTextField;

    @FXML
    private DatePicker dateFromDatePicker;

    @FXML
    private DatePicker dateToDatePicker;

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
    private VBox monthlyGoalBox;

    @FXML
    private Button setGoalButton;
    @FXML
    private TextField searchField;

    @FXML
    private Label financialAccountInfoOwnerLabel;

    @FXML
    private Label financialAccountInfoDescriptionLabel;

    @FXML
    private Label financialAccountInfoTitleLabel;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ComboBox<CategoryType> transactionTypeComboBox;

    @FXML
    private VBox transactionsPane;

    @FXML
    private Label userLabel;

    public static void reloadFinancialAccountDetailsScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            getStage().setScene(scene);
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFinancialAccount = getSelectedFinancialAccount();
        selectedFinancialAccount = loadSelectedFinancialAccountDetails(selectedFinancialAccount);
        showTransactions(selectedFinancialAccount.getTransactions());
        setLabels();
        setComboBoxes();
        setPieChart();
        resetPieChartOnChangesOfPieChartToggleGroup();
        setBarChart(6);
        showCollaborators();
        showRecurringTransactionOrders();
        showMonthlyGoal();
        showInfo();
    }

    private void showInfo() {
        if (userIsOwner()) {
            editAccountButton.setVisible(true);
        }
        financialAccountInfoTitleLabel.setText(selectedFinancialAccount.getTitle());
        financialAccountInfoDescriptionLabel.setText(selectedFinancialAccount.getDescription());
        financialAccountInfoOwnerLabel.setText(selectedFinancialAccount.getOwner().getUsername() + " ("
                + selectedFinancialAccount.getOwner().getEmail() + ")");
    }

    @FXML
    void onEditAccountButtonClicked(ActionEvent event) {
        showEditFinancialAccountDialog();
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
        CategoryList filteredList = loadCategories(transactionTypeComboBox.getSelectionModel().getSelectedItem());
        categoryObservableList.setAll(filteredList.getCategories());
        selectedType = transactionTypeComboBox.getValue();
        filterTransactions();
    }

    @FXML
    void onDateSelected(ActionEvent event) {
        DatePicker datePicker = (DatePicker) event.getSource();
        try {
            assertDateIsInPast(datePicker.getValue());
            selectedDateFrom = requireNonNullElse(dateFromDatePicker.getValue(), MIN.plusDays(1));
            selectedDateTo = requireNonNullElse(dateToDatePicker.getValue(), MAX.minusDays(1));
            filterTransactions();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    @FXML
    void onResetFiltersButtonClicked(ActionEvent event) {
        reloadFinancialAccountDetailsScreen();
    }

    @FXML
    void onEnterKeyPressedInSearchBar(KeyEvent event) {
        if (event.getCode().equals(ENTER)) {
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
        if (event.getCode().equals(ENTER)) {
            addCollaborator();
        }
    }

    @FXML
    void onAddRecurringTransactionsOrderClicked(ActionEvent event) {
        showCreateRecurringTransactionOrderDialog();
    }

    @FXML
    void onSetGoalButtonClicked(ActionEvent event) {
        showSetMonthlyGoalDialog();
    }

    private void addCollaborator() {
        collaboratorAlertMessageLabel.setText("");
        String email = collaboratorEmailTextField.getText();
        try {
            assertEmailIsValid(email);
            post("financial_accounts/" + selectedFinancialAccount.getId() + "/collaborators", jsonb.toJson(email), true);
            new Alert(INFORMATION, "User successfully added as collaborator").showAndWait();
            //TODO style Alert
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            collaboratorAlertMessageLabel.setText(e.getMessage());
        }
    }

    private void searchTransactions() {
        String query = searchField.getText();
        List<Transaction> resultList = new ArrayList<>();
        List<String> searchTerms = List.of(query.toLowerCase().split("[\\s.,+]+"));
        for (Transaction transaction : selectedFinancialAccount.getTransactions()) {
            String transactionNote = requireNonNullElse(transaction.getNote(), "");
            if (searchTerms.stream().anyMatch(transaction.getDescription().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transaction.getTransactionPartner().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transaction.getCategory().getTitle().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transactionNote.toLowerCase()::contains)) {
                resultList.add(transaction);
            }
        }
        transactionsPane.getChildren().clear();
        showTransactions(resultList);
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

    private void showTransactions(List<Transaction> transactions) {
        try {
            for (Transaction transaction : transactions) {
                FXMLLoader loader = TRANSACTION_TILE.getLoader();
                Parent transactionTile = buildTransactionTile(transaction, loader, transactionsPane);
                transactionsPane.getChildren().add(transactionTile);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }

    }

    private void showCollaborators() {
        List<User> collaborators = selectedFinancialAccount.getCollaborators();
        Long ownerId = selectedFinancialAccount.getOwner().getId();
        boolean isOwner = getLoggedInUser().getId().equals(ownerId);
        if (!isOwner) {
            collaboratorsPane.getChildren().clear();
        }
        try {
            for (User collaborator : collaborators) {
                if (isOwner && collaborator.getId().equals(ownerId)) {
                    continue;
                }
                FXMLLoader loader = COLLABORATOR_BOX.getLoader();
                GridPane collaboratorBox = buildCollaboratorBox(collaborator, selectedFinancialAccount.getId(), isOwner, loader);
                collaboratorsPane.getChildren().add(collaboratorBox);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
            // TODO for later: Alertlabel?
        }
    }

    private void showRecurringTransactionOrders() {
        try {
            List<RecurringTransactionOrder> orders = selectedFinancialAccount.getRecurringTransactionOrders();
            for (RecurringTransactionOrder order : orders) {
                FXMLLoader loader = RECURRING_TRANSACTION_BOX.getLoader();
                GridPane ordersBox = buildRecurringTransactionOrderBox(order, selectedFinancialAccount.getId(), loader);
                recurringTransactionOrdersPane.getChildren().add(ordersBox);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showCreateRecurringTransactionOrderDialog() {
        try {
            FXMLLoader loader = RECURRING_TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            RecurringTransactionDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                RecurringTransactionOrder order = buildRecurringTransactionOrder(controller);
                try {
                    post("financial_accounts/" + selectedFinancialAccount.getId() + "/recurring-transactions", jsonb.toJson(order), true);
                    reloadFinancialAccountDetailsScreen();
                } catch (ClientException e) {
                    new Alert(ERROR, e.getMessage()).showAndWait();
                }
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showCreateTransactionDialog() {
        try {
            FXMLLoader loader = TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            TransactionDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                Transaction transaction = buildTransaction(controller, false);
                try {
                    post("financial_accounts/" + selectedFinancialAccount.getId() + "/transactions", jsonb.toJson(transaction), true);
                    reloadFinancialAccountDetailsScreen();
                } catch (ClientException e) {
                    new Alert(ERROR, e.getMessage()).showAndWait();
                }
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
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
        CategoryType categoryType = pieChartToggleGroup.getSelectedToggle()
                .equals(incomeRadioButton) ? Income : Expense;
        pieChart.setData(PieChartDataFactory.buildPieChartData(selectedFinancialAccount.getTransactions(), categoryType));
    }

    private void resetPieChartOnChangesOfPieChartToggleGroup() {
        pieChartToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> setPieChart());
    }

    private void setBarChart(int numberOfMonths) {
        ObservableList<String> months = observableArrayList();
        List<Transaction> transactionList = selectedFinancialAccount.getTransactions();
        Series<String, Number> incomeSeries = new Series<>();
        incomeSeries.setName("Income");
        Series<String, Number> expenseSeries = new Series<>();
        expenseSeries.setName("Expenses");
        for (int i = 0; i < numberOfMonths; i++) {
            int currentMonthValue = now().getMonthValue() - i;
            List<Transaction> monthTransactionList = transactionList.stream()
                    .filter(transaction ->
                            transaction.getDate().getMonthValue() == currentMonthValue)
                    .toList();
            BigDecimal sumIncome = new BigDecimal(0);
            BigDecimal sumExpenses = new BigDecimal(0);
            months.add(Month.of(currentMonthValue).getDisplayName(FULL, US));
            for (Transaction transaction : monthTransactionList) {
                BigDecimal amount = transaction.getAmount().abs();
                if (transaction.getCategory().getType().equals(Income)) {
                    sumIncome = sumIncome.add(amount);
                } else {
                    sumExpenses = sumExpenses.add(amount);
                }
            }
            incomeSeries.getData().add(new Data<>(Month.of(currentMonthValue).getDisplayName(FULL, US), sumIncome.doubleValue()));
            expenseSeries.getData().add(new Data<>(Month.of(currentMonthValue).getDisplayName(FULL, US), sumExpenses.doubleValue()));
        }
        barChart.getData().addAll(incomeSeries, expenseSeries);
        reverse(months);
        categoryAxis.setCategories(months);
    }

    private void showSetMonthlyGoalDialog() {
        try {
            FXMLLoader loader = SET_MONTHLY_GOAL_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            SetMonthlyGoalDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                FinancialGoal goal = buildFinancialGoal(controller);
                try {
                    post("financial_accounts/" + selectedFinancialAccount.getId() + "/financial-goals", jsonb.toJson(goal), true);
                    reloadFinancialAccountDetailsScreen();
                } catch (ClientException e) {
                    new Alert(ERROR, e.getMessage()).showAndWait();
                }
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }


    private void showMonthlyGoal() {
        if (financialGoalIsNotSet()) {
            if (userIsOwner()) {
                setGoalButton.setVisible(true);
            }
        } else {
            FinancialGoal goal = selectedFinancialAccount.getFinancialGoal();
            showMonthlyGoalInfobox(goal);
            showMonthlyGoalHeader(goal);
        }
    }

    private void showMonthlyGoalInfobox(FinancialGoal goal) {
        try {
            monthlyGoalBox.getChildren().clear();
            FXMLLoader loader = MONTHLY_GOAL_INFOBOX.getLoader();
            VBox monthlyGoalInfoBox = loader.load();
            MonthlyGoalInfoBoxController controller = loader.getController();
            controller.getGoalLabel().setText(formatBalance(goal.getGoalAmount()));
            controller.getCurrentExpensesLabel().setText(formatBalance(goal.getCurrentMonthsExpenses().abs()));
            controller.setGoal(goal);
            monthlyGoalBox.getChildren().add(monthlyGoalInfoBox);
            if (userIsOwner()) {
                controller.getDeleteMonthlyGoalIcon().setVisible(true);
                controller.getEditMonthlyGoalIcon().setVisible(true);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showMonthlyGoalHeader(FinancialGoal goal) {
        double currentMonthsExpenses = goal.getCurrentMonthsExpenses().abs().doubleValue();
        double goalAmount = goal.getGoalAmount().doubleValue();
        double divisor = currentMonthsExpenses / goalAmount;
        int goalStatusInPercent = (int) Math.round(divisor * 100);
        try {
            FXMLLoader loader = MONTHLY_GOAL_HEADER.getLoader();
            GridPane monthlyGoalHeader = loader.load();
            MonthlyGoalHeaderController controller = loader.getController();
            controller.getPercentageLabel().setText(goalStatusInPercent + " %");
            controller.getProgressBar().setProgress(divisor);
            controller.setProgressBarColor(divisor);
            headerVBox.getChildren().add(1, monthlyGoalHeader);
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private boolean userIsOwner() {
        return selectedFinancialAccount.getOwner().getId().equals(getLoggedInUser().getId());
    }

    private boolean financialGoalIsNotSet() {
        return selectedFinancialAccount.getFinancialGoal() == null;
    }

    private void showEditFinancialAccountDialog() {
        try {
            FXMLLoader loader = EDIT_FINANCIAL_ACCOUNT_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            EditFinancialAccountDialogController controller = loader.getController();
            controller.setFinancialAccount(selectedFinancialAccount);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                FinancialAccount editedAccount = buildFinancialAccount(controller);
                putEditedFinancialAccount(editedAccount);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void putEditedFinancialAccount(FinancialAccount editedAccount) {
        try {
            ServiceFunctions.put("financial-accounts/" + selectedFinancialAccount.getId(), jsonb.toJson(editedAccount));
            new Alert(INFORMATION, "Financial account successfully updated").showAndWait();
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }
}
