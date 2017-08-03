package org.zstack.account.identity;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIMessage;

import java.util.List;
import java.util.Map;

public interface AccountManager {

    void checkApiMessagePermission(APIMessage msg);

    boolean isAdmin(SessionInventory session);

}
