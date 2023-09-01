package at.nicoleperak.client.controllers.screens;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
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
import static at.nicoleperak.client.PopulationUtils.loadSelectedFinancialAccountDetails;
import static at.nicoleperak.client.PopulationUtils.populateCategories;
import static at.nicoleperak.client.Redirection.redirectToFinancialAccountsOverviewScreen;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.Validation.*;
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

    /**
     * Upon initialization, stores the selected financial account,
     * retrieves the details of the selected financial account,
     * inserts other FXML control parts and sets and populates them.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
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

    /**
     * Exports transactions to the computer of the user in the form of a CSV-File.
     */
    @FXML
    void onDownloadIconClicked() {
        try {
            writeTransactionsToCsv();
        } catch (ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Upon the user's selection of a Category in the category ComboBox, the selection is stored in the selectedCategory variable.
     * The displayed transactions are filtered according to the selection.
     */
    @FXML
    void onCategorySelected() {
        selectedCategory = categoryComboBox.getSelectionModel().getSelectedItem();
        filterAndSearchTransactions();
    }

    /**
     * Upon the user's selection of a CategoryType in the category type ComboBox,
     * the category ComboBox is cleared and freshly populated with categories
     * corresponding to the selected category type.
     * Then the filters are re-applied to the transactions.
     */
    @FXML
    void onTypeSelected() {
        categoryComboBox.getSelectionModel().clearSelection();
        selectedCategory = null;
        CategoryList filteredList = populateCategories(transactionTypeComboBox.getSelectionModel().getSelectedItem());
        categoryObservableList.setAll(filteredList.getCategories());
        selectedType = transactionTypeComboBox.getValue();
        filterAndSearchTransactions();
    }

    /**
     * Upon selection of a date picker by the user,
     * the user input is validated (no past dates allowed),
     * the values from the date pickers are stored in the selectedDateFrom and selectedDateTo variables
     * and the filters are applied to the displayed list of transactions.
     *
     * @param event A date picker is selected.
     * @ImplNote If any of the date pickers is null (no date has been selected for them yet),
     * the value stored in the variables is set to the earliest or latest date possible.
     * This ensures that there is always a valid date range on which the transaction filter can be applied.
     */
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

    /**
     * Applies filter and search query to the list of transactions from the selected financial account.
     * Displays the results to the user.
     */
    private void filterAndSearchTransactions() {
        List<Transaction> filteredTransactionList = filterTransactions();
        List<Transaction> resultList = searchTransactions(filteredTransactionList);
        transactionsPane.getChildren().clear();
        showTransactions(resultList);
    }

    /**
     * Sets labels of the control to display information from the selected financial account.
     */
    private void setLabels() {
        financialAccountTitleLabel.setText(selectedFinancialAccount.getTitle().toUpperCase());
        balanceLabel.setText(formatBalance(selectedFinancialAccount.getBalance()));
    }

    /**
     * Populates the category and category type ComboBoxes.
     */
    private void setComboBoxes() {
        CategoryList categoryList = populateCategories();
        categoryObservableList.addAll(categoryList.getCategories());
        typeObservableList.addAll(categoryList.getCategories()
                .stream()
                .map(Category::getType)
                .distinct().toList());
        categoryComboBox.setItems(categoryObservableList);
        transactionTypeComboBox.setItems(typeObservableList);
    }

    /**
     * Sets PieChart into the analysis tab.
     */
    private void setAnalysisTab() {
        analysisTab.setContent(getPieChartBox());
    }

    /**
     * Sets BarChart into the trends tab.
     */
    private void setTrendsTab() {
        trendsTab.setContent(getBarChartBox());
    }

    /**
     * Inserts the navigation bar control into screenPane.
     */
    private void insertNavigationBar() {
        try {
            FXMLLoader loader = NAVIGATION_BAR.getLoader();
            HBox navigationBarBox = loader.load();
            screenPane.getChildren().add(0, navigationBarBox);
        } catch (IOException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Inserts a dynamic amount of transaction tiles to display a list of transactions to the user.
     *
     * @param transactions List of transactions to be displayed to the user.
     */
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

    /**
     * Displays information about the monthly goal of the financial account to the user.
     */
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

    /**
     * Displays a dynamic amount of collaborator box controls to the user
     * based on the list of collaborators of the selected financial account.
     */
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

    /**
     * Displays a dynamic amount of recurring transaction box controls to the user
     * based on the list of recurring transaction orders of the selected financial account.
     */
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

    /**
     * Displays information about the selected financial account to the user.
     */
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

    /**
     * Shows the "Create Transaction" dialog to the user.
     * Upon finish, creates the transaction.
     */
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

    /**
     * Shows the "Edit Financial Account" dialog to the user.
     * Upon finish, updates the financial account.
     */
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

    /**
     * Shows the "Set Monthly Goal" dialog to the user.
     * Upon finish, creates the monthly goal.
     */
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

    /**
     * Shows the "Create Recurring Transaction Order" dialog to the user.
     * Upon finish, creates the recurring transaction order.
     */
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

    /**
     * Shows a FileChooser "Save" dialog to the user to choose a CSV File.
     * Initializes the file name displayed in the chooser with a valid default.
     *
     * @return File chosen by the user.
     */
    private File showFileChooserSaveDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName(convertToValidFileName(selectedFinancialAccount.getTitle().toLowerCase()) + "_transactions");
        //https://stackoverflow.com/questions/74859461/java93422850-catransaction-synchronize-called-within-transaction-when-a
        return fileChooser.showSaveDialog(downloadIcon.getScene().getWindow());
    }

    /**
     * Takes user inputs from the collaboratorEmailTextField.
     * Sends post-request to server to create new collaborator.
     */
    private void addCollaborator() {
        try {
            collaboratorAlertMessageLabel.setText("");
            String email = collaboratorEmailTextField.getText();
            postCollaborator(email);
        } catch (ClientException e) {
            collaboratorAlertMessageLabel.setText(e.getMessage());
        }
    }

    /**
     * Sends post-request to server to create a new transaction. Then reloads the screen.
     */
    private void postTransaction(Transaction transaction) throws ClientException {
        post("financial_accounts/"
                        + selectedFinancialAccount.getId()
                        + "/transactions"
                , jsonb.toJson(transaction)
                , true);
        reloadFinancialAccountDetailsScreen();
    }

    /**
     * Sends put-request to server to update a financial account.
     * Then shows success alert to the user and reloads the screen.
     *
     * @param editedAccount Edited financial account to be saved.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void putEditedFinancialAccount(FinancialAccount editedAccount) throws ClientException {
        ServiceFunctions.put("financial-accounts/"
                        + selectedFinancialAccount.getId()
                , jsonb.toJson(editedAccount));
        showMoneyMinderSuccessAlert("Changes saved");
        reloadFinancialAccountDetailsScreen();
    }

    /**
     * Sends post-request to server to create a financial goal.
     * Then shows success alert to the user and reloads the screen.
     *
     * @param goal FinancialGoal to be posted.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void postFinancialGoal(FinancialGoal goal) throws ClientException {
        post("financial_accounts/"
                        + selectedFinancialAccount.getId()
                        + "/financial-goals"
                , jsonb.toJson(goal), true);
        showMoneyMinderSuccessAlert("Financial Goal successfully set to " + goal.getGoalAmount() + " €");
        reloadFinancialAccountDetailsScreen();
    }

    /**
     * Sends post-request to server to create a recurring transactions order.
     * Then shows success alert to the user and reloads the screen.
     *
     * @param order RecurringTransactionsOrder to be posted.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
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

    /**
     * Sends post-request to server to create a collaborator.
     * Then shows success alert to the user and reloads the screen.
     *
     * @param email Email of user to become collaborator.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
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

    /**
     * Sends delete-request to server to remove the financial account,
     * following the user's confirmation.
     * Then displays success message to user and
     * redirects the user to the financial accounts overview screen.
     */
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

    /**
     * Exports transactions list of financial account to .CSV File on user's computer.
     * Displays a FileChooserSaveDialog to the user.
     * Then sets the order of the columns in the CSV File to be exported
     * and writes all transactions into the file selected by the user.
     * Displays success message to the user after completion.
     *
     * @throws ClientException If there is an issue regarding the writing process of the CSV File.
     */
    @SuppressWarnings("unchecked")
    private void writeTransactionsToCsv() throws ClientException {
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
            } catch (Exception e) {
                throw new ClientException("Could not create CSV File.");
            }
            showMoneyMinderSuccessAlert("Transactions successfully exported to CSV-File.");
        }
    }

    /**
     * Takes one or several search terms from the user input in the searchField,
     * splits the query into a list of search terms and
     * searches for any match of any search term
     * in the following attributes of each transaction in a list of transactions:
     * description, note, transaction partner, and category. Returns the resulting list.
     *
     * @param filteredList List of filtered transactions to be searched.
     * @return list of filtered and subsequently searched transactions.
     */
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


    /**
     * Applies several filters to the list of transactions from the selected financial account.
     * Filters by: selected CategoryType, selected Category, and the selected date range.
     *
     * @return a list of all resulting transactions from the selected financial account,
     * after all filters have been applied
     */
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

    /**
     * Loads the pieChartBox control.
     *
     * @return the loaded pieChartBox.
     */
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

    /**
     * Loads the barChartBox control.
     *
     * @return the loaded barChartBox.
     */
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

    /**
     * Loads the monthlyGoalVBox control.
     *
     * @return the loaded monthlyGoalVbox.
     */
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

    /**
     * Loads the monthlyGoalHeader control.
     *
     * @return The loaded monthlyGoalHeader control.
     */
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

    /**
     * Checks if the logged-in user is the owner of the selected financial account.
     *
     * @return True, if the user is the owner.
     */
    private boolean userIsOwner() {
        return selectedFinancialAccount.getOwner().getId().equals(getLoggedInUser().getId());
    }

    /**
     * Checks whether no monthly goal is set for the selected financial account.
     *
     * @return True, if no goal is set.
     */
    private boolean financialGoalIsNotSet() {
        return selectedFinancialAccount.getFinancialGoal() == null;
    }
}
