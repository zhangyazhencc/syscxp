package com.syscxp.billing.header.balance;

import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.identity.Action;
import com.syscxp.header.identity.SuppressUserCredentialCheck;
import com.syscxp.header.message.APIParam;
import com.syscxp.header.message.APISyncCallMessage;
import org.springframework.util.StringUtils;

@SuppressUserCredentialCheck
@Action(category = BillingConstant.ACTION_CATEGORY_BILLING, names = {"read"})
public class APIGetAccountBalanceMsg extends APISyncCallMessage {

    @APIParam(required = false,emptyString = false)
    private String  accountUuid;

    public String getAccountUuid() {
       if(this.getSession().getType()!= AccountType.Normal && !StringUtils.isEmpty(accountUuid)){
           return accountUuid;
       }
       return this.getSession().getAccountUuid();
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }
}
