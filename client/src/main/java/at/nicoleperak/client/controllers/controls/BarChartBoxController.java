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

    private Integer numberOfMonths = 6;
    private FinancialAccount selectedFinancialAccount;

    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private Spinner<Integer> numberOfMonthsSpinner;

    @SuppressWarnings("unchecked")
    private void setBarChart() {
        barChart.getData().clear();
        ObservableList<String> months = observableArrayList();
        List<Transaction> transactionList = selectedFinancialAccount.getTransactions();
        XYChart.Series<String, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<String, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");
        for (int i = 0; i < numberOfMonths; i++) {
            int currentMonthValue = now().getMonthValue() - i;
            if (currentMonthValue <= 0) {
                currentMonthValue += 12;
            }
            int finalCurrentMonthValue = currentMonthValue;
            List<Transaction> monthTransactionList = transactionList.stream()
                    .filter(transaction ->
                            transaction.getDate().getMonthValue() == finalCurrentMonthValue)
                    .toList();
            BigDecimal sumIncome = new BigDecimal(0);
            BigDecimal sumExpenses = new BigDecimal(0);
            months.add(Month.of(currentMonthValue).getDisplayName(FULL, US));
            for (Transaction transaction : monthTransactionList) {
                BigDecimal amount = transaction.getAmount().abs();
                if (transaction.getCategory().getType().equals(INCOME)) {
                    sumIncome = sumIncome.add(amount);
                } else {
                    sumExpenses = sumExpenses.add(amount);
                }
            }
            incomeSeries.getData().add(new XYChart.Data<>(Month.of(currentMonthValue).getDisplayName(FULL, US), sumIncome.doubleValue()));
            expenseSeries.getData().add(new XYChart.Data<>(Month.of(currentMonthValue).getDisplayName(FULL, US), sumExpenses.doubleValue()));
        }
        barChart.getData().addAll(incomeSeries, expenseSeries);
        reverse(months);
        categoryAxis.getCategories().clear();
        categoryAxis.setCategories(months);
    }

    private void setSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, numberOfMonths);
        numberOfMonthsSpinner.setValueFactory(valueFactory);
        listenForChangesOnSpinner();
    }

    private void listenForChangesOnSpinner() {
        numberOfMonthsSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberOfMonths = newValue;
            setBarChart();
        });
    }

    public void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        this.selectedFinancialAccount = selectedFinancialAccount;
        setBarChart();
        setSpinner();
    }
}
