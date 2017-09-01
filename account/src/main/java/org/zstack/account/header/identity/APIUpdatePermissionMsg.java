package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.PermissionType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"permission"}, adminOnly = true)
public class APIUpdatePermissionMsg extends APIMessage implements AccountMessage {

    @APIParam(maxLength = 255)
    private String uuid;

    @APIParam(maxLength = 255, required = false)
    private String name;

    @APIParam(maxLength = 255, required = false)
    public String permisstion;

    @APIParam(maxLength = 36)
    private PermissionType type;

    @APIParam(numberRange = {0, 2})
    private Integer level;

    @APIParam(maxLength = 36)
    private Integer sortId;

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSortId() {
        return sortId;
    }

    public void setSortId(Integer sortId) {
        this.sortId = sortId;
    }

    public String getName() {
        return name;
    }

    public String getPermisstion() {
        return permisstion;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setPermisstion(String permisstion) {
        this.permisstion = permisstion;
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getAccountUuid() {
        return this.getSession().getAccountUuid();
    }
}
