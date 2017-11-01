package com.syscxp.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.query.QueryCondition;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@AutoQuery(replyClass = APIQueryTunnelDetailForAlarmReply.class, inventoryClass = TunnelForAlarmInventory.class)
@InnerCredentialCheck
public class APIQueryTunnelDetailForAlarmMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,maxLength = 32)
    private String tunnelUuid;

    public String getTunnelUuid() {
        return tunnelUuid;
    }

    public void setTunnelUuid(String tunnelUuid) {
        this.tunnelUuid = tunnelUuid;
    }
}
