package at.nicoleperak.client;

import at.nicoleperak.shared.FinancialAccount;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class CreateFinancialAccountTileController {

    private static final Jsonb jsonb = JsonbBuilder.create();

    @FXML
    private GridPane createFinancialAccountTile;

    @FXML
    protected void onClickedOnCreateFinancialAccountTile(MouseEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/create-financial-account-form.fxml"));
        DialogPane createFinancialAccountDialogPane = loader.load();
        CreateFinancialAccountDialogController formController = loader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setDialogPane(createFinancialAccountDialogPane);
        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if(clickedButton.get() == ButtonType.FINISH){
            String title = formController.getFinancialAccountTitleField().getText();
            String description = formController.getFinancialAccountDescriptionField().getText();
            FinancialAccount financialAccount = new FinancialAccount(title, description);
            try {
               ServiceFunctions.post("financial-accounts", jsonb.toJson(financialAccount), true);
               reloadFinancialAccountsOverviewScreen();
            } catch (ClientException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage());
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reloadFinancialAccountsOverviewScreen() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/financial-accounts-overview-screen.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = (Stage) createFinancialAccountTile.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }
}