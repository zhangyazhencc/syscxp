package com.syscxp.tunnel.header.switchs;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.tunnel.manage.SwitchConstant;

/**
 * Created by DCY on 2017-09-13
 */
@Action(category = SwitchConstant.ACTION_CATEGORY, names = {"update"}, adminOnly = true)
public class APIUpdateSwitchPortMsg extends APIMessage {

    @APIParam(emptyString = false,resourceType = SwitchPortVO.class)
    private String uuid;
    @APIParam(emptyString = false,required = false,validValues = {"Enabled", "Disabled"})
    private SwitchPortState state;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public SwitchPortState getState() {
        return state;
    }

    public void setState(SwitchPortState state) {
        this.state = state;
    }
}