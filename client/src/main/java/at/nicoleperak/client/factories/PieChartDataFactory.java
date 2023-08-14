package at.nicoleperak.client.factories;

import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import static at.nicoleperak.shared.Category.CategoryType;
import static javafx.scene.chart.PieChart.Data;

public class PieChartDataFactory {

    public static ObservableList<Data> buildPieChartData(List<Transaction> transactionsList, CategoryType categoryType) {
        HashMap<Category, BigDecimal> categories = new HashMap<>();
        for (Transaction transaction : transactionsList) {
            Category category = transaction.getCategory();
            BigDecimal amount = transaction.getAmount().abs();
            if (category.getType().equals(categoryType)) {
                categories.put(category, categories.get(category) == null ? amount : categories.get(category).add(amount));
            }
        }
        ObservableList<Data> pieChartData = FXCollections.observableArrayList();
        for (Entry<Category, BigDecimal> entry : categories.entrySet()) {
            pieChartData.add(new Data(entry.getKey().getTitle(), entry.getValue().doubleValue()));
        }
        return pieChartData;
    }
}

