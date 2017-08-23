package org.zstack.billing.header.identity.renew;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.zstack.billing.header.identity.order.APICreateOrderMsg;
import org.zstack.billing.header.identity.order.OrderType;
import org.zstack.billing.manage.BillingManagerImpl;
import org.zstack.core.Platform;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.GLock;
import org.zstack.core.db.SimpleQuery;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.sql.Timestamp;
import java.util.List;

public class RenewJob extends QuartzJobBean {

    private DatabaseFacade databaseFacade;
    private BillingManagerImpl manager;

    private ThreadLocal<DatabaseFacade> connThreadLocal = new ThreadLocal<DatabaseFacade>();

    private static final CLogger logger = Utils.getLogger(RenewJob.class);

    private static final String uuid = Platform.getUuid();

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        GLock lock = new GLock(String.format("id-%s", uuid), 120);
        logger.info(uuid);
        lock.lock();
        try {
            Timestamp currentTimestamp = databaseFacade.getCurrentSqlTime();

            SimpleQuery<RenewVO> q = databaseFacade.createQuery(RenewVO.class);
            q.add(RenewVO_.isRenewAuto, SimpleQuery.Op.EQ, true);
            List<RenewVO> renews = q.list();
            logger.info(renews.toString());
            for (RenewVO r : renews) {
                Timestamp expiredDate = r.getExpiredDate();//todo  would acquire from tunnel
                if (expiredDate.getTime() < currentTimestamp.getTime() && (currentTimestamp.getTime() - expiredDate.getTime()) < 7 * 24 * 60 * 60 * 1000l) {
                    APICreateOrderMsg msg = new APICreateOrderMsg();
                    msg.setDuration(r.getDuration());
                    msg.setPriceUnitUuid(r.getProductUnitPriceUuid());
                    msg.setProductChargeModel(r.getProductChargeModel());
                    msg.setProductUuid(r.getProductUuid());
                    msg.setProductName(r.getProductName());
                    msg.setProductType(r.getProductType());
                    msg.setType(OrderType.RENEW);
                    msg.setProductDescription("{test:test}");//todo would from tunnel
                    manager.createOrder(msg);
                }
            }

        } finally {
            lock.unlock();
        }

    }

    public DatabaseFacade getDatabaseFacade() {
        return databaseFacade;
    }

    public void setDatabaseFacade(DatabaseFacade databaseFacade) {
        if (connThreadLocal.get() == null) {
            connThreadLocal.set(databaseFacade);
            this.databaseFacade = databaseFacade;
        } else {
            this.databaseFacade = connThreadLocal.get();
        }
    }

    public BillingManagerImpl getManager() {
        return manager;
    }

    public void setManager(BillingManagerImpl manager) {
        this.manager = manager;
    }
}
