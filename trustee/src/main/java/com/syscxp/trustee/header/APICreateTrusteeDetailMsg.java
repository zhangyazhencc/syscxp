package com.syscxp.trustee.header;

import com.syscxp.header.identity.Action;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIParam;
import com.syscxp.trustee.trustee.TrusteeConstant;
import java.math.BigDecimal;

@Action(services = {TrusteeConstant.SERVICE_ID}, category = TrusteeConstant.ACTION_CATEGORY, names = {"create"})
public class APICreateTrusteeDetailMsg extends APIMessage {

    @APIParam(emptyString = false)
    private String name;

    @APIParam(emptyString = false)
    private String trusteeUuid;

    @APIParam(emptyString = false)
    private BigDecimal cost;

    @APIParam(required = false)
    private String description;

}
