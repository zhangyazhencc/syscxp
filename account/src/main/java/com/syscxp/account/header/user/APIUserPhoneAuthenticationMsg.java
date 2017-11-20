package com.syscxp.account.header.user;

import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/8/18.
 */
@SuppressUserCredentialCheck
@Action(services = {"account"}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIUserPhoneAuthenticationMsg extends APIMessage implements AccountMessage {

    @APIParam
    private String phone;

    @APIParam
    private String code;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
                ntfy("Phone Authentication").resource(that.getSession().getUserUuid(), UserVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
