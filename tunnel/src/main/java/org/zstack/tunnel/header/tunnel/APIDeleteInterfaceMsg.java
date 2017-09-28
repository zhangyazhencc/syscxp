package org.zstack.tunnel.header.tunnel;

import org.zstack.header.identity.AccountType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-09-14
 */
public class APIDeleteInterfaceMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = InterfaceVO.class)
    private String uuid;

    @APIParam(emptyString = false,required = false,maxLength = 32)
    private String accountUuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
