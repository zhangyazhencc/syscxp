package org.zstack.account.header.log;

import org.zstack.header.query.APIQueryReply;
import org.zstack.header.rest.RestResponse;

import java.util.List;

@RestResponse(allTo = "inventories")
public class APIQueryNoticeReply extends APIQueryReply {

    private List<NoticeInventory> inventories;

    public List<NoticeInventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<NoticeInventory> inventories) {
        this.inventories = inventories;
    }
}
