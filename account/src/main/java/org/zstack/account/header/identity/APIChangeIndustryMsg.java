package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/9.
 */
public class APIChangeIndustryMsg extends APIMessage {
    @APIParam
    private String accountUuid;

    @APIParam
    private String accountName;

    @APIParam
    private String newIndustry;

    public String getAccountUuid() {
        return accountUuid;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getNewIndustry() {
        return newIndustry;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setNewIndustry(String newIndustry) {
        this.newIndustry = newIndustry;
    }
}
