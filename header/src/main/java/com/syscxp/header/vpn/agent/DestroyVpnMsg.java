package com.syscxp.header.vpn.agent;


public class DestroyVpnMsg extends VpnMessage {
    private boolean deleteDb;

    public boolean isDeleteDb() {
        return deleteDb;
    }

    public void setDeleteDb(boolean deleteDb) {
        this.deleteDb = deleteDb;
    }
}
