package org.zstack.account.header.user;

import org.zstack.header.identity.SuppressCredentialCheck;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/9/4.
 */
@SuppressCredentialCheck
public class APIUserPWDBackMsg extends APIMessage  {

    @APIParam
    private String accountName;

    @APIParam
    private String phone;

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update user password").resource(that.getSession().getUserUuid(), UserVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}