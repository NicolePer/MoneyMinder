package at.nicoleperak.client.controllers.dialogs;

import at.nicoleperak.client.ClientException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;
import static javafx.event.ActionEvent.ACTION;
import static javafx.scene.control.ButtonType.FINISH;

public class CreateFinancialAccountDialogController implements Initializable {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextField financialAccountDescriptionField;

    @FXML
    private TextField financialAccountTitleField;

    @FXML
    private Label alertMessageLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
    }
    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(FINISH);
        finish.addEventFilter(ACTION, f -> {
            String title = financialAccountTitleField.getText();
            String description = financialAccountDescriptionField.getText();
            try {
                assertUserInputLengthIsValid(title, "title", 1, 255);
                assertUserInputLengthIsValid(description, "description", 0, 255);
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
                f.consume();
            }
        });
    }

    public TextField getFinancialAccountDescriptionField() {
        return financialAccountDescriptionField;
    }

    public TextField getFinancialAccountTitleField() {
        return financialAccountTitleField;
    }
}
