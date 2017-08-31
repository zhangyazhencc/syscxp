package org.zstack.tunnel.header.node;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

import java.util.List;

/**
 * Created by DCY on 2017-08-21
 */
public class APIUpdateNodeMsg extends APIMessage {

    @APIParam(nonempty = true,resourceType = NodeVO.class)
    private String targetUuid;

    @APIParam(required = false,maxLength = 255)
    private String name;

    @APIParam(required = false,maxLength = 128)
    private String code;

    @APIParam(required = false,maxLength = 128)
    private String property;

    @APIParam(required = false,validValues = {"CLOSE", "OPEN","AVAILABLE"})
    private NodeStatus status;

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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }
}
