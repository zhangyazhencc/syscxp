package com.syscxp.trustee.identity;

import com.syscxp.header.identity.AccountType;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.query.AbstractMysqlQuerySubQueryExtension;
import com.syscxp.query.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 */
public class TrusteeSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private IdentityInterceptor identityInterceptor;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }
        if (msg.getSession().getType().equals(AccountType.Proxy)) {

        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!identityInterceptor.isResourceHavingAccountReference(entityClass)) {
            return null;
        }

        return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), msg.getSession().getAccountUuid());

    }
}
