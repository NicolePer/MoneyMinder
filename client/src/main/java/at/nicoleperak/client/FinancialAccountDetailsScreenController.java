package at.nicoleperak.client;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class FinancialAccountDetailsScreenController {

    @FXML
    private MenuItem accountSettingsMenuItem;

    @FXML
    private BarChart<?, ?> barChart;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private MenuItem logoutMenuItem;

    @FXML
    private MenuButton menuButton;

    @FXML
    private PieChart pieCHart;

    @FXML
    private Label userLabel;

}
