package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

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
