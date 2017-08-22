package org.zstack.tunnel.header.identity.node;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-21
 */
public class ApiUpdateNodeMsg extends APIMessage {

    @APIParam(nonempty = true,resourceType = NodeVO.class, checkAccount = true, operationTarget = true)
    private String targetUuid;

    @APIParam(nonempty = true,maxLength = 255)
    private String name;

    @APIParam(nonempty = true,maxLength = 128)
    private String code;

    @APIParam(nonempty = true,validValues = {"CLOUD", "ACCESSIN","IDC","VPN","ECP","EXCHANGE"})
    private NodeProperty property;

    @APIParam(nonempty = true,validValues = {"CLOSE", "OPEN","AVAILABLE"})
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

    public NodeProperty getProperty() {
        return property;
    }

    public void setProperty(NodeProperty property) {
        this.property = property;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }
}
