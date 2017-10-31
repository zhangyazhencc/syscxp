package com.syscxp.billing.header.price;

import com.syscxp.header.message.APIReply;

import java.util.List;

public class APIGetProductCategoryListReply extends APIReply {
    List<ProductDataDictionary> inventories;

    public List<ProductDataDictionary> getInventories() {
        return inventories;
    }

    public void setInventories(List<ProductDataDictionary> inventories) {
        this.inventories = inventories;
    }
}
