package com.syscxp.idc.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.idc.trustee.TrusteeConstant;

@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"delete"})
public class APIDeleteTrusteeDetailMsg extends APIMessage {

    @APIParam(emptyString = true)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
