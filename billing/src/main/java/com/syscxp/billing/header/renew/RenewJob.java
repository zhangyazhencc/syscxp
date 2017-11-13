package com.syscxp.billing.header.renew;

import com.syscxp.billing.header.balance.*;
import com.syscxp.billing.header.sla.ProductCaller;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.*;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.tunnel.tunnel.APIUpdateExpireDateMsg;
import com.syscxp.header.tunnel.tunnel.APIUpdateExpireDateReply;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

@Component
public class RenewJob{

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade restf;

    private static final CLogger logger = Utils.getLogger(RenewJob.class);

    @Scheduled(cron = "0 0/1 * * * ? ")
    @Transactional
    protected void autoRenew() {

        GLock lock = new GLock(String.format("id-%s", "createRenew"), 120);
        lock.lock();
        try {
            Timestamp currentTimestamp = dbf.getCurrentSqlTime();

            SimpleQuery<RenewVO> q = dbf.createQuery(RenewVO.class);
            q.add(RenewVO_.isRenewAuto, SimpleQuery.Op.EQ, true);
            List<RenewVO> renewVOs = q.list();
            logger.info("the demon thread was going to autoRenew");
            ListIterator<RenewVO> ite = renewVOs.listIterator();
            while (ite.hasNext()) {
                RenewVO renewVO = ite.next();
                Timestamp expiredTimestamp = renewVO.getExpiredTime();
                if (currentTimestamp.getTime() - expiredTimestamp.getTime() > 7 * 24 * 60 * 60 * 1000l) {
                    dbf.getEntityManager().remove(dbf.getEntityManager().merge(renewVO));
                    dbf.getEntityManager().flush();
                    continue;
                }
                if(currentTimestamp.getTime()<expiredTimestamp.getTime()){
                    logger.info("this product is also valid");
                    continue;
                }

                ProductCaller caller = new ProductCaller(renewVO.getProductType());
                APIUpdateExpireDateMsg aMsg = caller.getCallMsg();

                aMsg.setUuid(renewVO.getProductUuid());
                aMsg.setDuration(1);
                aMsg.setProductChargeModel(renewVO.getProductChargeModel());
                aMsg.setType(OrderType.RENEW);
                aMsg.setAccountUuid(renewVO.getAccountUuid());
                InnerMessageHelper.setMD5(aMsg);
                String gstr = RESTApiDecoder.dumpWithSession(aMsg);
                RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, RestAPIResponse.class);

                if (rsp.getState().equals(RestAPIState.Done.toString())) {
                    try {
                        APIUpdateExpireDateReply productReply = (APIUpdateExpireDateReply) RESTApiDecoder.loads(rsp.getResult());
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                }

            }

        } finally {
            lock.unlock();
        }

    }


}
