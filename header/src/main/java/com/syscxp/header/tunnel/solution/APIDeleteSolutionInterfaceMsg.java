package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "delete")
public class APIDeleteSolutionInterfaceMsg extends APIMessage {

    @APIParam(maxLength = 32, emptyString = false, resourceType = SolutionInterfaceVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Delete SolutionInterfaceVO")
                        .resource(uuid, SolutionInterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
