package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIEvent;

public class APIUpdateProductPriceUnitEvent extends APIEvent{
    private ProductPriceUnitInventory inventory;

    public APIUpdateProductPriceUnitEvent(){}

    public APIUpdateProductPriceUnitEvent(String apiId){
        super(apiId);
    }

    public ProductPriceUnitInventory getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceUnitInventory inventory) {
        this.inventory = inventory;
    }
}
