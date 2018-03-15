package com.syscxp.header.tunnel.host;

import com.syscxp.header.identity.SuppressCredentialCheck;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;

/***
 * 网路工具监控机查询
 */
@SuppressCredentialCheck
@RestRequest(
        method = HttpMethod.GET,
        isAction = true,
        responseClass = APIQueryNettoolMonitorHostReply.class
)
public class APIQueryNettoolMonitorHostMsg extends APISyncCallMessage {

}
