package org.zstack.account.header.identity;

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
    public String permisstion;

    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(maxLength = 36, required = false)
    private PermissionType type;

    @APIParam(maxLength = 36, required = false)
    private PermissionVisible visible;

    @APIParam(maxLength = 36, required = false)
    private int sortId;



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

    public PermissionType getType() {
        return type;
    }

    public PermissionVisible getVisible() {
        return visible;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public void setVisible(PermissionVisible visible) {
        this.visible = visible;
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
