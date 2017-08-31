package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.PermissionType;
import org.zstack.header.identity.PermissionVisible;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"permission"}, adminOnly = true)
public class APICreatePermissionMsg extends  APIMessage implements  AccountMessage {
    @APIParam(maxLength = 128)
    private String name;

    @APIParam(maxLength = 255)
    public String permission;

    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 36)
    private PermissionType type;

    @APIParam(numberRange = {0, 2})
    private int level;

    @APIParam(maxLength = 36)
    private int sortId;



    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
