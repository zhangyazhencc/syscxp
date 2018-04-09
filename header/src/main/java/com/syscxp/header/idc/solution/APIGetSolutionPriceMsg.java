package com.syscxp.header.idc.solution;

import com.syscxp.header.idc.IdcConstant;
import com.syscxp.header.idc.SolutionConstant;
import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;

/**
 * Create by DCY on 2018/4/9
 */
@Action(services = {IdcConstant.ACTION_SERVICE}, category = SolutionConstant.ACTION_CATEGORY, names = "read")
public class APIGetSolutionPriceMsg extends APISyncCallMessage {

    @APIParam(maxLength = 32, emptyString = false, resourceType = SolutionVO.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
