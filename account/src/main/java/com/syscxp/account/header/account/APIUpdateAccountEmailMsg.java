package com.syscxp.account.header.account;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by wangwg on 2017/8/9.
 */
@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"update"}, accountOnly = true)
public class APIUpdateAccountEmailMsg extends APIMessage implements AccountMessage{
    @APIParam
    private String oldEmail;

    @APIParam
    private String oldCode;

    @APIParam
    private String newEmail;

    @APIParam
    private String newCode;

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
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
                ntfy("Update account email").resource(getAccountUuid(), AccountVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
