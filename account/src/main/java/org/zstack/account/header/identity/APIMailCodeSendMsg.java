package org.zstack.account.header.identity;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountMessage;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIParam;
import org.zstack.header.message.APISyncCallMessage;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIMailCodeSendMsg extends APISyncCallMessage implements  AccountMessage {

    @APIParam(nonempty = true)
    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }


}
