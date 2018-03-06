package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.PasswordNoSee;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/8/8.
 */
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APIUpdateAccountPWDMsg extends APIMessage implements AccountMessage {

    @APIParam(required = false)
    @PasswordNoSee
    private String phone;

    @APIParam(required = false)
    @PasswordNoSee
    private String email;

    @APIParam
    private String code;

    @APIParam
    @PasswordNoSee
    private String oldpassword;

    @APIParam(maxLength = 2048)
    @PasswordNoSee
    private String newpassword;

    public String getCode() {
        return code;
    }

    public String getOldpassword() {
        return oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
                ntfy("Update account password").resource(getAccountUuid(), AccountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
