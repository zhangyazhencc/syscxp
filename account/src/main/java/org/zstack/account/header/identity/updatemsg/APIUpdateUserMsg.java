package org.zstack.account.header.identity.updatemsg;

import org.zstack.account.header.identity.AccountConstant;
import org.zstack.account.header.identity.AccountMessage;
import org.zstack.account.header.identity.UserVO;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APIEvent;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.header.notification.ApiNotification;

/**
 * Created by frank on 7/10/2015.
 */
@Action(category = AccountConstant.ACTION_CATEGORY)
public class APIUpdateUserMsg extends APIMessage implements AccountMessage {
    @APIParam(resourceType = UserVO.class, checkAccount = true, operationTarget = true, required = false)
    private String uuid;
    @APIParam(maxLength = 255, required = false)
    private String password;
    @APIParam(maxLength = 255, required = false)
    private String name;
    @APIParam(maxLength = 2048, required = false)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getAccountUuid() {
        return getSession().getAccountUuid();
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
 
    public static APIUpdateUserMsg __example__() {
        APIUpdateUserMsg msg = new APIUpdateUserMsg();
        msg.setName("new");
        msg.setUuid(uuid());
        return msg;
    }

    public ApiNotification __notification__() {
        APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Updating").resource(uuid, UserVO.class.getSimpleName())
                        .messageAndEvent(that, evt).done();
            }
        };
    }

}
