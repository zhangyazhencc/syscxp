package com.syscxp.header.tunnel.solution;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIUpdateSolutionEvent.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "update")
public class APIUpdateSolutionMsg extends  APIMessage {

    @APIParam(maxLength = 32, resourceType = SolutionVO.class)
    private String uuid;
    @APIParam(maxLength = 128)
    private String name;
    @APIParam(maxLength = 255,required = false)
    private String description;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
            ntfy("Update SolutionVO")
                .resource(uuid, SolutionVO.class)
                .messageAndEvent(that, evt).done();
            }
        };
    }
}
