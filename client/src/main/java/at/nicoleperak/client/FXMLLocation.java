package at.nicoleperak.client;

import javafx.fxml.FXMLLoader;

public enum FXMLLocation {
    CREATE_FINANCIAL_ACCOUNT_FORM("/fxml/create-financial-account-form.fxml"),
    CREATE_FINANCIAL_ACCOUNT_TILE("/fxml/create-financial-account-tile.fxml"),
    FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN("/fxml/financial-accounts-overview-screen.fxml"),
    FINANCIAL_ACCOUNT_DETAILS_SCREEN("/fxml/financial-account-details-screen.fxml"),
    FINANCIAL_ACCOUNT_TILE("/fxml/financial-account-tile.fxml"),
    SIGN_UP_SCREEN("/fxml/sign-up-screen.fxml"),
    TRANSACTION_DETAILS_TILE("/fxml/transaction-details-tile.fxml"),
    TRANSACTION_FORM("/fxml/transaction-form.fxml"),
    TRANSACTION_TILE("/fxml/transaction-tile.fxml"),
    WELCOME_SCREEN("/fxml/welcome-screen.fxml"),
    COLLABORATOR_BOX("/fxml/collaborator-box.fxml"),
    RECURRING_TRANSACTION_FORM("/fxml/recurring-transaction-form.fxml"),
    RECURRING_TRANSACTION_BOX("/fxml/recurring-transaction-order-box.fxml");


    private final String location;

    FXMLLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public FXMLLoader getLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(location));
        return loader;
    }
}
