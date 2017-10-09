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
    private String OldEmail;

    @APIParam
    private String OldCode;

    @APIParam
    private String newEmail;

    @APIParam
    private String newCode;

    public String getOldEmail() {
        return OldEmail;
    }

    public void setOldEmail(String oldEmail) {
        OldEmail = oldEmail;
    }

    public String getOldCode() {
        return OldCode;
    }

    public void setOldCode(String oldCode) {
        OldCode = oldCode;
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
                ntfy("Update account email").resource(getAccountUuid(), AccountVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
