package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Created by DCY on 2017-09-14
 */

@RestRequest(
        path = "tunnel",
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIUpdateInterfaceEvent.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"update"})
public class APIUpdateInterfaceMsg extends APIMessage implements InterfaceMessage {
    @APIParam(resourceType = InterfaceVO.class, checkAccount = true)
    private String uuid;
    @APIParam(required = false, maxLength = 128)
    private String name;
    @APIParam(required = false, maxLength = 255)
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

    @Override
    public String getInterfaceUuid(){
        return uuid;
    }

    public ApiNotification __notification__() {
        final APIMessage that = this;

        return new ApiNotification() {
            @Override
            public void after(APIEvent evt) {
                ntfy("Update InterfaceVO")
                        .resource(uuid, InterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
