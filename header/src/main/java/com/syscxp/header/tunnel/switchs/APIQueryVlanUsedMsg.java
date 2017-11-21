package com.syscxp.header.tunnel.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.query.AutoQuery;
import com.syscxp.header.tunnel.SwitchConstant;
import com.syscxp.header.tunnel.TunnelConstant;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SwitchConstant.ACTION_CATEGORY, names = {"read"}, adminOnly = true)
@AutoQuery(replyClass = APIQueryVlanUsedReply.class, inventoryClass = VlanUsedInventory.class)
public class APIQueryVlanUsedMsg extends APISyncCallMessage {
    @APIParam
    private String uuid;
    @APIParam
    private Integer start = 0;

    @APIParam
    private Integer limit;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
