package org.zstack.billing.header.renew;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.zstack.billing.header.balance.AccountBalanceVO;
import org.zstack.billing.header.balance.ProductChargeModel;
import org.zstack.billing.header.order.*;
import org.zstack.billing.manage.BillingErrors;
import org.zstack.billing.manage.BillingManagerImpl;
import org.zstack.billing.manage.BillingServiceException;
import org.zstack.core.Platform;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.GLock;
import org.zstack.core.db.SimpleQuery;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class RenewJob extends QuartzJobBean {

    private DatabaseFacade databaseFacade;
    private ThreadLocal<DatabaseFacade> dbfThreadLocal = new ThreadLocal<DatabaseFacade>();

    private static final CLogger logger = Utils.getLogger(RenewJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        GLock lock = new GLock(String.format("id-%s", "createRenew"), 120);
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
                    ProductChargeModel productChargeModel = r.getProductChargeModel();
                    //todo get product detail from tunnel
                    ProductPriceUnitVO productPriceUnitVO = null;//todo get unitPrice
                    //todo place an order duration one chargeMode
                    AccountBalanceVO abvo = databaseFacade.findByUuid(r.getAccountUuid(), AccountBalanceVO.class);
                    BigDecimal cashBalance = abvo.getCashBalance();
                    BigDecimal presentBalance = abvo.getPresentBalance();
                    BigDecimal creditPoint = abvo.getCreditPoint();
                    BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

                    int productDisCharge = 100; //todo from

                    BigDecimal duration =  r.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)?new BigDecimal(12):BigDecimal.ONE;

                    BigDecimal originalPrice = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(duration);
                    BigDecimal total = originalPrice.multiply(BigDecimal.valueOf(productDisCharge).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));
                    if (total.compareTo(mayPayTotal) > 0) {
                        throw new IllegalArgumentException("have enough money");
                    }
                    OrderVO orderVo = new OrderVO();
                    if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
                        if (abvo.getPresentBalance().compareTo(total) > 0) {
                            BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                            abvo.setPresentBalance(presentNow);
                            orderVo.setPayPresent(total);
                            orderVo.setPayCash(BigDecimal.ZERO);

                        } else {
                            BigDecimal payPresent = abvo.getPresentBalance();
                            BigDecimal payCash = total.subtract(payPresent);
                            BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                            abvo.setCashBalance(remainCash);
                            abvo.setPresentBalance(BigDecimal.ZERO);
                            orderVo.setPayPresent(payPresent);
                            orderVo.setPayCash(payCash);
                        }

                    } else {
                        BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
                        abvo.setCashBalance(remainCashBalance);
                        orderVo.setPayPresent(BigDecimal.ZERO);
                        orderVo.setPayCash(total);
                    }
                    //todo generate product from tunel
                    orderVo.setUuid(Platform.getUuid());
                    orderVo.setAccountUuid(r.getAccountUuid());
                    orderVo.setProductName(r.getProductName());
                    orderVo.setState(OrderState.PAID);
                    orderVo.setProductType(r.getProductType());
                    orderVo.setType(OrderType.RENEW);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTimestamp);
                    calendar.add(Calendar.MONTH, duration.intValue());
                    orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));
                    orderVo.setProductEffectTimeStart(currentTimestamp);

                    orderVo.setProductChargeModel(r.getProductChargeModel());//todo this value would be got from account
                    orderVo.setPayTime(currentTimestamp);
                    orderVo.setProductDiscount(BigDecimal.valueOf(productDisCharge));
                    orderVo.setProductDescription("");//todo from tunnel
                    orderVo.setOriginalPrice(originalPrice);
                    orderVo.setProductUuid(r.getProductUuid());
                    orderVo.setPrice(total);
                    orderVo.setDuration(r.getDuration());

                    databaseFacade.updateAndRefresh(abvo);
                    databaseFacade.persistAndRefresh(orderVo);
                    //callback tunnel to modify effictive time

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
        if (dbfThreadLocal.get() == null) {
            dbfThreadLocal.set(databaseFacade);
            this.databaseFacade = databaseFacade;
        } else {
            this.databaseFacade = dbfThreadLocal.get();
        }
    }

}
