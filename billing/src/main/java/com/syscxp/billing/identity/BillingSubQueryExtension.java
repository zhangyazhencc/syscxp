package com.syscxp.billing.identity;

import com.syscxp.billing.header.APIQueryExpendMessage;
import com.syscxp.billing.header.balance.APIQueryAccountDiscountMsg;
import com.syscxp.billing.header.balance.APIQueryDealDetailMsg;
import com.syscxp.billing.header.bill.APIQueryBillMsg;
import com.syscxp.billing.header.order.APIQueryOrderMsg;
import com.syscxp.header.query.AddExtraConditionToQueryExtensionPoint;
import com.syscxp.header.query.QueryCondition;
import com.syscxp.header.query.QueryOp;
import com.syscxp.query.AbstractMysqlQuerySubQueryExtension;
import com.syscxp.query.QueryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.query.APIQueryMessage;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class BillingSubQueryExtension extends AbstractMysqlQuerySubQueryExtension implements AddExtraConditionToQueryExtensionPoint {
    @Autowired
    private IdentityInterceptor identityInterceptor;

    @Override
    public String makeSubquery(APIQueryMessage msg, Class inventoryClass) {
        if (msg.getSession().isAdminAccountSession() || msg.getSession().isAdminUserSession()) {
            return null;
        }

        if (msg.getSession().getType() == AccountType.Proxy) {
            if (getMessageClassesForAddExtraConditionToQueryExtensionPoint().contains(msg.getClass())){
                List<String> accountUuids = ((APIQueryExpendMessage) msg).getAccountUuids();
                if(accountUuids != null && accountUuids.size() > 0) {
                    return null;
                }
                return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), "-1");

            }
        }

        Class entityClass = QueryUtils.getEntityClassFromInventoryClass(inventoryClass);
        if (!identityInterceptor.isResourceHavingAccountReference(entityClass)) {
            return null;
        }

        return String.format("%s.accountUuid = '%s'", inventoryClass.getSimpleName().toLowerCase(), msg.getSession().getAccountUuid());

    }

    @Override
    public List<Class> getMessageClassesForAddExtraConditionToQueryExtensionPoint() {
        List<Class> clz = new ArrayList<>();
        clz.add(APIQueryAccountDiscountMsg.class);
        clz.add(APIQueryDealDetailMsg.class);
        clz.add(APIQueryBillMsg.class);
        clz.add(APIQueryOrderMsg.class);
        return clz;
    }

    @Override
    public List<QueryCondition> getExtraQueryConditionForMessage(APIQueryMessage msg) {
        List<QueryCondition> conds = new ArrayList<QueryCondition>();
        List<String> accountUuids = ((APIQueryExpendMessage) msg).getAccountUuids();
        if(accountUuids != null && accountUuids.size() > 0) {
            QueryCondition qcond = new QueryCondition();
            qcond.setName("accountUuid");
            qcond.setOp(QueryOp.IN.toString());
            qcond.setValues( accountUuids.toArray(new String[accountUuids.size()]));
            conds.add(qcond);
        }
        return conds;
    }
}
