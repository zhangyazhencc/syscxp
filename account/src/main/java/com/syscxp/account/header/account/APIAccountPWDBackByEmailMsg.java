package com.syscxp.account.header.account;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/9/4.
 */
@SuppressCredentialCheck
public class APIAccountPWDBackByEmailMsg extends APIMessage  {

    @APIParam
    private String email;

    @APIParam
    private String code;

    @APIParam(maxLength = 2048)
    private String newpassword;

    public String getCode() {
        return code;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update password ")
                        .resource(email, AccountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }


}
