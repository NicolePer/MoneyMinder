package at.nicoleperak.client;

import javafx.fxml.FXMLLoader;

public enum

FXMLLocation {
    CREATE_FINANCIAL_ACCOUNT_FORM("/fxml/create-financial-account-form.fxml"),
    EDIT_FINANCIAL_ACCOUNT_FORM("/fxml/edit-financial-account-form.fxml"),
    CREATE_FINANCIAL_ACCOUNT_TILE("/fxml/create-financial-account-tile.fxml"),
    FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN("/fxml/financial-accounts-overview-screen.fxml"),
    FINANCIAL_ACCOUNT_DETAILS_SCREEN("/fxml/financial-account-details-screen.fxml"),
    FINANCIAL_ACCOUNT_TILE("/fxml/financial-account-tile.fxml"),
    SIGN_UP_SCREEN("/fxml/sign-up-screen.fxml"),
    EXPANDED_TRANSACTION_TILE("/fxml/expanded-transaction-tile.fxml"),
    TRANSACTION_FORM("/fxml/transaction-form.fxml"),
    TRANSACTION_TILE("/fxml/transaction-tile.fxml"),
    WELCOME_SCREEN("/fxml/welcome-screen.fxml"),
    COLLABORATOR_BOX("/fxml/collaborator-box.fxml"),
    RECURRING_TRANSACTION_FORM("/fxml/recurring-transaction-form.fxml"),
    RECURRING_TRANSACTION_BOX("/fxml/recurring-transaction-order-box.fxml"),
    MONTHLY_GOAL_HEADER("/fxml/monthly-goal-header.fxml"),
    MONTHLY_GOAL_INFOBOX("/fxml/monthly-goal-infobox.fxml"),
    SET_MONTHLY_GOAL_FORM("/fxml/set-monthly-goal-form.fxml"),
    NAVIGATION_BAR("/fxml/navigation-bar.fxml"),
    EDIT_USER_ACCOUNT_FORM("/fxml/edit-user-account-form.fxml"),
    CHANGE_PASSWORD_FORM("/fxml/change-password-form.fxml"),
    MONEYMINDER_ERROR_ALERT("/fxml/moneyminder-error-alert.fxml"),
    MONEYMINDER_WARNING_ALERT("/fxml/moneyminder-warning-alert.fxml"),
    MONEYMINDER_SUCCESS_ALERT("/fxml/moneyminder-success-alert.fxml"),
    MONEYMINDER_CONFIRMATION_DIALOG("/fxml/moneyminder-confirmation-dialog.fxml"),
    PIE_CHART("/fxml/pie-chart-box.fxml"),
    BAR_CHART("/fxml/bar-chart-box.fxml");


    private final String location;

    FXMLLocation(String location) {
        this.location = location;
    }

    /**
     * Gets a new FXMLLoader configured with the location for loading FXML files.
     *
     * @return A new FXMLLoader instance with the specified location.
     */
    public FXMLLoader getLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(location));
        return loader;
    }

    public String getLocation() {
        return location;
    }
}
