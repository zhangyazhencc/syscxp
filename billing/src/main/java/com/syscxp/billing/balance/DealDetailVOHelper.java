package com.syscxp.billing.balance;

import com.syscxp.billing.header.balance.DealDetailVO;
import com.syscxp.billing.header.balance.DealState;
import com.syscxp.billing.header.balance.DealType;
import com.syscxp.billing.header.balance.DealWay;
import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class DealDetailVOHelper {

    private DatabaseFacade dbf;

    public DealDetailVOHelper(DatabaseFacade dbf) {
        this.dbf = dbf;
    }

    @Transactional
    public void saveDealDetailVO(String accountUuid, DealWay dealWay, BigDecimal income, BigDecimal expend, Timestamp currentTimestamp, DealType dealType, DealState dealState, BigDecimal balance, String outTradeNo, String tradeNo, String opAccountUuid,String comment,String orderUuid) {
        DealDetailVO dVO = new DealDetailVO();
        dVO.setUuid(Platform.getUuid());
        dVO.setAccountUuid(accountUuid);
        dVO.setDealWay(dealWay);
        dVO.setIncome(income);
        dVO.setExpend(expend);
        dVO.setFinishTime(currentTimestamp);
        dVO.setType(dealType);
        dVO.setState(dealState);
        dVO.setBalance(balance == null ? BigDecimal.ZERO : balance);
        dVO.setOutTradeNO(outTradeNo);
        dVO.setTradeNO(tradeNo);
        dVO.setOpAccountUuid(opAccountUuid);
        dVO.setComment(comment);
        dVO.setOrderUuid(orderUuid);
        dbf.getEntityManager().persist(dVO);
    }
}
