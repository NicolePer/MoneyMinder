package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import at.nicoleperak.shared.FinancialGoal;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Validation.assertAmountIsBigDecimal;
import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static javafx.event.ActionEvent.ACTION;
import static javafx.scene.control.ButtonType.FINISH;

public class SetMonthlyGoalDialogController implements Initializable {

    @FXML
    private Label alertMessageLabel;

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextField goalTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
    }

    public TextField getGoalTextField() {
        return goalTextField;
    }

    /**
     * Sets up an event filter that will be executed when the "Finish" button of the dialog is clicked.
     * Checks whether the user inputs into the dialog are valid and informs the user in case they are not.
     */
    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(FINISH);
        finish.addEventFilter(ACTION, f -> {
            String amountString = convertIntoParsableDecimal(goalTextField.getText());
            try {
                assertUserInputLengthIsValid(goalTextField.getText(), "amount", 1, 255);
                assertAmountIsBigDecimal(amountString);
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
                f.consume();
            }
        });
    }

    public void setSelectedGoal(FinancialGoal goal) {
        goalTextField.setText(goal.getGoalAmount().toString());
    }

}
