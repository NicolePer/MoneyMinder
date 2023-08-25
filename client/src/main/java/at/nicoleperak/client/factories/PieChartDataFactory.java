package at.nicoleperak.client.factories;

import at.nicoleperak.shared.Category;
import at.nicoleperak.shared.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import static at.nicoleperak.shared.Category.CategoryType;
import static javafx.scene.chart.PieChart.Data;

public class PieChartDataFactory {

    /**
     * Constructs pie chart data based on a list of transactions and a category type.
     *
     * @param transactionsList A list of transactions to calculate data from.
     * @param categoryType     The category type to filter transactions.
     * @return An ObservableList of Data objects representing pie chart segments.
     */
    public static ObservableList<Data> buildPieChartData(List<Transaction> transactionsList, CategoryType categoryType) {
        HashMap<Category, BigDecimal> categories = new HashMap<>();
        BigDecimal totalAmount = new BigDecimal(0);
        for (Transaction transaction : transactionsList) {
            Category category = transaction.getCategory();
            BigDecimal amount = transaction.getAmount().abs();
            if (category.getType().equals(categoryType)) {
                categories.put(category, categories.get(category) == null ? amount : categories.get(category).add(amount));
                totalAmount = totalAmount.add(transaction.getAmount().abs());
            }
        }
        ObservableList<Data> pieChartData = FXCollections.observableArrayList();
        for (Entry<Category, BigDecimal> entry : categories.entrySet()) {
            double percentage = entry.getValue().doubleValue() / totalAmount.doubleValue() * 100 * 100;
            percentage = (double) Math.round(percentage) / 100;
            pieChartData.add(new Data(entry.getKey().getTitle() + " (" + percentage + " %)", entry.getValue().doubleValue()));
        }
        pieChartData.sort(Comparator.comparingDouble(Data::getPieValue));
        return pieChartData;
    }
}

