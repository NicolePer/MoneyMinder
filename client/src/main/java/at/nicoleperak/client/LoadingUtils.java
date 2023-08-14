package at.nicoleperak.client;

import at.nicoleperak.shared.Category.CategoryType;
import at.nicoleperak.shared.CategoryList;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;

import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.shared.Category.CategoryType.Expense;
import static at.nicoleperak.shared.Category.CategoryType.Income;
import static javafx.scene.control.Alert.AlertType.*;

public class LoadingUtils {

    public static FinancialAccount loadSelectedFinancialAccountDetails(FinancialAccount selectedFinancialAccount) {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("financial-accounts/" + selectedFinancialAccount.getId());
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, FinancialAccount.class);
    }

    public static User loadLoggedInUser() throws ClientException {
        String jsonResponse = ServiceFunctions.get("users");
        return jsonb.fromJson(jsonResponse, User.class);
    }

    public static CategoryList loadCategories(RadioButton incomeRadioButton) {
        CategoryType categoryType = incomeRadioButton.isSelected() ? Income : Expense;
        return loadCategories(categoryType);
    }

    public static CategoryList loadCategories() {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories");
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

    public static CategoryList loadCategories(CategoryType categoryType) {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories/" + categoryType.name());
        } catch (ClientException e) {
            new Alert(ERROR, e.getMessage()).showAndWait();
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }


}
