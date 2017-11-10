package com.syscxp.header.tunnel.node;

import com.syscxp.header.message.APIEvent;

/**
 * Created by wangwg on 2017/10/09
 */
public class APICreateNodeExtensionInfoEvent extends APIEvent {

    private String inventory;

    public APICreateNodeExtensionInfoEvent(){}

    public APICreateNodeExtensionInfoEvent(String apiId){super(apiId);}

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
}
