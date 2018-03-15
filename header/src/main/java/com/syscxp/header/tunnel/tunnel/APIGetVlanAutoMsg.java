package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import org.springframework.http.HttpMethod;

/**
 * Create by DCY on 2017/11/3
 */

@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIGetVlanAutoReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetVlanAutoMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceUuidA;

    @APIParam(emptyString = false,resourceType = InterfaceVO.class, checkAccount = true)
    private String interfaceUuidZ;

    @APIParam(emptyString = false, required = false, maxLength = 32, resourceType = EndpointVO.class)
    private String innerConnectedEndpointUuid;

    public String getInterfaceUuidA() {
        return interfaceUuidA;
    }

    public void setInterfaceUuidA(String interfaceUuidA) {
        this.interfaceUuidA = interfaceUuidA;
    }

    public String getInterfaceUuidZ() {
        return interfaceUuidZ;
    }

    public void setInterfaceUuidZ(String interfaceUuidZ) {
        this.interfaceUuidZ = interfaceUuidZ;
    }

    public String getInnerConnectedEndpointUuid() {
        return innerConnectedEndpointUuid;
    }

    public void setInnerConnectedEndpointUuid(String innerConnectedEndpointUuid) {
        this.innerConnectedEndpointUuid = innerConnectedEndpointUuid;
    }
}
