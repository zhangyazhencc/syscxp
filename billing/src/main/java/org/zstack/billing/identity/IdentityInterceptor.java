package org.zstack.billing.identity;

import org.springframework.util.StringUtils;
import org.zstack.billing.header.balance.AccountBalanceVO;
import org.zstack.billing.header.balance.AccountBalanceVO_;
import org.zstack.billing.header.balance.AccountDischargeVO;
import org.zstack.billing.header.balance.AccountDischargeVO_;
import org.zstack.billing.header.order.ProductPriceUnitVO;
import org.zstack.core.Platform;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.identity.DefaultIdentityInterceptor;
import org.zstack.header.identity.SessionInventory;
import java.math.BigDecimal;
import java.util.List;


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
        List<ProductPriceUnitVO> ppu = dbf.listAll(ProductPriceUnitVO.class);
        for(ProductPriceUnitVO productPriceUnitVO : ppu){
            SimpleQuery<AccountDischargeVO> query = dbf.createQuery(AccountDischargeVO.class);
            query.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
            query.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, productPriceUnitVO.getProductType());
            query.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, productPriceUnitVO.getCategory());
            AccountDischargeVO accountDischargeVO = query.find();
            if(accountDischargeVO == null){
                accountDischargeVO = new AccountDischargeVO();
                accountDischargeVO.setUuid(Platform.getUuid());
                accountDischargeVO.setAccountUuid(accountUuid);
                accountDischargeVO.setCategory(productPriceUnitVO.getCategory());
                accountDischargeVO.setProductType(productPriceUnitVO.getProductType());
                accountDischargeVO.setDisCharge(100);
                dbf.persistAndRefresh(accountDischargeVO);
            }
        }
    }

}
