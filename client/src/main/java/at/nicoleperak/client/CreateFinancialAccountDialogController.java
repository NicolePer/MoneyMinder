package at.nicoleperak.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

import static at.nicoleperak.client.Validation.assertUserInputLengthIsValid;

public class CreateFinancialAccountDialogController implements Initializable {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private TextField financialAccountDescriptionField;

    @FXML
    private TextField financialAccountTitleField;

    @FXML
    private Label alertMessageLabel;


    public TextField getFinancialAccountDescriptionField() {
        return financialAccountDescriptionField;
    }

    public TextField getFinancialAccountTitleField() {
        return financialAccountTitleField;
    }

    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(ButtonType.FINISH);
        finish.addEventFilter(ActionEvent.ACTION, f -> {
            try {
                assertUserInputLengthIsValid(financialAccountTitleField.getText(), "title", 1, 255);
                assertUserInputLengthIsValid(financialAccountDescriptionField.getText(), "description", 0, 255);
            } catch (ClientException e) {
                alertMessageLabel.setText(e.getMessage());
                f.consume();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
    }
}
