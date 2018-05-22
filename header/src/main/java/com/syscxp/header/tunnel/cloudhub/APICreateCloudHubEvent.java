package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.message.APIEvent;

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
