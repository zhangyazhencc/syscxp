package org.zstack.account.header;

import org.zstack.header.message.APIEvent;
import org.zstack.header.rest.RestResponse;

/**
 * Created by wangwg on 2017/08/14.
 */
public class APIUpdateUserEvent extends APIEvent {
    private UserInventory inventory;

    public UserInventory getInventory() {
        return inventory;
    }

    public void setInventory(UserInventory inventory) {
        this.inventory = inventory;
    }

    public APIUpdateUserEvent() {
    }

    public APIUpdateUserEvent(String apiId) {
        super(apiId);
    }


}
