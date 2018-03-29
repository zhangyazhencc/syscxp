package com.syscxp.header.tunnel.tunnel;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestRequest;
import com.syscxp.header.tunnel.TunnelConstant;
import org.springframework.http.HttpMethod;

/**
 * Create by DCY on 2018/3/20
 */
@RestRequest(
        path = "tunnel",
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIGetTunnelVsiAutoReply.class
)
@Action(services = {TunnelConstant.ACTION_SERVICE}, category = TunnelConstant.ACTION_CATEGORY, names = {"read"})
public class APIGetTunnelVsiAutoMsg extends APISyncCallMessage {
}
