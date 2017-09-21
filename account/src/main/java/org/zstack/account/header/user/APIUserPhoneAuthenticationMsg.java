package org.zstack.account.header.user;

import org.zstack.account.header.account.AccountConstant;
import org.zstack.account.header.account.AccountMessage;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.SuppressUserCredentialCheck;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/8/18.
 */
@SuppressUserCredentialCheck
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT)
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
                ntfy("Phone Authentication").resource(that.getSession().getUserUuid(), UserVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
