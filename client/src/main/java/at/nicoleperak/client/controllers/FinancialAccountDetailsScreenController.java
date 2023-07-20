package at.nicoleperak.client.controllers;

import at.nicoleperak.client.Client;
import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.ServiceFunctions;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.Transaction;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Client.loadScene;
import static at.nicoleperak.client.FXMLLocation.FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN;
import static at.nicoleperak.client.FXMLLocation.TRANSACTION_TILE;
import static at.nicoleperak.client.Format.formatBalance;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;
import static java.time.format.FormatStyle.SHORT;

public class FinancialAccountDetailsScreenController implements Initializable {

    private static final Jsonb jsonb = JsonbBuilder.create();

    private FinancialAccount selectedFinancialAccount;

    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private BarChart<?, ?> barChart;

    @FXML
    private ImageView downloadIcon;

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
    private PieChart pieChart;

    @FXML
    private ImageView searchIcon;

    @FXML
    private Label userLabel;

    @FXML
    private VBox transactionsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.selectedFinancialAccount = Client.getSelectedFinancialAccount();
        loadSelectedFinancialAccountDetails();
        showTransactions();
    }

    @FXML
    protected void onGoBackButtonClicked(MouseEvent event) {
        redirectToFinancialAccountsOverviewScreen();
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
            //TODO: Alertlabel?
        }
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
        StringBuilder sb = new StringBuilder();
        if (transaction.getCategory().getType().ordinal() == 1) {
            sb.append("-");
        }
        sb.append(formatBalance(transaction.getAmount()));
        controller.getTransactionAmountLabel()
                .setText(sb.toString());
        return transactionTile;
    }

    public void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        this.selectedFinancialAccount = selectedFinancialAccount;
    }
}
