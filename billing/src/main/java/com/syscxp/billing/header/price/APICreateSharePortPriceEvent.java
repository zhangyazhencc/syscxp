package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitVO;
import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APICreateSharePortPriceEvent extends APIEvent {
    public APICreateSharePortPriceEvent(){}
    public APICreateSharePortPriceEvent(String apiId) {
        super(apiId);
    }
    List<ProductPriceUnitVO> inventories;
    public List<ProductPriceUnitVO> getInventories() {
        return inventories;
    }

    public void setInventories(List<ProductPriceUnitVO> inventories) {
        this.inventories = inventories;
    }
}
