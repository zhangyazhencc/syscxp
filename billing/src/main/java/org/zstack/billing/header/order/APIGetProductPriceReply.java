package org.zstack.billing.header.order;

import org.zstack.header.message.APIReply;

import java.util.List;

public class APIGetProductPriceReply extends APIReply {

    private ProductPriceInventory inventory;

    public ProductPriceInventory getInventory() {
        return inventory;
    }

    public void setInventory(ProductPriceInventory inventory) {
        this.inventory = inventory;
    }
}