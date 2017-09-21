package org.zstack.account.header.user;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountMessage;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.UserCredentialCheck;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/8/8.
 */
@UserCredentialCheck
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
public class APIUpdateUserPWDMsg extends APIMessage implements AccountMessage {

    @APIParam(maxLength = 36)
    private String phone;

    @APIParam(maxLength = 32)
    private String code;

    @APIParam(maxLength = 128)
    private String oldpassword;

    @APIParam(maxLength = 128)
    private String newpassword;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

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
