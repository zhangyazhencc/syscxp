package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import org.springframework.http.HttpMethod;

/**
 * Create by DCY on 2017/11/20
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIListInnerEndpointReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIListInnerEndpointMsg extends APISyncCallMessage {

    @APIParam(emptyString = false, resourceType = EndpointVO.class)
    private String endpointAUuid;

    @APIParam(emptyString = false, resourceType = EndpointVO.class)
    private String endpointZUuid;

    public String getEndpointAUuid() {
        return endpointAUuid;
    }

    public void setEndpointAUuid(String endpointAUuid) {
        this.endpointAUuid = endpointAUuid;
    }

    public String getEndpointZUuid() {
        return endpointZUuid;
    }

    public void setEndpointZUuid(String endpointZUuid) {
        this.endpointZUuid = endpointZUuid;
    }
}
