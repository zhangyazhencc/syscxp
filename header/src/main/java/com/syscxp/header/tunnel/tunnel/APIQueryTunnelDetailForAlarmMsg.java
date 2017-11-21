package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.TunnelConstant;

import java.util.List;

/**
 * Created by DCY on 2017-09-17
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@InnerCredentialCheck
public class APIQueryTunnelDetailForAlarmMsg extends APISyncCallMessage {
    @APIParam(emptyString = false,maxLength = 32)
    private List<String> tunnelUuidList;

    public List<String> getTunnelUuidList() {
        return tunnelUuidList;
    }

    public void setTunnelUuidList(List<String> tunnelUuidList) {
        this.tunnelUuidList = tunnelUuidList;
    }
}