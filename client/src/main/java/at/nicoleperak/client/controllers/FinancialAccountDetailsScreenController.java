package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.Transaction;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.*;
import static at.nicoleperak.client.Format.*;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;

public class FinancialAccountDetailsScreenController implements Initializable {

    private static final Jsonb jsonb = JsonbBuilder.create();

    private FinancialAccount selectedFinancialAccount;


    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private Label balanceLabel;

    @FXML
    private BarChart<?, ?> barChart;

    @FXML
    private ImageView downloadIcon;

    @FXML
    private Button filterButton;

    @FXML
    private Label financialAccountTitleLabel;

    @FXML
    private ImageView goBackButton;

    @FXML
    private GridPane goalStatusBar;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuButton menuButton;

    @FXML
    private PieChart pieCHart;

    @FXML
    private ImageView searchIcon;

    @FXML
    private Button newTransactionButton;

    @FXML
    private VBox transactionsPane;

    @FXML
    private Label userLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.selectedFinancialAccount = Client.getSelectedFinancialAccount();
        loadSelectedFinancialAccountDetails();
        showTransactions();
        setLabels();
    }

    @FXML
    protected void onGoBackButtonClicked(MouseEvent event) {
        redirectToFinancialAccountsOverviewScreen();
    }

    @FXML
    void onNewTransactionButtonClicked(ActionEvent event) {
        showCreateTransactionDialog();
    }

    private void loadSelectedFinancialAccountDetails() {
        try {
            String jsonResponse = ServiceFunctions.get("financial-accounts/" + selectedFinancialAccount.getId());
            selectedFinancialAccount = jsonb.fromJson(jsonResponse, FinancialAccount.class);
        } catch (ClientException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showTransactions() {
        try {
            for (Transaction transaction : selectedFinancialAccount.getTransactions()) {
                FXMLLoader transactionTileLoader = new FXMLLoader();
                transactionTileLoader.setLocation(getClass().getResource(TRANSACTION_TILE.getLocation()));
                Parent transactionTile = buildTransactionTile(transaction, transactionTileLoader);
                transactionsPane.getChildren().add(transactionTile);
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            //TODO for later: Alertlabel?
        }
    }

    private void setLabels() {
        financialAccountTitleLabel.setText(selectedFinancialAccount.getTitle().toUpperCase());
        balanceLabel.setText(formatBalance(selectedFinancialAccount.getBalance()));
        userLabel.setText(Client.getLoggedInUser().getUsername());
    }


    private void redirectToFinancialAccountsOverviewScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private static Parent buildTransactionTile(Transaction transaction, FXMLLoader loader) throws IOException {
        Parent transactionTile = loader.load();
        TransactionTileController controller = loader.getController();
        controller
                .getTransactionDateLabel()
                .setText(transaction.getDate().format(ofLocalizedDate(MEDIUM).withLocale(Locale.US)).toUpperCase());
        controller
                .getTransactionPartnerLabel()
                .setText(transaction.getTransactionPartner().toUpperCase());
        controller
                .getTransactionDescriptionLabel()
                .setText(transaction.getDescription().toUpperCase());
        controller.getTransactionAmountLabel()
                .setText(formatBalance(transaction.getAmount()));
        return transactionTile;
    }

    public void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        this.selectedFinancialAccount = selectedFinancialAccount;
    }

    private void showCreateTransactionDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(CREATE_TRANSACTION_FORM.getLocation()));
            DialogPane createFinancialAccountDialogPane = loader.load();
            CreateTransactionDialogController formController = loader.getController();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(createFinancialAccountDialogPane);
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.FINISH) {
                    LocalDate date = formController.getDatePicker().getValue();
                    String transactionPartner = formController.getTransactionPartnerField().getText();
                    String description = formController.getDescriptionField().getText();
                    Category category = (Category) formController.getCategoryComboBox().getSelectionModel().getSelectedItem();
                    String amountString = convertIntoParsableDecimal(formController.getAmountField().getText());
                    BigDecimal amount = new BigDecimal(formatAmount(amountString, category));
                    String note = formController.getNoteArea().getText();
                    Transaction transaction = new Transaction(null, description, amount, date, category, transactionPartner, note, false);
                    try {
                        ServiceFunctions.post("financial_accounts/" + selectedFinancialAccount.getId() + "/transactions", jsonb.toJson(transaction), true);
                        reloadFinancialAccountDetailsScreen();
                    } catch (ClientException e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
                    }
                }
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }


    private void reloadFinancialAccountDetailsScreen() {
        try {
            Scene scene = loadScene(FINANCIAL_ACCOUNT_DETAILS_SCREEN);
            Client.getStage().setScene(scene);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}
