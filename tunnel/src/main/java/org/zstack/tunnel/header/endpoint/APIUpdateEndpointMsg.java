package org.zstack.tunnel.header.endpoint;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * Created by DCY on 2017-08-23
 */

@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"endpoint"}, adminOnly = true)
public class APIUpdateEndpointMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = EndpointVO.class)
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
