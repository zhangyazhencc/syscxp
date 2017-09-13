package org.zstack.tunnel.header.endpoint;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-13.
 * @Description: .
 */
@Action(category = TunnelConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteEndpointMsg extends APIMessage {
    @APIParam(emptyString = false, checkAccount = true, resourceType = EndpointEO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
