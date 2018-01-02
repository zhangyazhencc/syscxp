package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIDeleteVPNPriceEvent extends APIEvent {

    public APIDeleteVPNPriceEvent(){}

    public APIDeleteVPNPriceEvent(String apiId){super(apiId);}

    private List<ProductPriceUnitInventory> inventories;

    public List<ProductPriceUnitInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ProductPriceUnitInventory> inventories) {
        this.inventories = inventories;
    }
}
