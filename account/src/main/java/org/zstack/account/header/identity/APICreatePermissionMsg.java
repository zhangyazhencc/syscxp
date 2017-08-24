package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APICreatePermissionMsg extends  APIMessage implements  AccountMessage {
    @APIParam(maxLength = 128)
    private String name;

    @APIParam(maxLength = 255)
    public String permisstion;

    @APIParam(maxLength = 255, required = false)
    private String description;

    public String getName() {
        return name;
    }

    public String getPermisstion() {
        return permisstion;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermisstion(String permisstion) {
        this.permisstion = permisstion;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}