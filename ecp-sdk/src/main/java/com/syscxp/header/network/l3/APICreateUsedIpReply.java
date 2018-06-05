package com.syscxp.header.network.l3;


import com.syscxp.header.message.APIReply;

public class APICreateUsedIpReply extends APIReply {
    private UsedIpInventory inventory;

    private String networkType;

    private boolean isPublic;

    private int vlan;

    public UsedIpInventory getInventory() {
        return inventory;
    }

    public void setInventory(UsedIpInventory inventory) {
        this.inventory = inventory;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getVlan() {
        return vlan;
    }

    public void setVlan(int vlan) {
        this.vlan = vlan;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }
}
