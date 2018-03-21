package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;


@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "create")
public class APICreateSolutionMsg extends  APIMessage {

    @APIParam(maxLength = 128)
    private String name;
    @APIParam(maxLength = 255,required = false)
    private String description;
    @APIParam(maxLength = 32)
    private String accountUuid;

    public String getAccountUuid() {
        if(getSession().getType() == AccountType.SystemAdmin){
            return accountUuid;
        }else{
            return getSession().getAccountUuid();
        }
    }
    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
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
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
            String uuid = null;
            if (evt.isSuccess()) {
                uuid = ((APICreateSolutionEvent) evt).getInventory().getUuid();
            }
            ntfy("Create SolutionVO")
                .resource(uuid, SolutionVO.class)
                .messageAndEvent(that, evt).done();
            }
        };
    }
}
