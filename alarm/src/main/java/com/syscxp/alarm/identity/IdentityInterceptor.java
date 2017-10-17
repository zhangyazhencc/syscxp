package com.syscxp.alarm.identity;

import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.identity.DefaultIdentityInterceptor;
import com.syscxp.header.billing.AccountBalanceVO;
import com.syscxp.header.billing.AccountBalanceVO_;
import com.syscxp.header.identity.SessionInventory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;


public class IdentityInterceptor extends DefaultIdentityInterceptor {

    protected void afterGetSessionInventory(SessionInventory session){

        String accountUuid = session.getAccountUuid();
    }

}
