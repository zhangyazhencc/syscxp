package org.zstack.tunnel.header.endpoint;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-23
 */
public class APICreateEndpointMsg extends APIMessage {

    @APIParam(emptyString = false,maxLength = 32)
    private String nodeUuid;

    @APIParam(emptyString = false,maxLength = 255)
    private String name;

    @APIParam(emptyString = false,maxLength = 128)
    private String code;

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
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
