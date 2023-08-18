package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.SetMonthlyGoalDialogController;
import at.nicoleperak.shared.FinancialGoal;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.SET_MONTHLY_GOAL_FORM;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.FinancialGoalFactory.buildFinancialGoal;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.ButtonType.FINISH;

public class MonthlyGoalInfoBoxController {

    private FinancialGoal goal;

    @FXML
    private Label currentExpensesLabel;

    @FXML
    private ImageView deleteMonthlyGoalIcon;

    @FXML
    private ImageView editMonthlyGoalIcon;

    @FXML
    private Label goalLabel;

    @FXML
    void onDeleteMonthlyGoalIconClicked() {
        removeMonthlyGoal();
    }

    @FXML
    void onEditMonthlyGoalIconClicked() {
        showSetFinancialGoalDialog();
    }

    private void removeMonthlyGoal() {
        try {
            delete("financial-goals/" + goal.getId());
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void showSetFinancialGoalDialog() {
        try {
            FXMLLoader loader = SET_MONTHLY_GOAL_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            SetMonthlyGoalDialogController controller = loader.getController();
            controller.setSelectedGoal(goal);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                FinancialGoal newGoal = buildFinancialGoal(controller);
                putFinancialGoal(newGoal);
            }
        } catch (IOException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
    }

    private void putFinancialGoal(FinancialGoal newGoal) {
        try {
            put("financial-goals/" + goal.getId(), jsonb.toJson(newGoal));
            reloadFinancialAccountDetailsScreen();
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
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

    public void setGoal(FinancialGoal goal) {
        this.goal = goal;
    }
}
