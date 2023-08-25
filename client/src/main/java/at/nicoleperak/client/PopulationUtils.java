package at.nicoleperak.client;

import at.nicoleperak.shared.Category.CategoryType;
import at.nicoleperak.shared.CategoryList;
import at.nicoleperak.shared.FinancialAccount;
import at.nicoleperak.shared.User;
import javafx.scene.control.RadioButton;

import static at.nicoleperak.client.ServiceFunctions.jsonb;
import static at.nicoleperak.client.controllers.dialogs.MoneyMinderAlertController.showMoneyMinderErrorAlert;
import static at.nicoleperak.shared.Category.CategoryType.EXPENSE;
import static at.nicoleperak.shared.Category.CategoryType.INCOME;

public class PopulationUtils {

    public static FinancialAccount loadSelectedFinancialAccountDetails(FinancialAccount selectedFinancialAccount) {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("financial-accounts/" + selectedFinancialAccount.getId());
        } catch (ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
        return jsonb.fromJson(jsonResponse, FinancialAccount.class);
    }

    public static User loadLoggedInUser() throws ClientException {
        String jsonResponse = ServiceFunctions.get("users");
        return jsonb.fromJson(jsonResponse, User.class);
    }

    public static CategoryList populateCategories(RadioButton incomeRadioButton) {
        CategoryType categoryType = incomeRadioButton.isSelected() ? INCOME : EXPENSE;
        return populateCategories(categoryType);
    }

    public static CategoryList populateCategories() {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories");
        } catch (ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

    public static CategoryList populateCategories(CategoryType categoryType) {
        String jsonResponse = null;
        try {
            jsonResponse = ServiceFunctions.get("categories/" + categoryType.name());
        } catch (ClientException e) {
            showMoneyMinderErrorAlert(e.getMessage());
        }
        return jsonb.fromJson(jsonResponse, CategoryList.class);
    }

}
