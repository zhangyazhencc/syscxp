package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APICreateTunnelProductPriceUnitEvent extends APIEvent {

    private List<ProductPriceUnitInventory> inventoryList;

    public APICreateTunnelProductPriceUnitEvent(){}

    public APICreateTunnelProductPriceUnitEvent(String apiId){super(apiId);}

    public List<ProductPriceUnitInventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<ProductPriceUnitInventory> inventoryList) {
        this.inventoryList = inventoryList;
    }
}
