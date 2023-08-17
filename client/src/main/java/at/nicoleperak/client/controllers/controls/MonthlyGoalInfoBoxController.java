package at.nicoleperak.client.controllers.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class MonthlyGoalInfoBoxController {

    @FXML
    private Label currentExpensesLabel;

    @FXML
    private ImageView deleteMonthlyGoalIcon;

    @FXML
    private ImageView editMonthlyGoalIcon;

    @FXML
    private Label goalLabel;

    @FXML
    void onDeleteMonthlyGoalIconClicked(MouseEvent event) {

    }

    @FXML
    void onEditMonthlyGoalIconClicked(MouseEvent event) {

    }

    public Label getCurrentExpensesLabel() {
        return currentExpensesLabel;
    }

    public Label getGoalLabel() {
        return goalLabel;
    }

    public ImageView getDeleteMonthlyGoalIcon() {
        return deleteMonthlyGoalIcon;
    }

    public ImageView getEditMonthlyGoalIcon() {
        return editMonthlyGoalIcon;
    }
}
