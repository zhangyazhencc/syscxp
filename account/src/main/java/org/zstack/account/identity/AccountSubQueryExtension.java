package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.query.AbstractMysqlQuerySubQueryExtension;
import org.zstack.query.QueryUtils;

/**
 */
public class AccountSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private AccountManager acntMgr;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!acntMgr.isResourceHavingAccountReference(entityClass)) {
            return null;
        }

        String priKey = QueryUtils.getPrimaryKeyNameFromEntityClass(entityClass);

        return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), msg.getSession().getAccountUuid());

    }
}
