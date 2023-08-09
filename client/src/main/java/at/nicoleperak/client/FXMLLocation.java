package at.nicoleperak.client;
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
    WELCOME_SCREEN("/fxml/welcome-screen.fxml");

    private final String location;

    FXMLLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}