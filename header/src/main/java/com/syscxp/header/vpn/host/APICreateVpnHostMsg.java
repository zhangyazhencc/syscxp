package com.syscxp.header.vpn.host;

import com.syscxp.header.host.APIAddHostEvent;
import com.syscxp.header.host.APIAddHostMsg;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.rest.RestRequest;
import org.springframework.http.HttpMethod;

@RestRequest(
        method = HttpMethod.POST,
        isAction = true,
        responseClass = APIAddHostEvent.class
)
public class APICreateVpnHostMsg extends APIAddHostMsg {
    @APIParam(emptyString = false)
    private String publicIp;
    @APIParam(required = false)
    private Integer sshPort;
    @APIParam(emptyString = false)
    private String nodeUuid;
    @APIParam(emptyString = false)
    private String username;
    @APIParam(emptyString = false)
    private String password;

    public String getPublicIp() {
        return publicIp;
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public String getNodeUuid() {
        return nodeUuid;
    }

    public void setNodeUuid(String nodeUuid) {
        this.nodeUuid = nodeUuid;
    }

    public Integer getSshPort() {
        return sshPort;
    }

    public void setSshPort(Integer sshPort) {
        this.sshPort = sshPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
