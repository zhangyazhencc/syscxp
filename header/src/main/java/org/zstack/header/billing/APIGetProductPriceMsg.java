package org.zstack.header.billing;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.List;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER, names = {"read"})
public class APIGetProductPriceMsg extends APISyncCallMessage{
    @APIParam(nonempty = true)
    private List<ProductPriceUnit> units;

    public List<ProductPriceUnit> getUnits() {
        return units;
    }

    public void setUnits(List<ProductPriceUnit> units) {
        this.units = units;
    }
}
