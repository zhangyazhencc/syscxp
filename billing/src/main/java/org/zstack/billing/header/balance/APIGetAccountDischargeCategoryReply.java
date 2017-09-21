package org.zstack.billing.header.balance;

import org.zstack.header.billing.ProductPriceUnitInventory;
import org.zstack.header.message.APIReply;

import java.util.List;

public class APIGetAccountDischargeCategoryReply extends APIReply {

    private List<ProductPriceUnitInventory> inventories;

    public List<ProductPriceUnitInventory> getInventories() { return inventories;}

    public void setInventories(List<ProductPriceUnitInventory> inventories) {
        this.inventories = inventories;
    }


}
