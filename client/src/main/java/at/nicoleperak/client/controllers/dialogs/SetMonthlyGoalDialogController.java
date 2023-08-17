package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Format.convertIntoParsableDecimal;
import static at.nicoleperak.client.Validation.assertAmountIsBigDecimal;
import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static javafx.event.ActionEvent.ACTION;

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

    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(ButtonType.FINISH);
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

}
