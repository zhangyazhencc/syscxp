package org.zstack.header.billing;

import org.zstack.header.billing.BillingConstant;
import org.zstack.header.billing.ProductPriceUnit;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.List;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER, names = {"read"})
public class APIGetProductPriceMsg extends APISyncCallMessage{
    @APIParam(nonempty = true)
    private List<ProductPriceUnit> units;

    @APIParam(numberRange = {1,Integer.MAX_VALUE})
    private int duration;

    @APIParam(emptyString = false)
    private ProductChargeModel productChargeModel;

    @APIParam
    private String accountUuid;

    public List<ProductPriceUnit> getUnits() {
        return units;
    }

    public void setUnits(List<ProductPriceUnit> units) {
        this.units = units;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ProductChargeModel getProductChargeModel() {
        return productChargeModel;
    }

    public void setProductChargeModel(ProductChargeModel productChargeModel) {
        this.productChargeModel = productChargeModel;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
