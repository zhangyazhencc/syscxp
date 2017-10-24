package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIEvent;

import java.util.List;

public class APIDeleteProductPriceUnitEvent extends APIEvent {
    private List<ProductPriceUnitInventory> inventoryList;

    private ProductPriceUnitInventory inventory;

    public APIDeleteProductPriceUnitEvent() {}
    public APIDeleteProductPriceUnitEvent(String apiId) {
        super(apiId);
    }

    public List<ProductPriceUnitInventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<ProductPriceUnitInventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public ProductPriceUnitInventory getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceUnitInventory inventory) {
        this.inventory = inventory;
    }
}
