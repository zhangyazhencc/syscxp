package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitVO;
import com.syscxp.header.message.APIEvent;

public class APIUpdateBroadPriceEvent extends APIEvent {

    private ProductPriceUnitVO inventory;

    public APIUpdateBroadPriceEvent(){}
    public APIUpdateBroadPriceEvent(String apiId){
        super(apiId);
    }

    public ProductPriceUnitVO getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceUnitVO inventory) {
        this.inventory = inventory;
    }
}
