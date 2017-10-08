package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.ProductPriceUnitInventory;
import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetAccountDischargeCategoryReply extends APIReply {

    private List<ProductPriceUnitInventory> inventories;

    public List<ProductPriceUnitInventory> getInventories() { return inventories;}

    public void setInventories(List<ProductPriceUnitInventory> inventories) {
        this.inventories = inventories;
    }


}
