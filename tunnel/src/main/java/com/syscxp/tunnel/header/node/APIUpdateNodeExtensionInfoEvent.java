package com.syscxp.tunnel.header.node;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * Created by wangwg on 2017/10/09
 */
public class APIUpdateNodeExtensionInfoEvent extends APIEvent {

    private String inventory;

    public APIUpdateNodeExtensionInfoEvent(String apiId) {
        super(apiId);
    }

    public APIUpdateNodeExtensionInfoEvent() {}

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
}
