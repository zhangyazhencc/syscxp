package org.zstack.account.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.identity.AccountConstant;
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
        if (AccountConstant.INITIAL_SYSTEM_ADMIN_UUID.equals(msg.getSession().getAccountUuid())
                && IdentityGlobalConfig.SHOW_ALL_RESOURCE_TO_ADMIN.value(Boolean.class))  {
            return null;
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
//        if (!acntMgr.isResourceHavingAccountReference(entityClass)) {
//            return null;
//        }

        String priKey = QueryUtils.getPrimaryKeyNameFromEntityClass(entityClass);

        return String.format("(%s.%s in (select accountresourcerefvo.resourceUuid from AccountResourceRefVO accountresourcerefvo where accountresourcerefvo.ownerAccountUuid = '%s'" +
                " and accountresourcerefvo.resourceType = '%s') or %s.%s in (select sharedresourcevo.resourceUuid from SharedResourceVO sharedresourcevo where" +
                " (sharedresourcevo.receiverAccountUuid = '%s' or sharedresourcevo.toPublic = 1) and sharedresourcevo.resourceType = '%s'))",
        inventoryClass.getSimpleName().toLowerCase(), priKey, msg.getSession().getAccountUuid(), entityClass.getSimpleName(),
        inventoryClass.getSimpleName().toLowerCase(), priKey, msg.getSession().getAccountUuid(), entityClass.getSimpleName());

        /*
        return String.format("%s.%s in (select resourceUuid from (select accountresourcerefvo.resourceUuid from AccountResourceRefVO accountresourcerefvo where accountresourcerefvo.ownerAccountUuid = '%s'" +
                        " and accountresourcerefvo.resourceType = '%s' union select sharedresourcevo.resourceUuid from SharedResourceVO sharedresourcevo where" +
                        " (sharedresourcevo.receiverAccountUuid = '%s' or sharedresourcevo.toPublic = 1) and sharedresourcevo.resourceType = '%s') as accountsubtable)",
                inventoryClass.getSimpleName().toLowerCase(), priKey, msg.getSession().getAccountUuid(), entityClass.getSimpleName(), msg.getSession().getAccountUuid(), entityClass.getSimpleName());
                */
    }
}
