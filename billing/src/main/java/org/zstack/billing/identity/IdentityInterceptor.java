package org.zstack.billing.identity;

import org.springframework.util.StringUtils;
import org.zstack.header.billing.AccountBalanceVO;
import org.zstack.header.billing.AccountBalanceVO_;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.identity.DefaultIdentityInterceptor;
import org.zstack.header.identity.SessionInventory;
import java.math.BigDecimal;


public class IdentityInterceptor extends DefaultIdentityInterceptor {

    protected void afterGetSessionInventory(SessionInventory session){

        String accountUuid = session.getAccountUuid();
        if (!StringUtils.isEmpty(accountUuid)) {
            SimpleQuery<AccountBalanceVO> q = dbf.createQuery(AccountBalanceVO.class);
            q.add(AccountBalanceVO_.uuid, SimpleQuery.Op.EQ, accountUuid);
            AccountBalanceVO a = q.find();
            if (a == null) {
                AccountBalanceVO vo = new AccountBalanceVO();
                vo.setUuid(accountUuid);
                vo.setCashBalance(new BigDecimal("0"));
                vo.setPresentBalance(new BigDecimal("0"));
                vo.setCreditPoint(new BigDecimal("0"));
                dbf.persist(vo);
            }
        }
    }

}
