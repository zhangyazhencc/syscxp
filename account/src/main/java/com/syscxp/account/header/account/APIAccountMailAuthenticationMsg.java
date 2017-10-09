package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/09/14.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APIAccountMailAuthenticationMsg extends APIMessage implements AccountMessage {

    @APIParam
    private String mail;

    @APIParam
    private String code;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("account_mail Authentication ")
                        .resource(getAccountUuid(), AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
