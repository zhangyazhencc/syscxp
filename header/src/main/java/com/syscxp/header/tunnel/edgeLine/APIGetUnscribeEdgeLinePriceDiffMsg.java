package com.syscxp.header.tunnel.edgeLine;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.EdgeLineConstant;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Create by DCY on 2018/1/12
 */
@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIGetUnscribeEdgeLinePriceDiffReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = EdgeLineConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetUnscribeEdgeLinePriceDiffMsg extends APISyncCallMessage {

    @APIParam(emptyString = false,resourceType = EdgeLineVO.class, checkAccount = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
