package com.syscxp.billing.identity;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.query.QueryOp;
import com.syscxp.query.AbstractMysqlQuerySubQueryExtension;
import com.syscxp.query.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.query.APIQueryMessage;

import java.util.List;

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

                List<String> accountUuids = ((APIQueryExpendMessage) msg).getAccountUuids();
                if(accountUuids!=null && accountUuids.size()>0){
                    msg.addQueryCondition("accountUuid", QueryOp.IN, (String[])accountUuids.toArray());
                    return null;
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
