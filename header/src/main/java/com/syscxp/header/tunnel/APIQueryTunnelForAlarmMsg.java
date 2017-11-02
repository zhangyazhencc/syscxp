package com.syscxp.header.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Created by DCY on 2017-09-17
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
@InnerCredentialCheck
public class APIQueryTunnelForAlarmMsg extends APISyncCallMessage {

    private Integer limit = 1000;
    private Integer start = 0;
    @APIParam(required = false)
    private String accountUuid;
    @APIParam(required = false)
    private String productName;

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

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}
