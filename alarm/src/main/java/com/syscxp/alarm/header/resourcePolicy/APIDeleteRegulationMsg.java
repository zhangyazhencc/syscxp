package com.syscxp.alarm.header.resourcePolicy;

import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;

@Action(category = AlarmConstant.ACTION_CATEGORY_ALARM, names = {"delete"})
public class APIDeleteRegulationMsg extends APIMessage{

    @APIParam(emptyString = false,resourceType = RegulationVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
