package at.nicoleperak.shared;

import java.util.List;

@SuppressWarnings("unused")
public class FinancialAccountsList {

    private List<FinancialAccount> financialAccounts;

    public FinancialAccountsList() {
    }

    public FinancialAccountsList(List<FinancialAccount> financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

    public List<FinancialAccount> getFinancialAccounts() {
        return financialAccounts;
    }

    public void setFinancialAccounts(List<FinancialAccount> financialAccounts) {
        this.financialAccounts = financialAccounts;
    }

}
