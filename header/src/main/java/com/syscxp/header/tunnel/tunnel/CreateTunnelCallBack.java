package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.billing.NotifyCallBackData;

/**
 * Create by DCY on 2017/11/16
 */
public class CreateTunnelCallBack extends NotifyCallBackData {

    private String interfaceAUuid;

    private String interfaceZUuid;

    private boolean newBuyInterfaceA;

    private boolean newBuyInterfaceZ;

    public String getInterfaceAUuid() {
        return interfaceAUuid;
    }

    public void setInterfaceAUuid(String interfaceAUuid) {
        this.interfaceAUuid = interfaceAUuid;
    }

    public String getInterfaceZUuid() {
        return interfaceZUuid;
    }

    public void setInterfaceZUuid(String interfaceZUuid) {
        this.interfaceZUuid = interfaceZUuid;
    }

    public boolean isNewBuyInterfaceA() {
        return newBuyInterfaceA;
    }

    public void setNewBuyInterfaceA(boolean newBuyInterfaceA) {
        this.newBuyInterfaceA = newBuyInterfaceA;
    }

    public boolean isNewBuyInterfaceZ() {
        return newBuyInterfaceZ;
    }

    public void setNewBuyInterfaceZ(boolean newBuyInterfaceZ) {
        this.newBuyInterfaceZ = newBuyInterfaceZ;
    }
}
