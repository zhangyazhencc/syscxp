package org.zstack.account.header.identity;

import org.zstack.header.identity.SessionInventory;

/**
 * Created by xing5 on 2016/5/17.
 */
public interface SessionLogoutExtensionPoint {
    void sessionLogout(SessionInventory session);
}
