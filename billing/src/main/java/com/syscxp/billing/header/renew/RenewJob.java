package com.syscxp.billing.header.renew;

import com.syscxp.billing.header.balance.*;
import com.syscxp.header.billing.*;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.billing.header.balance.*;
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
                Timestamp expiredTimestamp =renewVO.getExpiredTime();
                if(currentTimestamp.getTime()-expiredTimestamp.getTime()>7*24*60*60*1000l){
                    databaseFacade.getEntityManager().remove(renewVO);
                    databaseFacade.getEntityManager().flush();
                    continue;
                }

                    BigDecimal discountPrice = BigDecimal.ZERO;
                    BigDecimal originalPrice = BigDecimal.ZERO;
                    SimpleQuery<PriceRefRenewVO> queryPriceRefRenewVO = databaseFacade.createQuery(PriceRefRenewVO.class);
                    queryPriceRefRenewVO.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
                    List<PriceRefRenewVO> PriceRefRenewVOs = queryPriceRefRenewVO.list();

                    for (PriceRefRenewVO priceUuid : PriceRefRenewVOs) {
                        ProductPriceUnitVO productPriceUnitVO = databaseFacade.findByUuid(priceUuid.getProductPriceUnitUuid(), ProductPriceUnitVO.class);
                        if (productPriceUnitVO == null) {
                            throw new IllegalArgumentException("price uuid is not valid");
                        }
                        SimpleQuery<AccountDiscountVO> qDiscount = databaseFacade.createQuery(AccountDiscountVO.class);
                        qDiscount.add(AccountDiscountVO_.category, SimpleQuery.Op.EQ, productPriceUnitVO.getCategoryCode());
                        qDiscount.add(AccountDiscountVO_.productType, SimpleQuery.Op.EQ, productPriceUnitVO.getProductTypeCode());
                        qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, renewVO.getAccountUuid());
                        AccountDiscountVO accountDiscountVO = qDiscount.find();
                        int productDiscount = 100;
                        if (accountDiscountVO != null) {
                            productDiscount = accountDiscountVO.getDiscount() == 0 ? 100 : accountDiscountVO.getDiscount();
                        }
                        originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()));
                        discountPrice = discountPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));

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
                    discountPrice = discountPrice.multiply(duration);
                    if (originalPrice.compareTo(mayPayTotal) > 0) {
                        return;
                    }
                    int hash = renewVO.getAccountUuid().hashCode();
                    if (hash < 0) {
                        hash = ~hash;
                    }
                    String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
                    if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
                        if (abvo.getPresentBalance().compareTo(discountPrice) > 0) {
                            BigDecimal presentNow = abvo.getPresentBalance().subtract(discountPrice);
                            abvo.setPresentBalance(presentNow);
                            orderVo.setPayPresent(discountPrice);
                            orderVo.setPayCash(BigDecimal.ZERO);
                            DealDetailVO dealDetailVO = new DealDetailVO();
                            dealDetailVO.setUuid(Platform.getUuid());
                            dealDetailVO.setAccountUuid(renewVO.getAccountUuid());
                            dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                            dealDetailVO.setIncome(BigDecimal.ZERO);
                            dealDetailVO.setExpend(discountPrice.negate());
                            dealDetailVO.setFinishTime(currentTimestamp);
                            dealDetailVO.setType(DealType.DEDUCTION);
                            dealDetailVO.setState(DealState.SUCCESS);
                            dealDetailVO.setBalance(presentNow==null?BigDecimal.ZERO:presentNow);
                            dealDetailVO.setOutTradeNO(outTradeNO);
                            dealDetailVO.setOpAccountUuid(renewVO.getAccountUuid());
                            databaseFacade.getEntityManager().persist(dealDetailVO);

                        } else {
                            BigDecimal payPresent = abvo.getPresentBalance();
                            BigDecimal payCash = discountPrice.subtract(payPresent);
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
                            dVO.setBalance(remainCash==null?BigDecimal.ZERO:remainCash);
                            dVO.setOutTradeNO(outTradeNO+"-2");
                            dVO.setOpAccountUuid(renewVO.getAccountUuid());
                            databaseFacade.getEntityManager().persist(dVO);
                        }
                    } else {
                        BigDecimal remainCashBalance = abvo.getCashBalance().subtract(discountPrice);
                        abvo.setCashBalance(remainCashBalance);
                        orderVo.setPayPresent(BigDecimal.ZERO);
                        orderVo.setPayCash(discountPrice);

                        DealDetailVO dVO = new DealDetailVO();
                        dVO.setUuid(Platform.getUuid());
                        dVO.setAccountUuid(renewVO.getAccountUuid());
                        dVO.setDealWay(DealWay.CASH_BILL);
                        dVO.setIncome(BigDecimal.ZERO);
                        dVO.setExpend(discountPrice.negate());
                        dVO.setFinishTime(currentTimestamp);
                        dVO.setType(DealType.DEDUCTION);
                        dVO.setState(DealState.SUCCESS);
                        dVO.setBalance(remainCashBalance==null?BigDecimal.ZERO:remainCashBalance);
                        dVO.setOutTradeNO(outTradeNO);
                        dVO.setOpAccountUuid(renewVO.getAccountUuid());
                        databaseFacade.getEntityManager().persist(dVO);
                    }
                    orderVo.setType(OrderType.RENEW);
                    orderVo.setOriginalPrice(originalPrice);
                    orderVo.setPrice(discountPrice);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentTimestamp);
                    calendar.add(Calendar.MONTH, duration.intValue());
                    orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));
                    orderVo.setProductEffectTimeStart(currentTimestamp);


                    Timestamp endTime = new Timestamp(calendar.getTime().getTime());
                    long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
                    renewVO.setPricePerDay(renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays)).add(discountPrice).divide(BigDecimal.valueOf(notUseDays).add(duration),4,BigDecimal.ROUND_HALF_EVEN));
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
