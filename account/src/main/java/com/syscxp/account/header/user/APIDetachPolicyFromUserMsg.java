package com.syscxp.account.header.user;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.account.header.identity.RoleVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

/**
 * Created by frank on 7/9/2015.
 */
@Action(services = {AccountConstant.ACTION_SERVICE}, category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APIDetachPolicyFromUserMsg extends APIMessage implements AccountMessage {
    @APIParam(resourceType = RoleVO.class, checkAccount = true, operationTarget = true)
    private String policyUuid;
    @APIParam(resourceType = UserVO.class, checkAccount = true, operationTarget = true)
    private String userUuid;

    public String getPolicyUuid() {
        return policyUuid;
    }

    public void setPolicyUuid(String policyUuid) {
        this.policyUuid = policyUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    @Override
    public String getAccountUuid() {
        return getSession().getAccountUuid();
    }
 
    public static APIDetachPolicyFromUserMsg __example__() {
        APIDetachPolicyFromUserMsg msg = new APIDetachPolicyFromUserMsg();
        msg.setPolicyUuid(uuid());
        msg.setUserUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Detaching a policy[uuid:%s]", policyUuid).resource(userUuid, UserVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
