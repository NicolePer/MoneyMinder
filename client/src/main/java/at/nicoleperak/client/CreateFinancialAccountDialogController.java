package at.nicoleperak.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateFinancialAccountDialogController {

    @FXML
    private TextField financialAccountDescriptionField;

    @FXML
    private TextField financialAccountTitleField;

    public TextField getFinancialAccountDescriptionField() {
        return financialAccountDescriptionField;
    }

    public TextField getFinancialAccountTitleField() {
        return financialAccountTitleField;
    }
}
