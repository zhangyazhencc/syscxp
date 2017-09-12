package org.zstack.account.header.account;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/8/8.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APIUpdateAccountPWDMsg extends APIMessage implements AccountMessage {

    @APIParam
    private String phone;

    @APIParam
    private String code;

    @APIParam
    private String oldpassword;

    @APIParam(maxLength = 2048)
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

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update account password").resource(getAccountUuid(), AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
