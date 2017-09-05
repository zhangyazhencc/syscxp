package org.zstack.billing.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.billing.header.balance.APIQueryDealDetailMsg;
import org.zstack.billing.header.bill.APIQueryBillMsg;
import org.zstack.billing.header.order.APIQueryOrderMsg;
import org.zstack.billing.header.order.APIQueryOrderReply;
import org.zstack.billing.identity.IdentiyInterceptor;
import org.zstack.header.identity.AccountType;
import org.zstack.header.query.APIQueryMessage;
import org.zstack.query.AbstractMysqlQuerySubQueryExtension;
import org.zstack.query.QueryUtils;

/**
 */
public class BillingSubQueryExtension extends AbstractMysqlQuerySubQueryExtension {
    @Autowired
    private IdentiyInterceptor identiyInterceptor;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }
        if (msg.getSession().getType().equals(AccountType.Proxy)) {
            if (msg instanceof APIQueryBillMsg) {
                if (!((APIQueryBillMsg) msg).isSelfSelect()) {
                    return null;
                }

            } else if (msg instanceof APIQueryDealDetailMsg) {
                if (!((APIQueryDealDetailMsg) msg).isSelfSelect()) {
                    return null;
                }

            } else if (msg instanceof APIQueryOrderMsg) {
                if (!((APIQueryOrderMsg) msg).isSelfSelect()) {
                    return null;
                }

            }
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!identiyInterceptor.isResourceHavingAccountReference(entityClass)) {
            return null;
        }

        return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), msg.getSession().getAccountUuid());

    }
}
