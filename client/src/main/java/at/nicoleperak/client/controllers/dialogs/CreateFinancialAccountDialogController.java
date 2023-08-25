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

    /**
     * Upon initialization, engages event filters for the dialog.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        engageEventFilters();
    }

    /**
     * Engages event filters for the dialog.
     */
    private void engageEventFilters() {
        validateUserInputsOnFinish();
    }


    /**
     * Sets up an event filter that gets called when the "Finish" button of the dialog is clicked.
     * Checks whether the user inputs into the dialog are valid and informs the user in case they are not.
     */
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
