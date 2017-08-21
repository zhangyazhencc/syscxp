package org.zstack.billing.header.identity.bill;

import org.zstack.header.message.APIReply;

import java.util.List;

public class APIGetMonetaryGroupByProductTypeReply  extends APIReply {

    private List<Monetary> inventory;

    public List<Monetary>  getInventory() {
        return inventory;
    }

    public void setInventory(List<Monetary>  inventory) {
        this.inventory = inventory;
    }

}
