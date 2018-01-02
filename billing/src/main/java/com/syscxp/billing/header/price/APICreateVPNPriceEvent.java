package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIEvent;

public class APICreateVPNPriceEvent extends APIEvent {

    public APICreateVPNPriceEvent(){}

    public APICreateVPNPriceEvent(String apiId){super(apiId);}

    private ProductPriceUnitInventory inventory;

    public ProductPriceUnitInventory getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceUnitInventory inventory) {
        this.inventory = inventory;
    }
}
