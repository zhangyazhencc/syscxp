package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

import java.util.List;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, names = {"create"}, accountOnly = true)
public class APICreateRoleMsg extends  APIMessage implements AccountMessage {
    @APIParam(maxLength = 128)
    private String name;

    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(nonempty = true)
    private List<String> policyUuids;

    public List<String> getPolicyUuids() {
        return policyUuids;
    }

    public void setPolicyUuids(List<String> policyUuids) {
        this.policyUuids = policyUuids;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
 

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreateRoleEvent)evt).getInventory().getUuid();
                }
                ntfy("Creating").resource(uuid, RoleVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
