package org.zstack.tunnel.header.tunnel;

import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;

/**
 * Created by DCY on 2017-09-14
 */
public class APIDeleteNetworkMsg extends APIMessage {
    @APIParam(emptyString = false, resourceType = NetworkVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}