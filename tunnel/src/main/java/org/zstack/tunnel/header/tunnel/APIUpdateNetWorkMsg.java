package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-09-07
 */
public class APIUpdateNetWorkMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = NetWorkVO.class)
    private String uuid;

    @APIParam(required = false,maxLength = 32)
    private String accountUuid;

    @APIParam(required = false,maxLength = 128)
    private String name;

    @APIParam(required = false,maxLength = 255)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
