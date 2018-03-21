package com.syscxp.alarm.identity;

import com.syscxp.core.identity.DefaultIdentityInterceptor;
import com.syscxp.header.identity.SessionInventory;

public class IdentityInterceptor extends DefaultIdentityInterceptor {

    protected void afterGetSessionInventory(SessionInventory session){
    }

}
