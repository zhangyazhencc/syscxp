package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;


@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"}, accountOnly = true)
public class APIResetAccountApiSecurityMsg extends APIMessage implements AccountMessage{

    @APIParam(emptyString = false, required = true, validRegexValues = "^1[3,4,5,7,8]\\d{9}$")
    String phone;

    @APIParam(emptyString = false, maxLength = 6)
    String code;

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
                ntfy("Reset api_key").resource(getAccountUuid(), AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
