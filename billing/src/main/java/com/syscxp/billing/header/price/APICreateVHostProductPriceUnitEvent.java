package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventory")
public class APICreateVHostProductPriceUnitEvent extends APIEvent{
    private ProductPriceUnitInventory inventory;

    public APICreateVHostProductPriceUnitEvent(){}


    public APICreateVHostProductPriceUnitEvent(String apiId) {
        super(apiId);
    }

    public ProductPriceUnitInventory getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceUnitInventory inventory) {
        this.inventory = inventory;
    }
}
