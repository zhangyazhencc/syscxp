package org.zstack.header.billing;

import org.zstack.header.billing.ProductPriceInventory;
import org.zstack.header.message.APIReply;

public class APIGetProductPriceReply extends APIReply {

    private ProductPriceInventory inventory;

    public ProductPriceInventory getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceInventory inventory) {
        this.inventory = inventory;
    }
}