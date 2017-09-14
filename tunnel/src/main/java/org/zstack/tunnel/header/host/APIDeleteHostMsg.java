package org.zstack.tunnel.header.host;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.TunnelConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-14.
 * @Description: .
 */
@Action(category = TunnelConstant.ACTION_CATEGORY_HOST, names = {"delete"}, adminOnly = true)
public class APIDeleteHostMsg extends APIMessage {
    @APIParam(emptyString = false,checkAccount = true,resourceType = HostEO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
