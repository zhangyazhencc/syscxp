package com.syscxp.header.tunnel.cloudhub;

import com.syscxp.header.message.APIEvent;

/**
 * Create by DCY on 2018/5/23
 */
public class APIRenewCloudHubEvent extends APIEvent {

    private CloudHubInventory inventory;

    public APIRenewCloudHubEvent(){}

    public APIRenewCloudHubEvent(String apiId){super(apiId);}

    public CloudHubInventory getInventory() {
        return inventory;
    }

    public void setInventory(CloudHubInventory inventory) {
        this.inventory = inventory;
    }
}
