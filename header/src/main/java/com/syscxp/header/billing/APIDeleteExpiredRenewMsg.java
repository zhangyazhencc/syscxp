package com.syscxp.header.billing;

import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.InnerCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import org.springframework.util.StringUtils;

@InnerCredentialCheck
@Action(services = {BillingConstant.ACTION_SERVICE}, category = BillingConstant.ACTION_CATEGORY_RENEW)
public class APIDeleteExpiredRenewMsg extends APISyncCallMessage {

    @APIParam(emptyString = false)
    private String accountUuid;

    @APIParam(emptyString = false)
    private String productUuid;

    public String getAccountUuid() {
        if (StringUtils.isEmpty(accountUuid)){
            return this.getSession().getAccountUuid();
        }else {
            return accountUuid;
        }
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String productUuid) {
        this.productUuid = productUuid;
    }
}
