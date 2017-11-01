package com.syscxp.header.billing;

import com.syscxp.header.message.APIReply;

import java.math.BigDecimal;
import java.util.List;

public class APIGetProductPriceReply extends APIReply {

    private List<ProductPriceUnitInventory> productPriceInventories;
    private AccountBalanceInventory accountBalanceInventory;
    private BigDecimal originalPrice;
    private BigDecimal discountPrice;
    private BigDecimal mayPayTotal;
    private boolean payable;

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

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getMayPayTotal() {
        return mayPayTotal;
    }

    public void setMayPayTotal(BigDecimal mayPayTotal) {
        this.mayPayTotal = mayPayTotal;
    }

    public boolean isPayable() {
        return payable;
    }

    public void setPayable(boolean payable) {
        this.payable = payable;
    }
}