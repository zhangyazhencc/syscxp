package org.zstack.account.header.identity;

import org.zstack.header.identity.AccountType;
import org.zstack.header.identity.Action;
import org.zstack.header.identity.PermissionType;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"permission"}, adminOnly = true)
public class APICreatePermissionMsg extends  APIMessage implements  AccountMessage {
    @APIParam(maxLength = 128)
    private String name;

    @APIParam(maxLength = 255)
    public String permission;

    @APIParam(maxLength = 36)
    private PermissionType type;

    @APIParam
    private AccountType level;

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

    public AccountType getLevel() {
        return level;
    }

    public void setLevel(AccountType level) {
        this.level = level;
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
                    uuid = ((APICreatePermissionEvent) evt).getInventory().getUuid();
                }

                ntfy("Create PermissionVO")
                        .resource(uuid, PermissionVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
