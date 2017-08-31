package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-08-29
 */
public class APIPrivateSwitchMsg extends APIMessage {
    @APIParam(emptyString = false,resourceType = SwitchVO.class)
    private String targetUuid;

    @APIParam(required = false)
    private Integer isPrivate;

    public String getTargetUuid() {
        return targetUuid;
    }

    public void setTargetUuid(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }
}
