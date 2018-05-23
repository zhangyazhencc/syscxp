package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/5/23
 */
public class APICreateCloudHubEvent extends APIEvent {

    public APICreateCloudHubEvent(){}

    public APICreateCloudHubEvent(String apiId) {
        super(apiId);
    }

    private CloudHubInventory inventory;

    public CloudHubInventory getInventory() {
        return inventory;
    }

    public void setInventory(CloudHubInventory inventory) {
        this.inventory = inventory;
    }
}
