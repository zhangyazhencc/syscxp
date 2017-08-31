package org.zstack.tunnel.header.endpoint;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-24
 */
public class APIOpenEndpointMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
    private String targetUuid;

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }
}
