package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;

/**
 * Created by DCY on 2017-09-11
 */
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIListSwitchPortByTypeMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, maxLength = 32)
    private String accountUuid;

    @APIParam(emptyString = false, resourceType = EndpointVO.class)
    private String uuid;
    @APIParam
    private String type;
    @APIParam(required = false, numberRange = {0, 200})
    private Integer limit;
    @APIParam(required = false, numberRange = {0, 200})
    private Integer start;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
