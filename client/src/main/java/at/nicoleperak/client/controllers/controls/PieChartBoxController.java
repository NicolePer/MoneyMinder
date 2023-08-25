package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import static at.nicoleperak.client.factories.PieChartDataFactory.buildPieChartData;
import static at.nicoleperak.shared.Category.CategoryType;
import static at.nicoleperak.shared.Category.CategoryType.EXPENSE;
import static at.nicoleperak.shared.Category.CategoryType.INCOME;

public class PieChartBoxController {
    private FinancialAccount selectedFinancialAccount;
    @FXML
    private RadioButton incomeRadioButton;
    @FXML
    private PieChart pieChart;
    @FXML
    private ToggleGroup pieChartToggleGroup;

    /**
     * Sets the pie chart data based on the user's radio button selection (category type).
     */
    private void setPieChart() {
        CategoryType categoryType = pieChartToggleGroup.getSelectedToggle()
                .equals(incomeRadioButton) ? INCOME : EXPENSE;
        pieChart.setData(buildPieChartData(selectedFinancialAccount.getTransactions(), categoryType));
    }

    /**
     * Sets up a change listener on the radio button toggle group selection which
     * resets the pie chart.
     */
    private void listenForChangesOfPieChartToggleGroup() {
        pieChartToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                setPieChart());
    }

    /**
     * Sets the selected financial account.
     * Triggers the setting of the pie chart and the change listener on the toggle group.
     *
     * @param selectedFinancialAccount The financial account whose data is to be displayed on the pie chart.
     */
    public void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        this.selectedFinancialAccount = selectedFinancialAccount;
        setPieChart();
        listenForChangesOfPieChartToggleGroup();
    }
}
