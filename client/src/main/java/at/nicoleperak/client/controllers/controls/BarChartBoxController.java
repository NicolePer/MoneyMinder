package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.math.BigDecimal;
import java.time.Month;
import java.util.List;

import static at.nicoleperak.shared.Category.CategoryType.INCOME;
import static java.time.LocalDate.now;
import static java.time.format.TextStyle.FULL;
import static java.util.Locale.US;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.collections.FXCollections.reverse;

public class BarChartBoxController {
    private final static int MIN_MONTHS_DISPLAYED = 1;
    private final static int MAX_MONTHS_DISPLAYED = 12;
    private Integer numberOfMonths = 6;
    private FinancialAccount selectedFinancialAccount;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private CategoryAxis categoryAxis;
    @FXML
    private Spinner<Integer> numberOfMonthsSpinner;

    /**
     * Sets the selected financial account.
     * Triggers the setting of the bar chart and spinner.
     *
     * @param selectedFinancialAccount The financial account whose data is to be displayed on the bar chart.
     */
    public void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        this.selectedFinancialAccount = selectedFinancialAccount;
        setBarChart();
        setSpinner();
    }

    /**
     * Sets up the bar chart with income and expense data for different months.
     */
    private void setBarChart() {
        barChart.getData().clear();
        ObservableList<String> months = getListOfMonths();
        List<Transaction> transactions = selectedFinancialAccount.getTransactions();
        XYChart.Series<String, Number> incomeSeries = createSeries("Income");
        XYChart.Series<String, Number> expenseSeries = createSeries("Expense");
        populateSeriesWithData(months, transactions, incomeSeries, expenseSeries);
        barChart.getData().addAll(incomeSeries, expenseSeries);
        setCategoryAxis(months);
    }

    /**
     * Creates a list of months for the X-axis categories.
     *
     * @return List of month names.
     */
    private ObservableList<String> getListOfMonths() {
        ObservableList<String> months = observableArrayList();
        for (int i = 0; i < numberOfMonths; i++) {
            int currentMonthValue = now().getMonthValue() - i;
            if (currentMonthValue <= 0) {
                currentMonthValue += 12;
            }
            months.add(Month.of(currentMonthValue).getDisplayName(FULL, US));
        }
        reverse(months);
        return months;
    }

    /**
     * Creates an XYChart series with the given name.
     *
     * @param name The name of the series.
     * @return An XYChart series with the specified name.
     */
    private XYChart.Series<String, Number> createSeries(String name) {
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName(name);
        return incomeSeries;
    }


    /**
     * Populates data for the income and expense series.
     *
     * @param months          List of month names.
     * @param transactionList List of transactions.
     * @param incomeSeries    The income XYChart series.
     * @param expenseSeries   The expense XYChart series.
     */
    private void populateSeriesWithData(ObservableList<String> months, List<Transaction> transactionList,
                                        XYChart.Series<String, Number> incomeSeries,
                                        XYChart.Series<String, Number> expenseSeries) {
        for (String month : months) {
            int monthIndex = Month.valueOf(month.toUpperCase()).getValue();
            List<Transaction> monthTransactionList = transactionList.stream()
                    .filter(transaction -> transaction.getDate().getMonthValue() == monthIndex)
                    .toList();
            BigDecimal sumIncome = new BigDecimal(0);
            BigDecimal sumExpenses = new BigDecimal(0);
            for (Transaction transaction : monthTransactionList) {
                BigDecimal amount = transaction.getAmount().abs();
                if (transaction.getCategory().getType().equals(INCOME)) {
                    sumIncome = sumIncome.add(amount);
                } else {
                    sumExpenses = sumExpenses.add(amount);
                }
            }
            incomeSeries.getData().add(new XYChart.Data<>(month, sumIncome.doubleValue()));
            expenseSeries.getData().add(new XYChart.Data<>(month, sumExpenses.doubleValue()));
        }
    }

    /**
     * Sets the X-axis categories using the provided list of months.
     *
     * @param months List of month names.
     */
    private void setCategoryAxis(ObservableList<String> months) {
        categoryAxis.getCategories().clear();
        categoryAxis.setCategories(months);
    }

    /**
     * Sets up the spinner for selecting the number of months and listens for changes.
     */
    private void setSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory
                .IntegerSpinnerValueFactory(MIN_MONTHS_DISPLAYED, MAX_MONTHS_DISPLAYED, numberOfMonths);
        numberOfMonthsSpinner.setValueFactory(valueFactory);
        listenForChangesOnSpinner();
    }

    /**
     * Sets up a change listener on the spinner value that
     * updates the numberOfMonths and
     * resets the bar chart.
     */
    private void listenForChangesOnSpinner() {
        numberOfMonthsSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberOfMonths = newValue;
            setBarChart();
        });
    }
}
