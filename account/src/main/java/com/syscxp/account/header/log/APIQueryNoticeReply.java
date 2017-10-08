package com.syscxp.account.header.log;

import com.syscxp.header.query.APIQueryReply;
import com.syscxp.header.rest.RestResponse;

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
