package org.zstack.header.billing;

import org.zstack.header.billing.ProductPriceInventory;
import org.zstack.header.message.APIReply;

import java.math.BigDecimal;
import java.util.List;

public class APIGetProductPriceReply extends APIReply {

    private List<ProductPriceUnitInventory> productPriceInventories;

    private AccountBalanceInventory accountBalanceInventory;

    private BigDecimal originalPrice;
    private BigDecimal dischargePrice;

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

    public BigDecimal getDischargePrice() {
        return dischargePrice;
    }

    public void setDischargePrice(BigDecimal dischargePrice) {
        this.dischargePrice = dischargePrice;
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