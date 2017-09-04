package org.zstack.account.header.identity;


import org.zstack.header.message.APISyncCallMessage;

public class APIGetUserMsg extends APISyncCallMessage implements  AccountMessage {

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

}
