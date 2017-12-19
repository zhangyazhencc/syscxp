package com.syscxp.billing.header.price;

import com.syscxp.header.message.APIReply;

import java.util.ArrayList;
import java.util.List;

public class APIGetBroadPriceListReply extends APIReply {

    List<PriceData> inventories;

    private long  count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<PriceData> getInventories() {
        return inventories;
    }

    public void setInventories(List<PriceData> inventories) {
        this.inventories = inventories;
    }
}
