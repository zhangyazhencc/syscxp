package com.syscxp.tunnel.header.tunnel;

import com.syscxp.header.billing.OrderType;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.tunnel.header.switchs.SwitchPortType;
import com.syscxp.tunnel.header.switchs.SwitchPortVO;

/**
 * Create by DCY on 2017/9/28
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateInterfacePortMsg extends APIMessage {
    @APIParam(emptyString = false, maxLength = 32)
    private String accountUuid;
    @APIParam(emptyString = false, resourceType = InterfaceVO.class, checkAccount = true)
    private String uuid;
    @APIParam(emptyString = false, maxLength = 32, resourceType = SwitchPortVO.class)
    private String switchPortUuid;
    @APIParam
    private NetworkType networkType;
    @APIParam
    private boolean issue = false;

    public boolean isIssue() {
        return issue;
    }

    public void setIssue(boolean issue) {
        this.issue = issue;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getAccountUuid() {
        if (getSession().getType() == AccountType.SystemAdmin) {
            return accountUuid;
        } else {
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getSwitchPortUuid() {
        return switchPortUuid;
    }

    public void setSwitchPortUuid(String switchPortUuid) {
        this.switchPortUuid = switchPortUuid;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }
}

