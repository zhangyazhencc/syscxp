package com.syscxp.header.vpn.vpn;

import com.syscxp.header.message.APIEvent;
import com.syscxp.header.vpn.agent.CertInventory;

public class APIDownloadCertEvent extends APIEvent {
    CertInventory inventory;

    public APIDownloadCertEvent() {
    }

    public APIDownloadCertEvent(String apiId) {
        super(apiId);
    }

    public CertInventory getInventory() {
        return inventory;
    }

    public void setInventory(CertInventory inventory) {
        this.inventory = inventory;
    }
}
