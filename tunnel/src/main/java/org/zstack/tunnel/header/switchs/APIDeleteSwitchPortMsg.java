package org.zstack.tunnel.header.switchs;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-09-13
 */
public class APIDeleteSwitchPortMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = SwitchPortVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
