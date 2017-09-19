package org.zstack.billing.header.renew;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.billing.header.balance.*;
import org.zstack.billing.header.order.*;
import org.zstack.core.Platform;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.GLock;
import org.zstack.core.db.SimpleQuery;
import org.zstack.header.billing.OrderState;
import org.zstack.header.billing.OrderType;
import org.zstack.header.billing.OrderVO;
import org.zstack.header.billing.ProductChargeModel;
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
    @Transactional
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        GLock lock = new GLock(String.format("id-%s", "createRenew"), 120);
        lock.lock();
        try {
            Timestamp currentTimestamp = databaseFacade.getCurrentSqlTime();

            SimpleQuery<RenewVO> q = databaseFacade.createQuery(RenewVO.class);
            q.add(RenewVO_.isRenewAuto, SimpleQuery.Op.EQ, true);
            List<RenewVO> renewVOs = q.list();
            logger.info(renewVOs.toString());
            for (RenewVO renewVO : renewVOs) {
                Timestamp expiredTimestamp = currentTimestamp;//todo get from product by productUuid
                if (expiredTimestamp.getTime() <= currentTimestamp.getTime() && (currentTimestamp.getTime() - expiredTimestamp.getTime()) < 7 * 24 * 60 * 60 * 1000l) {

                    BigDecimal dischargePrice = BigDecimal.ZERO;
                    BigDecimal originalPrice = BigDecimal.ZERO;
                    SimpleQuery<PriceRefRenewVO> queryPriceRefRenewVO = databaseFacade.createQuery(PriceRefRenewVO.class);
                    queryPriceRefRenewVO.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
                    List<PriceRefRenewVO> PriceRefRenewVOs = queryPriceRefRenewVO.list();

                    for (PriceRefRenewVO priceUuid : PriceRefRenewVOs) {
                        ProductPriceUnitVO productPriceUnitVO = databaseFacade.findByUuid(priceUuid.getProductPriceUnitUuid(), ProductPriceUnitVO.class);
                        if (productPriceUnitVO == null) {
                            throw new IllegalArgumentException("price uuid is not valid");
                        }
                        SimpleQuery<AccountDischargeVO> qDischarge = databaseFacade.createQuery(AccountDischargeVO.class);
                        qDischarge.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, productPriceUnitVO.getCategory());
                        qDischarge.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, productPriceUnitVO.getProductType());
                        qDischarge.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, renewVO.getAccountUuid());
                        AccountDischargeVO accountDischargeVO = qDischarge.find();
                        int productDisCharge = 100;
                        if (accountDischargeVO != null) {
                            productDisCharge = accountDischargeVO.getDisCharge() == 0 ? 100 : accountDischargeVO.getDisCharge();
                        }
                        originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()));
                        dischargePrice = dischargePrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(BigDecimal.valueOf(productDisCharge)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));

                    }

                    BigDecimal duration = BigDecimal.ONE;
                    if (renewVO.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)) {
                        duration = duration.multiply(BigDecimal.valueOf(12));
                    }

                    AccountBalanceVO abvo = databaseFacade.findByUuid(renewVO.getAccountUuid(), AccountBalanceVO.class);
                    BigDecimal cashBalance = abvo.getCashBalance();
                    BigDecimal presentBalance = abvo.getPresentBalance();
                    BigDecimal creditPoint = abvo.getCreditPoint();
                    BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

                    OrderVO orderVo = new OrderVO();

                    originalPrice = originalPrice.multiply(duration);
                    dischargePrice = dischargePrice.multiply(duration);
                    if (originalPrice.compareTo(mayPayTotal) > 0) {
                        return;
                    }
                    int hash = renewVO.getAccountUuid().hashCode();
                    if (hash < 0) {
                        hash = ~hash;
                    }
                    String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
                    if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
                        if (abvo.getPresentBalance().compareTo(dischargePrice) > 0) {
                            BigDecimal presentNow = abvo.getPresentBalance().subtract(dischargePrice);
                            abvo.setPresentBalance(presentNow);
                            orderVo.setPayPresent(dischargePrice);
                            orderVo.setPayCash(BigDecimal.ZERO);
                            DealDetailVO dealDetailVO = new DealDetailVO();
                            dealDetailVO.setUuid(Platform.getUuid());
                            dealDetailVO.setAccountUuid(renewVO.getAccountUuid());
                            dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                            dealDetailVO.setIncome(BigDecimal.ZERO);
                            dealDetailVO.setExpend(dischargePrice.negate());
                            dealDetailVO.setFinishTime(currentTimestamp);
                            dealDetailVO.setType(DealType.DEDUCTION);
                            dealDetailVO.setState(DealState.SUCCESS);
                            dealDetailVO.setBalance(presentNow);
                            dealDetailVO.setOutTradeNO(outTradeNO);
                            dealDetailVO.setOpAccountUuid(renewVO.getAccountUuid());
                            databaseFacade.getEntityManager().persist(dealDetailVO);

                        } else {
                            BigDecimal payPresent = abvo.getPresentBalance();
                            BigDecimal payCash = dischargePrice.subtract(payPresent);
                            BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                            abvo.setCashBalance(remainCash);
                            abvo.setPresentBalance(BigDecimal.ZERO);
                            orderVo.setPayPresent(payPresent);

                            DealDetailVO dealDetailVO = new DealDetailVO();
                            dealDetailVO.setUuid(Platform.getUuid());
                            dealDetailVO.setAccountUuid(renewVO.getAccountUuid());
                            dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                            dealDetailVO.setIncome(BigDecimal.ZERO);
                            dealDetailVO.setExpend(payPresent.negate());
                            dealDetailVO.setFinishTime(currentTimestamp);
                            dealDetailVO.setType(DealType.DEDUCTION);
                            dealDetailVO.setState(DealState.SUCCESS);
                            dealDetailVO.setBalance(BigDecimal.ZERO);
                            dealDetailVO.setOutTradeNO(outTradeNO+"-1");
                            dealDetailVO.setOpAccountUuid(renewVO.getAccountUuid());
                            databaseFacade.getEntityManager().persist(dealDetailVO);

                            orderVo.setPayCash(payCash);

                            DealDetailVO dVO = new DealDetailVO();
                            dVO.setUuid(Platform.getUuid());
                            dVO.setAccountUuid(renewVO.getAccountUuid());
                            dVO.setDealWay(DealWay.CASH_BILL);
                            dVO.setIncome(BigDecimal.ZERO);
                            dVO.setExpend(payCash.negate());
                            dVO.setFinishTime(currentTimestamp);
                            dVO.setType(DealType.DEDUCTION);
                            dVO.setState(DealState.SUCCESS);
                            dVO.setBalance(remainCash);
                            dVO.setOutTradeNO(outTradeNO+"-2");
                            dVO.setOpAccountUuid(renewVO.getAccountUuid());
                            databaseFacade.getEntityManager().persist(dVO);
                        }
                    } else {
                        BigDecimal remainCashBalance = abvo.getCashBalance().subtract(dischargePrice);
                        abvo.setCashBalance(remainCashBalance);
                        orderVo.setPayPresent(BigDecimal.ZERO);
                        orderVo.setPayCash(dischargePrice);

                        DealDetailVO dVO = new DealDetailVO();
                        dVO.setUuid(Platform.getUuid());
                        dVO.setAccountUuid(renewVO.getAccountUuid());
                        dVO.setDealWay(DealWay.CASH_BILL);
                        dVO.setIncome(BigDecimal.ZERO);
                        dVO.setExpend(dischargePrice.negate());
                        dVO.setFinishTime(currentTimestamp);
                        dVO.setType(DealType.DEDUCTION);
                        dVO.setState(DealState.SUCCESS);
                        dVO.setBalance(remainCashBalance);
                        dVO.setOutTradeNO(outTradeNO);
                        dVO.setOpAccountUuid(renewVO.getAccountUuid());
                        databaseFacade.getEntityManager().persist(dVO);
                    }
                    orderVo.setType(OrderType.RENEW);
                    orderVo.setOriginalPrice(originalPrice);
                    orderVo.setPrice(dischargePrice);
                    //todo modify product from tunel
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTimestamp);
                    calendar.add(Calendar.MONTH, duration.intValue());
                    orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));


                    Timestamp startTime = new Timestamp(currentTimestamp.getTime() - 30 * 24 * 60 * 60 * 1000);//todo this would get from product
                    Timestamp endTime = new Timestamp(currentTimestamp.getTime() + 30 * 24 * 60 * 60 * 1000);//todo this would get from product
                    long notUseDays = (endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
                    renewVO.setPricePerDay(renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays)).add(dischargePrice).divide(BigDecimal.valueOf(notUseDays).add(duration),4,BigDecimal.ROUND_HALF_EVEN));
                    databaseFacade.getEntityManager().merge(renewVO);

                    orderVo.setUuid(Platform.getUuid());
                    orderVo.setAccountUuid(renewVO.getAccountUuid());
                    orderVo.setProductName(renewVO.getProductName());
                    orderVo.setState(OrderState.PAID);
                    orderVo.setProductType(renewVO.getProductType());
                    orderVo.setProductChargeModel(renewVO.getProductChargeModel());
                    orderVo.setPayTime(currentTimestamp);
                    orderVo.setProductDescription(renewVO.getProductDescription());
                    orderVo.setProductUuid(renewVO.getProductUuid());
                    orderVo.setDuration(1);

                    databaseFacade.getEntityManager().merge(abvo);
                    databaseFacade.getEntityManager().persist(orderVo);
                    databaseFacade.getEntityManager().flush();
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
