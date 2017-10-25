package com.syscxp.billing.header.price;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.query.APIQueryReply;

import java.util.List;

public class APIQueryProductPriceUnitReply extends APIQueryReply{

    private List<ProductPriceUnitInventory> inventories;

    public List<ProductPriceUnitInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<ProductPriceUnitInventory> inventories) {
        this.inventories = inventories;
    }
}
