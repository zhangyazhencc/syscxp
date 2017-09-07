package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.PolicyStatement;
import org.zstack.header.identity.StatementEffect;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

import java.util.List;

import static org.zstack.utils.CollectionDSL.list;

@Action(category = AccountConstant.ACTION_CATEGORY, names = {"user_policy"}, accountOnly = true)
public class APICreatePolicyMsg extends  APIMessage implements AccountMessage{
    @APIParam(maxLength = 128)
    private String name;

    @APIParam(maxLength = 255, required = false)
    private String description;

    @APIParam(nonempty = true)
    private List<String> PermissionUuids;

    public List<String> getPermissionUuids() {
        return PermissionUuids;
    }

    public void setPermissionUuids(List<String> permissionUuids) {
        PermissionUuids = permissionUuids;
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
                    uuid = ((APICreatePolicyEvent)evt).getInventory().getUuid();
                }
                ntfy("Creating").resource(uuid, PolicyVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
