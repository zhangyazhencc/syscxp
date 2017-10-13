package com.syscxp.alarm.identity;

import com.syscxp.alarm.header.APIQueryExpendMessage;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.query.APIQueryMessage;
import com.syscxp.query.AbstractMysqlQuerySubQueryExtension;
import com.syscxp.query.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

/**
 */
public class AlarmSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
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
