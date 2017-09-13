package org.zstack.tunnel.header.switchs;

import org.zstack.header.identity.Action;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APIParam;
import org.zstack.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-09-13
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SwitchPortVO.class)
    private String uuid;
    @APIParam(required = false)
    private Integer enabled;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }
}
