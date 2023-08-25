package at.nicoleperak.client.controllers.controls;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.client.controllers.dialogs.SetMonthlyGoalDialogController;
import at.nicoleperak.shared.FinancialGoal;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Optional;

import static at.nicoleperak.client.Client.getDialog;
import static at.nicoleperak.client.FXMLLocation.SET_MONTHLY_GOAL_FORM;
import static at.nicoleperak.client.ServiceFunctions.*;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderSuccessAlert;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderConfirmationDialogController.userHasConfirmedActionWhenAskedForConfirmation;
import static at.nicoleperak.client.controllers.screens.FinancialAccountDetailsScreenController.reloadFinancialAccountDetailsScreen;
import static at.nicoleperak.client.factories.FinancialGoalFactory.buildFinancialGoal;
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

    /**
     * Sends delete-request to server to remove the monthly goal from a financial account,
     * following the user's confirmation.
     * Then displays success message to user and reloads the screen.
     */
    private void removeMonthlyGoal() {
        if (userHasConfirmedActionWhenAskedForConfirmation(
                "Are you sure you want to delete your monthly goal?")) {
            try {
                delete("financial-goals/" + goal.getId());
                showMoneyMinderSuccessAlert("Monthly goal deleted");
                reloadFinancialAccountDetailsScreen();
            } catch (ClientException e) {
                showMoneyMinderErrorAlert(e.getMessage());
            }
        }
    }

    /**
     * Shows the "Set Monthly Goal" dialog to the user.
     * Upon finish, updates the monthly goal.
     */
    private void showSetFinancialGoalDialog() {
        try {
            FXMLLoader loader = SET_MONTHLY_GOAL_FORM.getLoader();
            DialogPane dialogPane = loader.load();
            SetMonthlyGoalDialogController controller = loader.getController();
            controller.setSelectedGoal(goal);
            Optional<ButtonType> result = getDialog(dialogPane).showAndWait();
            if (result.isPresent() && result.get() == FINISH) {
                updateMonthlyGoal(controller);
            }
        } catch (IOException | ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
    }

    /**
     * Takes user inputs from dialog and creates new FinancialGoal object.
     * Sends put-request to server. Then shows success message to user and reloads the screen.
     *
     * @param controller The FXML controller of the dialog that gathered the input from the user.
     * @throws ClientException If there is an issue regarding the server interaction.
     */
    private void updateMonthlyGoal(SetMonthlyGoalDialogController controller) throws ClientException {
        FinancialGoal newGoal = buildFinancialGoal(controller);
        put("financial-goals/" + goal.getId(), jsonb.toJson(newGoal));
        showMoneyMinderSuccessAlert("Monthly goal successfully set to " + newGoal.getGoalAmount() + " €");
        reloadFinancialAccountDetailsScreen();
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
