package at.nicoleperak.client.controllers;

import at.nicoleperak.client.ClientException;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        validateUserInputsOnFinish();
    }
    public void validateUserInputsOnFinish() {
        Button finish = (Button) dialogPane.lookupButton(ButtonType.FINISH);
        finish.addEventFilter(ActionEvent.ACTION, f -> {
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
}