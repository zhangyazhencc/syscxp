package org.zstack.billing.identity;

import org.zstack.header.identity.SessionInventory;
import org.zstack.header.message.APIMessage;

public interface BillingManager {

    void checkApiMessagePermission(APIMessage msg);

    boolean isAdmin(SessionInventory session);

    boolean isResourceHavingAccountReference(Class entityClass);


}