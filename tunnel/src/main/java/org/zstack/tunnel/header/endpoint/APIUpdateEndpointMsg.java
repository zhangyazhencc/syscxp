package org.zstack.tunnel.header.endpoint;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-23
 */
public class APIUpdateEndpointMsg extends APIMessage {
    @APIParam(nonempty = true,resourceType = EndpointVO.class)
    private String targetUuid;

    @APIParam(required = false,maxLength = 255)
    private String name;

    @APIParam(required = false,maxLength = 128)
    private String code;

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
