package org.zstack.account.header.log;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

@RestResponse(allTo = "inventory")
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
