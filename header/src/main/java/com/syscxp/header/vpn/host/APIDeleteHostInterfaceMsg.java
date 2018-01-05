package com.syscxp.header.vpn.host;

import com.syscxp.header.message.APIDeleteMessage;
import com.syscxp.header.message.APIEvent;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.notification.ApiNotification;
import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;


@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIDeleteHostInterfaceEvent.class
)
public class APIDeleteHostInterfaceMsg extends APIDeleteMessage {
    @APIParam(resourceType = HostInterfaceVO.class)
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
                ntfy("Delete HostInterfaceVO")
                        .resource(uuid, HostInterfaceVO.class)
                        .messageAndEvent(that, evt).done();
            }
        };
    }
}
