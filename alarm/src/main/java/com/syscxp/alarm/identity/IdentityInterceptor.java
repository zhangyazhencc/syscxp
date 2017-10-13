package com.syscxp.alarm.identity;

import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.identity.DefaultIdentityInterceptor;
import com.syscxp.header.billing.AccountBalanceVO;
import com.syscxp.header.billing.AccountBalanceVO_;
import com.syscxp.header.identity.SessionInventory;
import org.springframework.util.StringUtils;

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
