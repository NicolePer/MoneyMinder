package at.nicoleperak.client.factories;

import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PieChartDataFactory {

    public static ObservableList<PieChart.Data> buildPieChartData(List<Transaction> transactionsList, Category.CategoryType categoryType) {
        HashMap<Category, BigDecimal> categories = new HashMap<>();
        for (Transaction transaction : transactionsList) {
            Category category = transaction.getCategory();
            BigDecimal amount = transaction.getAmount().abs();
            if(category.getType().equals(categoryType)) {
                categories.put(category, categories.get(category) == null ? amount : categories.get(category).add(amount));
            }
        }
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<Category, BigDecimal> entry : categories.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey().getTitle(), entry.getValue().doubleValue()));
        }
        return pieChartData;
    }
}

