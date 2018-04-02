package com.syscxp.idc.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.idc.idc.IdcConstant;

@Action(services = {IdcConstant.SERVICE_ID}, category = IdcConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteIdcMsg extends APIMessage{

    @APIParam(emptyString = false)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
