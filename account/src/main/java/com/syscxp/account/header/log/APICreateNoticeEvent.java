package com.syscxp.account.header.log;

import com.syscxp.header.message.APIEvent;

public class APICreateNoticeEvent extends APIEvent {
    private NoticeInventory inventory;

    public APICreateNoticeEvent() {
        super(null);
    }

    public APICreateNoticeEvent(String apiId) {
        super(apiId);
    }

    public NoticeInventory getInventory() {
        return inventory;
    }

    public void setInventory(NoticeInventory inventory) {
        this.inventory = inventory;
    }
}
