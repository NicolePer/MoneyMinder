package at.nicoleperak.client;
public enum FXMLLocation {
    WELCOME_SCREEN("/fxml/welcome-screen.fxml"),
    SIGN_UP_SCREEN("/fxml/sign-up-screen.fxml"),
    FINANCIAL_ACCOUNTS_OVERVIEW_SCREEN("/fxml/financial-accounts-overview-screen.fxml"),
    FINANCIAL_ACCOUNT_DETAILS_SCREEN("/fxml/financial-account-details-screen.fxml"),
    CREATE_FINANCIAL_ACCOUNT_FORM("/fxml/create-financial-account-form.fxml"),
    CREATE_FINANCIAL_ACCOUNT_TILE("/fxml/create-financial-account-tile.fxml"),
    FINANCIAL_ACCOUNT_TILE("/fxml/financial-account-tile.fxml"),
    TRANSACTION_TILE("/fxml/transaction-tile.fxml"),
    CREATE_TRANSACTION_FORM("/fxml/create-transaction-form.fxml");

    private final String location;

    FXMLLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
