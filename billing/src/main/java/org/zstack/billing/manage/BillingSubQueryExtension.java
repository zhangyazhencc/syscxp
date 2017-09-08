package org.zstack.billing.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.zstack.billing.identity.IdentityInterceptor;
import org.zstack.header.identity.AccountType;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.query.AbstractMysqlQuerySubQueryExtension;
import org.zstack.query.QueryUtils;

/**
 */
public class BillingSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private IdentityInterceptor identityInterceptor;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }
        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            if (msg instanceof APIQueryExpendMessage) {
               if(!StringUtils.isEmpty(((APIQueryExpendMessage) msg).getAccountUuid())){
                   return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), ((APIQueryExpendMessage) msg).getAccountUuid());
               }
            }
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!identityInterceptor.isResourceHavingAccountReference(entityClass)) {
            return null;
        }

        return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), msg.getSession().getAccountUuid());

    }
}
