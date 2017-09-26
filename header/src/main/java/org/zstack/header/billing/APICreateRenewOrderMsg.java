package org.zstack.header.billing;

import org.zstack.header.identity.Action;
import org.zstack.header.identity.InnerCredentialCheck;
import org.zstack.header.message.APIParam;

@Action(category = BillingConstant.ACTION_CATEGORY_ORDER)
@InnerCredentialCheck
public class APICreateRenewOrderMsg  extends APICreateOrderMsg {

    @APIParam(emptyString = false)
    private String renewUuid;

    public String getRenewUuid() {
        return renewUuid;
    }

    public void setRenewUuid(String renewUuid) {
        this.renewUuid = renewUuid;
    }
}
