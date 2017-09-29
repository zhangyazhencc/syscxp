package org.zstack.vpn.header.vpn;

import org.zstack.header.message.APIEvent;

public class APIDownloadCertificateEvent extends APIEvent {
    CertificateInventory inventory;

    public APIDownloadCertificateEvent() {
    }

    public APIDownloadCertificateEvent(String apiId) {
        super(apiId);
    }

    public CertificateInventory getInventory() {
        return inventory;
    }

    public void setInventory(CertificateInventory inventory) {
        this.inventory = inventory;
    }
}
