package org.zstack.account.header;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/8/9.
 */
public class APIChangeIndustryMsg extends APIMessage {
    @APIParam
    private String uuid;

    @APIParam
    private String newIndustry;

    public String getUuid() {
        return uuid;
    }

    public String getNewIndustry() {
        return newIndustry;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setNewIndustry(String newIndustry) {
        this.newIndustry = newIndustry;
    }
}
