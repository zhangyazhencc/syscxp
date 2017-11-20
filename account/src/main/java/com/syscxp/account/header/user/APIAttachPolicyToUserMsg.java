package com.syscxp.account.header.user;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.account.header.identity.RoleVO;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, accountOnly = true)
public class APIAttachPolicyToUserMsg extends APIMessage implements AccountMessage {
    @APIParam(resourceType = UserVO.class, checkAccount = true, operationTarget = true)
    private String userUuid;
    @APIParam(resourceType = RoleVO.class, checkAccount = true, operationTarget = true)
    private String roleUuid;

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public static APIAttachPolicyToUserMsg __example__() {
        APIAttachPolicyToUserMsg msg = new APIAttachPolicyToUserMsg();

        msg.setRoleUuid(uuid());
        msg.setUserUuid(uuid());

        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Attaching a policy[uuid:%s]", roleUuid).resource(userUuid, UserVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
