package org.zstack.billing.header.balance;

import org.zstack.billing.header.order.Category;
import org.zstack.header.billing.ProductType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

public class APICreateAccountDischargeMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam
    private ProductType productType;

    @APIParam
    private Category category;

    @APIParam(numberRange = {1,100})
    private int  disCharge;

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getDisCharge() {
        return disCharge;
    }

    public void setDisCharge(int disCharge) {
        this.disCharge = disCharge;
    }
}
