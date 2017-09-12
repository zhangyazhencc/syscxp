package org.zstack.tunnel.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.query.AbstractMysqlQuerySubQueryExtension;
import org.zstack.query.QueryUtils;
import org.zstack.tunnel.identity.IdentityInterceptor;

/**
 * Created by DCY on 2017-09-07
 */
public class TunnelSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private IdentityInterceptor identityInterceptor;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!identityInterceptor.isResourceHavingAccountReference(entityClass)) {
            return null;
        }


        return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase()
                , msg.getSession().getAccountUuid()) ;

    }
}
