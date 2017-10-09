package com.syscxp.account.header.log;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
public class APIUpdateNoticeEvent extends APIEvent {
    private NoticeInventory inventory;

    public APIUpdateNoticeEvent() {
    }

    public APIUpdateNoticeEvent(String apiId) {
        super(apiId);
    }

    public NoticeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NoticeInventory inventory) {
        this.inventory = inventory;
    }
}