package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.client.Validation;
import at.nicoleperak.client.controllers.controls.BarChartBoxController;
import at.nicoleperak.client.controllers.controls.MonthlyGoalHeaderController;
import at.nicoleperak.client.controllers.controls.MonthlyGoalInfoBoxController;
import at.nicoleperak.client.controllers.controls.PieChartBoxController;
import at.nicoleperak.client.controllers.dialogs.EditFinancialAccountDialogController;
import at.nicoleperak.client.controllers.dialogs.RecurringTransactionDialogController;
import at.nicoleperak.client.controllers.dialogs.SetMonthlyGoalDialogController;
import at.nicoleperak.client.controllers.dialogs.TransactionDialogController;
import at.nicoleperak.shared.*;
import at.nicoleperak.shared.Category.CategoryType;
import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.commons.collections.comparators.FixedOrderComparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.time.LocalDate;
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
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.Validation.assertDateIsInPast;
import static at.nicoleperak.client.Validation.assertEmailIsValid;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderSuccessAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static at.nicoleperak.client.factories.CollaboratorBoxFactory.buildCollaboratorBox;
import static at.nicoleperak.client.factories.FinancialAccountFactory.buildFinancialAccount;
import static at.nicoleperak.client.factories.FinancialGoalFactory.buildFinancialGoal;
import static at.nicoleperak.client.factories.RecurringTransactionOrderBoxFactory.buildRecurringTransactionOrderBox;
import static at.nicoleperak.client.factories.RecurringTransactionOrderFactory.buildRecurringTransactionOrder;
import static at.nicoleperak.client.factories.TransactionFactory.buildTransaction;
import static at.nicoleperak.client.factories.TransactionTileFactory.buildTransactionTile;
import static java.time.LocalDate.MAX;
import static java.time.LocalDate.MIN;
import static java.util.Objects.requireNonNullElse;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.ButtonType.FINISH;
import static javafx.scene.input.KeyCode.ENTER;

public class FinancialAccountDetailsScreenController implements Initializable {

    private final ObservableList<Category> categoryObservableList = observableArrayList();
    private final ObservableList<CategoryType> typeObservableList = observableArrayList();
    private FinancialAccount selectedFinancialAccount;
    private CategoryType selectedType;
    private Category selectedCategory;
    private LocalDate selectedDateFrom;
    private LocalDate selectedDateTo;
    @FXML
    private VBox screenPane;
    @FXML
    private Tab analysisTab;
    @FXML
    private Label balanceLabel;
    @FXML
    private VBox recurringTransactionOrdersPane;
    @FXML
    private VBox headerVBox;
    @FXML
    private Button editAccountButton;
    @FXML
    private VBox collaboratorsPane;
    @FXML
    private Label financialAccountTitleLabel;
    @FXML
    private TextField collaboratorEmailTextField;
    @FXML
    private DatePicker dateFromDatePicker;
    @FXML
    private DatePicker dateToDatePicker;
    @FXML
    private Label collaboratorAlertMessageLabel;
    @FXML
    private VBox monthlyGoalBox;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private Button setGoalButton;
    @FXML
    private ImageView downloadIcon;
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
    private Tab trendsTab;
    @FXML
    private ComboBox<CategoryType> transactionTypeComboBox;
    @FXML
    private VBox transactionsPane;


    /**
     * Reloads the scene.
     */
    public static void reloadFinancialAccountDetailsScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            getStage().setScene(scene);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedFinancialAccount = getSelectedFinancialAccount();
        selectedFinancialAccount = loadSelectedFinancialAccountDetails(selectedFinancialAccount);
        insertNavigationBar();
        showTransactions(selectedFinancialAccount.getTransactions());
        showInfo();
        showCollaborators();
        showRecurringTransactionOrders();
        showMonthlyGoal();
        setLabels();
        setComboBoxes();
        setAnalysisTab();
        setTrendsTab();
    }

    @FXML
    void onEditAccountButtonClicked() {
        showEditFinancialAccountDialog();
    }

    @FXML
    void onNewTransactionButtonClicked() {
        showCreateTransactionDialog();
    }

    @FXML
    void onDownloadIconClicked() {
        try {
            writeTransactionsToCsv();
            showMoneyMinderSuccessAlert("Transactions successfully exported to CSV-File.");
        } catch (Exception e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    @FXML
    void onCategorySelected() {
        selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        filterAndSearchTransactions();
    }

    @FXML
    void onTypeSelected() {
        categoryComboBox.getSelectionModel().clearSelection();
        selectedCategory = null;
        CategoryList filteredList = loadCategories(transactionTypeComboBox.getSelectionModel().getSelectedItem());
        categoryObservableList.setAll(filteredList.getCategories());
        selectedType = transactionTypeComboBox.getValue();
        filterAndSearchTransactions();
    }

    @FXML
    void onDateSelected(ActionEvent event) {
        DatePicker datePicker = (DatePicker) event.getSource();
        try {
            assertDateIsInPast(datePicker.getValue());
            selectedDateFrom = requireNonNullElse(dateFromDatePicker.getValue(),
                    MIN.plusDays(1));
            selectedDateTo = requireNonNullElse(dateToDatePicker.getValue(),
                    MAX.minusDays(1));
            filterAndSearchTransactions();
        } catch (ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    @FXML
    void onResetFiltersButtonClicked() {
        reloadFinancialAccountDetailsScreen();
    }

    @FXML
    void onEnterKeyPressedInSearchBar(KeyEvent event) {
        if (event.getCode().equals(ENTER)) {
            filterAndSearchTransactions();
        }
    }

    @FXML
    void onSearchIconClicked() {
        filterAndSearchTransactions();
    }

    @FXML
    void onAddCollaboratorIconClicked() {
        addCollaborator();
    }

    @FXML
    void onEnterKeyPressedInCollaboratorsEmailTextField(KeyEvent event) {
        if (event.getCode().equals(ENTER)) {
            addCollaborator();
        }
    }

    @FXML
    void onAddRecurringTransactionsOrderClicked() {
        showCreateRecurringTransactionOrderDialog();
    }

    @FXML
    void onSetGoalButtonClicked() {
        showSetMonthlyGoalDialog();
    }

    @FXML
    void onDeleteAccountButtonClicked() {
        deleteFinancialAccount();
    }


    private void filterAndSearchTransactions() {
        List<Transaction> filteredTransactionList = filterTransactions();
        List<Transaction> resultList = searchTransactions(filteredTransactionList);
        transactionsPane.getChildren().clear();
        showTransactions(resultList);
    }


    private void setLabels() {
        financialAccountTitleLabel.setText(selectedFinancialAccount.getTitle().toUpperCase());
        balanceLabel.setText(formatBalance(selectedFinancialAccount.getBalance()));
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

    private void setAnalysisTab() {
        analysisTab.setContent(getPieChartBox());
    }

    private void setTrendsTab() {
        trendsTab.setContent(getBarChartBox());
    }

    private void insertNavigationBar() {
        try {
            FXMLLoader loader = NAVIGATION_BAR.getLoader();
            HBox navigationBarBox = loader.load();
            screenPane.getChildren().add(0, navigationBarBox);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void showTransactions(List<Transaction> transactions) {
        try {
            for (Transaction transaction : transactions) {
                FXMLLoader loader = TRANSACTION_TILE.getLoader();
                Parent transactionTile = buildTransactionTile(transaction, loader, transactionsPane);
                transactionsPane.getChildren().add(transactionTile);
            }
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }

    }

    private void showMonthlyGoal() {
        if (financialGoalIsNotSet()) {
            if (userIsOwner()) {
                setGoalButton.setVisible(true);
            }
        } else {
            FinancialGoal goal = selectedFinancialAccount.getFinancialGoal();
            monthlyGoalBox.getChildren().add(getMonthlyGoalVBox(goal));
            headerVBox.getChildren().add(1, getMonthlyGoalHeader(goal));
        }
    }

    private void showCollaborators() {
        List<User> collaborators = selectedFinancialAccount.getCollaborators();
        if (!userIsOwner()) {
            collaboratorsPane.getChildren().clear();
        }
        try {
            for (User collaborator : collaborators) {
                if (userIsOwner() && collaborator.getId().equals(selectedFinancialAccount.getOwner().getId())) {
                    continue;
                }
                FXMLLoader loader = COLLABORATOR_BOX.getLoader();
                GridPane collaboratorBox = buildCollaboratorBox(collaborator, selectedFinancialAccount.getId(), userIsOwner(), loader);
                collaboratorsPane.getChildren().add(collaboratorBox);
            }
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void showRecurringTransactionOrders() {
        try {
            List<RecurringTransactionOrder> orders = selectedFinancialAccount.getRecurringTransactionOrders();
            for (RecurringTransactionOrder order : orders) {
                FXMLLoader loader = RECURRING_TRANSACTION_BOX.getLoader();
                GridPane ordersBox = buildRecurringTransactionOrderBox(order, loader);
                recurringTransactionOrdersPane.getChildren().add(ordersBox);
            }
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }


    private void showInfo() {
        if (userIsOwner()) {
            editAccountButton.setVisible(true);
            deleteAccountButton.setVisible(true);
        }
        financialAccountInfoTitleLabel
                .setText(selectedFinancialAccount.getTitle());
        financialAccountInfoDescriptionLabel
                .setText(selectedFinancialAccount.getDescription());
        financialAccountInfoOwnerLabel
                .setText(selectedFinancialAccount.getOwner().getUsername()
                        + " (" + selectedFinancialAccount.getOwner().getEmail() + ")");
    }

    private void showCreateTransactionDialog() {
        try {
            FXMLLoader loader = TRANSACTION_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            TransactionDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                Transaction transaction = buildTransaction(controller, false);
                postTransaction(transaction);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
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
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private void showSetMonthlyGoalDialog() {
        try {
            FXMLLoader loader = SET_MONTHLY_GOAL_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            SetMonthlyGoalDialogController controller = loader.getController();
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                FinancialGoal goal = buildFinancialGoal(controller);
                postFinancialGoal(goal);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
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
                postRecurringTransactionsOrder(order);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    private File showFileChooserSaveDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(Validation.convertToValidFileName(selectedFinancialAccount.getTitle().toLowerCase()) + "_transactions");
        //https://stackoverflow.com/questions/74859461/java93422850-catransaction-synchronize-called-within-transaction-when-a
        return fileChooser.showSaveDialog(downloadIcon.getScene().getWindow());
    }

    private void addCollaborator() {
        try {
            collaboratorAlertMessageLabel.setText("");
            String email = collaboratorEmailTextField.getText();
            postCollaborator(email);
        } catch (ClientException e) {
            collaboratorAlertMessageLabel.setText(e.getMessage());
        }
    }

    private void postTransaction(Transaction transaction) throws ClientException {
        post("financial_accounts/"
                        + selectedFinancialAccount.getId()
                        + "/transactions"
                , jsonb.toJson(transaction)
                , true);
        reloadFinancialAccountDetailsScreen();
    }

    private void putEditedFinancialAccount(FinancialAccount editedAccount) throws ClientException {
        ServiceFunctions.put("financial-accounts/"
                        + selectedFinancialAccount.getId()
                , jsonb.toJson(editedAccount));
        showMoneyMinderSuccessAlert("Changes saved");
        reloadFinancialAccountDetailsScreen();
    }

    private void postFinancialGoal(FinancialGoal goal) throws ClientException {
        post("financial_accounts/"
                        + selectedFinancialAccount.getId()
                        + "/financial-goals"
                , jsonb.toJson(goal), true);
        showMoneyMinderSuccessAlert("Financial Goal successfully set to " + goal.getGoalAmount() + " €");
        reloadFinancialAccountDetailsScreen();
    }

    private void postRecurringTransactionsOrder(RecurringTransactionOrder order) throws ClientException {
        post("financial_accounts/"
                        + selectedFinancialAccount.getId()
                        + "/recurring-transactions"
                , jsonb.toJson(order)
                , true);
        showMoneyMinderSuccessAlert("Recurring transaction order \""
                + order.getDescription() + "\" successfully created");
        reloadFinancialAccountDetailsScreen();
    }

    private void postCollaborator(String email) throws ClientException {
        assertEmailIsValid(email);
        post("financial_accounts/"
                        + selectedFinancialAccount.getId()
                        + "/collaborators"
                , jsonb.toJson(email)
                , true);
        showMoneyMinderSuccessAlert("\"" + email + "\" successfully added as collaborator");
        reloadFinancialAccountDetailsScreen();
    }

    private void deleteFinancialAccount() {
        if (userHasConfirmedActionWhenAskedForConfirmation(
                "Are you sure you want to delete the financial account \"" + selectedFinancialAccount.getTitle() +
                        "\"?\nThe entire financial account will be permanently removed, including transactions, recurring transactions, and monthly goals. " +
                        "Collaborators' access is also revoked. This cannot be reverted.")) {
            try {
                delete("financial-accounts/" + selectedFinancialAccount.getId());
                showMoneyMinderSuccessAlert("Financial account \"" +
                        selectedFinancialAccount.getTitle() + "\" successfully deleted");
                redirectToFinancialAccountsOverviewScreen();
            } catch (ClientException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void writeTransactionsToCsv() throws Exception {
        File selectedFile = showFileChooserSaveDialog();
        if (selectedFile != null) {
            List<Transaction> transactions = selectedFinancialAccount.getTransactions();
            HeaderColumnNameMappingStrategy<Transaction> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(Transaction.class);
            String[] columns = {"DATE", "DESCRIPTION", "TRANSACTION PARTNER", "AMOUNT", "CATEGORY", "NOTE", "ADDED AUTOMATICALLY"};
            strategy.setColumnOrderOnWrite(new FixedOrderComparator(columns));
            try (Writer writer = new FileWriter(selectedFile.toPath().toString())) {
                StatefulBeanToCsv<Transaction> sbc = new StatefulBeanToCsvBuilder<Transaction>(writer)
                        .withQuotechar('\"')
                        .withMappingStrategy(strategy)
                        .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                        .build();
                sbc.write(transactions);
            }
        }
    }

    private List<Transaction> searchTransactions(List<Transaction> filteredList) {
        String query = searchField.getText();
        List<String> searchTerms = List.of(query.toLowerCase().split("[\\s.,+]+"));
        List<Transaction> resultList = new ArrayList<>();
        for (Transaction transaction : filteredList) {
            String transactionNote = requireNonNullElse(transaction.getNote(), "");
            if (searchTerms.stream().anyMatch(transaction.getDescription().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transaction.getTransactionPartner().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transaction.getCategory().getTitle().toLowerCase()::contains) ||
                    searchTerms.stream().anyMatch(transactionNote.toLowerCase()::contains)) {
                resultList.add(transaction);
            }
        }
        return resultList;
    }

    private List<Transaction> filterTransactions() {
        return selectedFinancialAccount.getTransactions().stream()
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
    }

    private VBox getPieChartBox() {
        try {
            FXMLLoader loader = PIE_CHART.getLoader();
            VBox pieChartBox = loader.load();
            PieChartBoxController controller = loader.getController();
            controller.setSelectedFinancialAccount(selectedFinancialAccount);
            return pieChartBox;
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
            return null;
        }
    }

    private VBox getBarChartBox() {
        try {
            FXMLLoader loader = BAR_CHART.getLoader();
            VBox barChartBox = loader.load();
            BarChartBoxController controller = loader.getController();
            controller.setSelectedFinancialAccount(selectedFinancialAccount);
            return barChartBox;
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
            return null;
        }
    }

    private VBox getMonthlyGoalVBox(FinancialGoal goal) {
        try {
            monthlyGoalBox.getChildren().clear();
            FXMLLoader loader = MONTHLY_GOAL_INFOBOX.getLoader();
            VBox monthlyGoalInfoBox = loader.load();
            MonthlyGoalInfoBoxController controller = loader.getController();
            controller.getGoalLabel().setText(formatBalance(goal.getGoalAmount()));
            controller.getCurrentExpensesLabel().setText(formatBalance(goal.getCurrentMonthsExpenses().abs()));
            controller.setGoal(goal);
            if (userIsOwner()) {
                controller.getDeleteMonthlyGoalIcon().setVisible(true);
                controller.getEditMonthlyGoalIcon().setVisible(true);
            }
            return monthlyGoalInfoBox;
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
            return null;
        }
    }

    private GridPane getMonthlyGoalHeader(FinancialGoal goal) {
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
            return monthlyGoalHeader;
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
            return null;
        }
    }

    private boolean userIsOwner() {
        return selectedFinancialAccount.getOwner().getId().equals(getLoggedInUser().getId());
    }

    private boolean financialGoalIsNotSet() {
        return selectedFinancialAccount.getFinancialGoal() == null;
    }
}
