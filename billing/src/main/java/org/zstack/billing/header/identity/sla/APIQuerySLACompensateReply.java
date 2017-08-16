package org.zstack.billing.header.identity.sla;

import org.zstack.billing.header.identity.receipt.ReceiptPostAddressInventory;
import org.zstack.header.query.APIQueryReply;

import java.util.List;

public class APIQuerySLACompensateReply extends APIQueryReply {

    private List<SLACompensateInventory> inventories;

    public List<SLACompensateInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<SLACompensateInventory> inventories) {
        this.inventories = inventories;
    }
}
