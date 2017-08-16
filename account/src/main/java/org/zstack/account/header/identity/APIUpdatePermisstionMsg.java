package org.zstack.account.header.identity;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

@Action(category = AccountConstant.ACTION_CATEGORY, accountOnly = true)
public class APIUpdatePermisstionMsg extends  APIMessage {

    @APIParam(maxLength = 255)
    private String uuid;

    @APIParam(maxLength = 255)
    private String name;

    @APIParam(maxLength = 255)
    public String authority;

    @APIParam(maxLength = 2048, required = false)
    private String description;

    public String getName() {
        return name;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
