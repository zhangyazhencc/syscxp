package org.zstack.billing.header.order;

import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

import java.util.List;

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
