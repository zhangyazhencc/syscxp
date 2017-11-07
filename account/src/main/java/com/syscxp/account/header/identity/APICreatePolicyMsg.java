package com.syscxp.account.header.identity;

import com.syscxp.account.header.account.AccountConstant;
import com.syscxp.account.header.account.AccountMessage;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.PermissionType;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY_ACCOUNT, adminOnly = true)
public class APICreatePolicyMsg extends  APIMessage implements AccountMessage {
    @APIParam(maxLength = 128)
    private String name;

    @APIParam(maxLength = 255)
    public String permission;

    @APIParam(maxLength = 36)
    private PermissionType type;

    @APIParam
    private AccountType accountType;

    @APIParam(maxLength = 36)
    private Integer sortId;


    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }


    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
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
                String uuid = null;
                if (evt.isSuccess()) {
                    uuid = ((APICreatePolicyEvent) evt).getInventory().getUuid();
                }

                ntfy("Create PolicyVO")
                        .resource(uuid, PolicyVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
