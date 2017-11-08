package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnit;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

import java.sql.Timestamp;
import java.util.List;

public class APIGetModifyProductPriceDiffMsg extends APISyncCallMessage {
    @APIParam(nonempty = true)
    private List<ProductPriceUnit> units;

    @APIParam
    private String productUuid;

    @APIParam
    private Timestamp expiredTime;

    public List<ProductPriceUnit> getUnits() {
        return units;
    }

    public void setUnits(List<ProductPriceUnit> units) {
        this.units = units;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }

    public Timestamp getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(Timestamp expiredTime) {
        this.expiredTime = expiredTime;
    }
}
