package com.syscxp.tunnel.manage;

import com.syscxp.query.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.query.AbstractMysqlQuerySubQueryExtension;
import com.syscxp.tunnel.identity.IdentityInterceptor;

/**
 * Created by DCY on 2017-09-07
 */
public class TunnelSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private IdentityInterceptor identityInterceptor;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if(msg.getSession() == null){
            return null;
        }
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
