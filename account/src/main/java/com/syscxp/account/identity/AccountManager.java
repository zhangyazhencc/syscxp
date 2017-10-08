package com.syscxp.account.identity;

import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.message.APIMessage;

public interface AccountManager {

    void checkApiMessagePermission(APIMessage msg);

    boolean isAdmin(SessionInventory session);

}
