package com.syscxp.header.tunnel.monitor;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.rest.RestResponse;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-11-13.
 * @Description: 网络工具测速.
 */
public class APICreateNettoolRecordEvent extends APIEvent {
    private NettoolRecordInventory inventory;

    public APICreateNettoolRecordEvent(){};

    public APICreateNettoolRecordEvent(String apiId){super(apiId);}

    public NettoolRecordInventory getInventory() {
        return inventory;
    }

    public void setInventory(NettoolRecordInventory inventory) {
        this.inventory = inventory;
    }
}
