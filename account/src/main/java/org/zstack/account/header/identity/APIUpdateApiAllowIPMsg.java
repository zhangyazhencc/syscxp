package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by wangwg on 2017/08/23.
 */
@Action(category = AccountConstant.ACTION_CATEGORY)
public class APIUpdateApiAllowIPMsg extends APIMessage implements AccountMessage{

    @APIParam
    private String allowIP;

    @Override
    public String getAccountUuid() {
        return this.getAccountUuid();
    }

    public String getAllowIP() {
        return allowIP;
    }

    public void setAllowIP(String allowIP) {
        this.allowIP = allowIP;
    }
}
