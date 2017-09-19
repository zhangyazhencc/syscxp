package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-09-07
 */
@Action(category = TunnelConstant.ACTION_CATEGORY)
public class APIUpdateNetworkMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = NetworkVO.class)
    private String uuid;

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;

    @APIParam(emptyString = false,required = false,maxLength = 128)
    private String name;

    @APIParam(emptyString = false,required = false,maxLength = 255)
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
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
