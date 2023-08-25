package at.nicoleperak.client.controllers.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class MonthlyGoalHeaderController {
    @FXML
    private Label percentageLabel;
    @FXML
    private ProgressBar progressBar;

    /**
     * Adjusts the color of a progress bar based on the progress value.
     *
     * @param progressValue A value between 0 and 1 indicating the progress made.
     */
    public void setProgressBarColor(double progressValue) {
        if (progressValue >= 0.8) {
            progressBar.setStyle("-fx-accent: #f3de8a;");
        }
        if (progressValue >= 1) {
            progressBar.setStyle("-fx-accent: #D87D7E;");
        }
    }

    public Label getPercentageLabel() {
        return percentageLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}
