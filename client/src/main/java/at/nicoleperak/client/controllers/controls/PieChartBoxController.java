package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.FinancialAccount;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import static at.nicoleperak.client.factories.PieChartDataFactory.buildPieChartData;
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

    private void setPieChart() {
        Category.CategoryType categoryType = pieChartToggleGroup.getSelectedToggle()
                .equals(incomeRadioButton) ? INCOME : EXPENSE;
        pieChart.setData(buildPieChartData(selectedFinancialAccount.getTransactions(), categoryType));
    }

    private void resetPieChartOnChangesOfPieChartToggleGroup() {
        pieChartToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> setPieChart());
    }

    public void setSelectedFinancialAccount(FinancialAccount selectedFinancialAccount) {
        this.selectedFinancialAccount = selectedFinancialAccount;
        setPieChart();
        resetPieChartOnChangesOfPieChartToggleGroup();
    }
}
