package org.zstack.account.header.identity;

import org.zstack.header.message.APIMessage;

public class APIGetAccountMsg extends  APIMessage implements  AccountMessage {

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
