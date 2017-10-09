package com.syscxp.header.billing;

import java.util.List;

public class ProductPriceInventory {

    private List<ProductPriceUnitInventory> productPriceInventories;

    private AccountBalanceInventory accountBalanceInventory;

    public List<ProductPriceUnitInventory> getProductPriceInventories() {
        return productPriceInventories;
    }

    public void setProductPriceInventories(List<ProductPriceUnitInventory> productPriceInventories) {
        this.productPriceInventories = productPriceInventories;
    }

    public AccountBalanceInventory getAccountBalanceInventory() {
        return accountBalanceInventory;
    }

    public void setAccountBalanceInventory(AccountBalanceInventory accountBalanceInventory) {
        this.accountBalanceInventory = accountBalanceInventory;
    }
}
