package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.PasswordNoSee;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;


@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIResetAccountApiSecurityMsg extends APIMessage implements AccountMessage{

    @APIParam(emptyString = false, validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    @PasswordNoSee
    private String phone;

    @APIParam(emptyString = false, maxLength = 6)
    private String code;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Reset api_key").resource(getAccountUuid(), AccountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
