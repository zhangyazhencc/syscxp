package com.syscxp.billing.order;

import com.syscxp.billing.balance.DealDetailVOHelper;
import com.syscxp.billing.header.balance.*;
import com.syscxp.billing.header.order.APIUpdateOrderExpiredTimeEvent;
import com.syscxp.billing.header.renew.PriceRefRenewVO;
import com.syscxp.billing.header.renew.PriceRefRenewVO_;
import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.billing.header.renew.RenewVO_;
import com.syscxp.billing.header.sla.SLACompensateVO;
import com.syscxp.billing.header.sla.SLALogVO;
import com.syscxp.billing.header.sla.SLALogVO_;
import com.syscxp.billing.header.sla.SLAState;
import com.syscxp.header.billing.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.billing.header.order.APIUpdateOrderExpiredTimeMsg;
import com.syscxp.billing.BillingErrors;
import com.syscxp.billing.BillingServiceException;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;

import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(OrderManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;

    private AtomicInteger atomicInteger = new AtomicInteger();

    public OrderManagerImpl() {
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }

    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateBuyOrderMsg) {
            handle((APICreateBuyOrderMsg) msg);
        } else if (msg instanceof APICreateRenewOrderMsg) {
            handle((APICreateRenewOrderMsg) msg);
        } else if (msg instanceof APICreateSLACompensationOrderMsg) {
            handle((APICreateSLACompensationOrderMsg) msg);
        } else if (msg instanceof APICreateUnsubcribeOrderMsg) {
            handle((APICreateUnsubcribeOrderMsg) msg);
        } else if (msg instanceof APICreateModifyOrderMsg) {
            handle((APICreateModifyOrderMsg) msg);
        } else if (msg instanceof APIUpdateOrderExpiredTimeMsg) {
            handle((APIUpdateOrderExpiredTimeMsg) msg);
        } else if (msg instanceof APIGetHasNotifyMsg) {
            handle((APIGetHasNotifyMsg) msg);
        } else if (msg instanceof APIGetUnscribeProductPriceDiffMsg) {
            handle((APIGetUnscribeProductPriceDiffMsg) msg);
        } else if (msg instanceof APIGetModifyProductPriceDiffMsg) {
            handle((APIGetModifyProductPriceDiffMsg) msg);
        } else if (msg instanceof APIGetProductPriceMsg) {
            handle((APIGetProductPriceMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetHasNotifyMsg msg) {
        APIGetHasNotifyReply reply = new APIGetHasNotifyReply();
        reply.setInventory(hasFailureNotify(msg.getAccountUuid(), msg.getProductUuid()));
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APIUpdateOrderExpiredTimeMsg msg) {
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        query.add(OrderVO_.productStatus, SimpleQuery.Op.EQ, 0);
        OrderVO orderVO = query.find();
        if (orderVO == null) {
            throw new RuntimeException("cannot find the order");
        }
        orderVO.setProductEffectTimeStart(msg.getStartTime());
        orderVO.setProductEffectTimeEnd(msg.getEndTime());
        orderVO.setProductStatus(1);

        RenewVO renewVO = getRenewVO(msg.getSession().getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }
        renewVO.setExpiredTime(msg.getEndTime());
        dbf.getEntityManager().merge(renewVO);
        dbf.getEntityManager().persist(orderVO);
        dbf.getEntityManager().flush();
        APIUpdateOrderExpiredTimeEvent event = new APIUpdateOrderExpiredTimeEvent();
        event.setInventory(OrderInventory.valueOf(orderVO));
        bus.publish(event);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void payMethod(String accountUuid, String opAccountUuid, OrderVO orderVo, AccountBalanceVO abvo, BigDecimal total, Timestamp currentTimeStamp) {

        int hash = accountUuid.hashCode() < 0 ? ~accountUuid.hashCode() : accountUuid.hashCode();
        String outTradeNO = currentTimeStamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash)) + atomicInteger.getAndIncrement();
        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) > 0) {
                BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                abvo.setPresentBalance(presentNow);
                orderVo.setPayPresent(total);
                orderVo.setPayCash(BigDecimal.ZERO);
                new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.PRESENT_BILL, BigDecimal.ZERO, total, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, presentNow, outTradeNO, orderVo.getUuid(), opAccountUuid, null,orderVo.getUuid());
            } else {
                BigDecimal payPresent = abvo.getPresentBalance();
                BigDecimal payCash = total.subtract(payPresent);
                BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                abvo.setCashBalance(remainCash);
                abvo.setPresentBalance(BigDecimal.ZERO);
                orderVo.setPayPresent(payPresent);
                orderVo.setPayCash(payCash);

                new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.PRESENT_BILL, BigDecimal.ZERO, payPresent, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, BigDecimal.ZERO, outTradeNO + "-1", orderVo.getUuid(), opAccountUuid, null,orderVo.getUuid());
                new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.CASH_BILL, BigDecimal.ZERO, payCash, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, remainCash, outTradeNO + "-2", orderVo.getUuid(), opAccountUuid, null,orderVo.getUuid());
            }
        } else {
            BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
            abvo.setCashBalance(remainCashBalance);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setPayCash(total);
            new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.CASH_BILL, BigDecimal.ZERO, total, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, remainCashBalance, outTradeNO, orderVo.getUuid(), opAccountUuid, null,orderVo.getUuid());
        }
    }

    @Transactional
    private void handle(APICreateRenewOrderMsg msg) {

        RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("please input the correct value");
        }

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

        OrderVO orderVo = new OrderVO();
        setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), msg.getDuration(), msg.getCallBackData());
        //discountPrice = discountPrice.multiply(duration);//按现在的价格续费
        BigDecimal discountPrice = renewVO.getPriceOneMonth().multiply(duration);//按上次买的价格续费
        if (discountPrice.compareTo(mayPayTotal) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
        }
        payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, discountPrice, currentTimestamp);
        orderVo.setType(OrderType.RENEW);
        orderVo.setOriginalPrice(discountPrice);
        orderVo.setPrice(discountPrice);
        orderVo.setProductEffectTimeStart(msg.getExpiredTime());
        orderVo.setProductEffectTimeEnd(Timestamp.valueOf(msg.getExpiredTime().toLocalDateTime().plusMonths(duration.intValue())));

        renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());
        renewVO.setProductChargeModel(msg.getProductChargeModel());

        dbf.getEntityManager().merge(renewVO);
        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        saveNotifyOrderVO(msg, orderVo.getUuid());
        dbf.getEntityManager().flush();
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(OrderInventory.valueOf(orderVo));
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateSLACompensationOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        OrderVO orderVo = new OrderVO();
        setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), ProductChargeModel.BY_DAY, currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), 0, msg.getCallBackData());

        orderVo.setPayCash(BigDecimal.ZERO);
        orderVo.setPayPresent(BigDecimal.ZERO);
        orderVo.setType(OrderType.SLA_COMPENSATION);
        orderVo.setOriginalPrice(BigDecimal.ZERO);
        orderVo.setPrice(BigDecimal.ZERO);

        orderVo.setProductEffectTimeStart(msg.getExpiredTime());
        orderVo.setProductEffectTimeEnd(Timestamp.valueOf(msg.getExpiredTime().toLocalDateTime().plusDays(msg.getDuration())));
        orderVo.setProductStatus(1);

        RenewVO renewVO = getRenewVO(msg.getProductUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("please input the correct value");
        }
        renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());

        updateSLA(msg.getSlaUuid(), orderVo.getProductEffectTimeStart(), orderVo.getProductEffectTimeEnd());
        saveSLALogVO(msg.getAccountUuid(), msg.getProductUuid(), msg.getDuration(), orderVo.getProductEffectTimeStart(), msg.getExpiredTime(), renewVO.getPriceOneMonth());
        dbf.getEntityManager().merge(renewVO);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        saveNotifyOrderVO(msg, orderVo.getUuid());
        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void updateSLA(String uuid, Timestamp startTime, Timestamp endTime) {
        SLACompensateVO slaCompensateVO = dbf.findByUuid(uuid, SLACompensateVO.class);
        slaCompensateVO.setTimeStart(startTime);
        slaCompensateVO.setTimeEnd(endTime);
        slaCompensateVO.setState(SLAState.DONE);
        dbf.getEntityManager().merge(slaCompensateVO);

    }


    @Transactional
    private void saveSLALogVO(String accountUuid, String productUuid, int duration, Timestamp startTime, Timestamp endTime, BigDecimal priceOnMonth) {
        SLALogVO slaLogVO = new SLALogVO();
        slaLogVO.setUuid(Platform.getUuid());
        slaLogVO.setAccountUuid(accountUuid);
        slaLogVO.setProductUuid(productUuid);
        slaLogVO.setDuration(duration);
        slaLogVO.setTimeStart(startTime);
        slaLogVO.setTimeEnd(endTime);
        slaLogVO.setSlaPrice(priceOnMonth);
        dbf.getEntityManager().persist(slaLogVO);
    }

    @Transactional
    private void handle(APICreateUnsubcribeOrderMsg msg) {

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);

        BigDecimal notUseMonth = getNotUseMonths(currentTimestamp.toLocalDateTime(),msg.getExpiredTime().toLocalDateTime());
        OrderVO orderVo = new OrderVO();
        setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), ProductChargeModel.BY_MONTH, currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), notUseMonth.intValue(), msg.getCallBackData());
        orderVo.setType(OrderType.UN_SUBCRIBE);

        RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
        BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());

        remainMoney = remainMoney.subtract(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), BigDecimal.ZERO));
        if (remainMoney.compareTo(valuePayCash) > 0) {
            remainMoney = valuePayCash;
        }
        BigDecimal refundPresent = BigDecimal.ZERO;
        updateMoneyIfCreateFailure(msg.isCreateFailure(), msg.getAccountUuid(), msg.getProductUuid(), remainMoney, refundPresent);

        orderVo.setOriginalPrice(remainMoney);
        orderVo.setPrice(remainMoney);
        orderVo.setProductEffectTimeStart(msg.getStartTime());
        orderVo.setProductEffectTimeEnd(msg.getExpiredTime());
        BigDecimal remainCash = abvo.getCashBalance().add(remainMoney);
        abvo.setCashBalance(remainCash);
        orderVo.setPayPresent(refundPresent);
        orderVo.setPayCash(remainMoney.negate());
        int hash = msg.getAccountUuid().hashCode() < 0 ? ~msg.getAccountUuid().hashCode() : msg.getAccountUuid().hashCode();
        String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.CASH_BILL, remainMoney, BigDecimal.ZERO, currentTimestamp, DealType.REFUND, DealState.SUCCESS, remainCash, outTradeNO, orderVo.getUuid(), msg.getOpAccountUuid(), null,orderVo.getUuid());
        deletePriceRefRenews(renewVO.getUuid());
        dbf.getEntityManager().remove(dbf.getEntityManager().merge(renewVO));

        saveNotifyOrderVO(msg, orderVo.getUuid());
        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void updateMoneyIfCreateFailure(boolean isCreateFailure, String accountUuid, String productUuid, BigDecimal remainMoney, BigDecimal refundPresent) {
        if (isCreateFailure) {
            OrderVO refundOrder = getOrderVO(accountUuid, productUuid);
            if (refundOrder == null) {
                throw new IllegalArgumentException("can not find this product buy history ,please check up");
            }
            remainMoney = refundOrder.getPayCash();
            refundPresent = refundOrder.getPayPresent();
        }
    }

    @Transactional
    private void handle(APICreateModifyOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
        BigDecimal discountPrice = orderTempProp.getDiscountPrice();
        BigDecimal originalPrice = orderTempProp.getOriginalPrice();
        List<String> productPriceUnitUuids = orderTempProp.getProductPriceUnitUuids();

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额

        OrderVO orderVo = new OrderVO();
        setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), null, currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), 0, msg.getCallBackData());

        Timestamp endTime = msg.getExpiredTime();
        BigDecimal notUseMonth = getNotUseMonths(currentTimestamp.toLocalDateTime(),msg.getExpiredTime().toLocalDateTime());

        RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
        BigDecimal needPayMoney = discountPrice.multiply(notUseMonth);
        BigDecimal needPayOriginMoney = originalPrice.multiply(notUseMonth);
        BigDecimal subMoney = needPayMoney.subtract(remainMoney);
        orderVo.setProductEffectTimeStart(currentTimestamp);
        orderVo.setProductEffectTimeEnd(endTime);

        if (subMoney.compareTo(BigDecimal.ZERO) >= 0) { //upgrade
            if (subMoney.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            orderVo.setType(OrderType.UPGRADE);
            orderVo.setOriginalPrice(needPayOriginMoney.subtract(remainMoney));
            orderVo.setPrice(subMoney);
            payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, subMoney, currentTimestamp);

        } else { //downgrade
            BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());
            orderVo.setType(OrderType.DOWNGRADE);
            subMoney = subMoney.add(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), discountPrice));
            if (subMoney.compareTo(valuePayCash.negate()) < 0) {
                subMoney = valuePayCash.negate();
            }
            orderVo.setPayCash(subMoney);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setOriginalPrice(subMoney);
            orderVo.setPrice(subMoney);

            BigDecimal remainCash = abvo.getCashBalance().add(subMoney.negate());
            abvo.setCashBalance(remainCash);
            int hash = msg.getAccountUuid().hashCode() < 0 ? ~msg.getAccountUuid().hashCode() : msg.getAccountUuid().hashCode();
            String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
            new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.CASH_BILL, subMoney.negate(), BigDecimal.ZERO, currentTimestamp, DealType.REFUND, DealState.SUCCESS, remainCash, outTradeNO, orderVo.getUuid(), msg.getOpAccountUuid(), null,orderVo.getUuid());

        }
        renewVO.setPriceOneMonth(discountPrice);

        updatePriceRefRenews(msg.getAccountUuid(), renewVO.getUuid(), productPriceUnitUuids);
        saveNotifyOrderVO(msg, orderVo.getUuid());

        dbf.getEntityManager().merge(renewVO);
        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void updatePriceRefRenews(String renewUuid, String accountUuid, List<String> productPriceUnitUuids) {
        deletePriceRefRenews(renewUuid);
        savePriceRefRenewVO(productPriceUnitUuids, accountUuid, renewUuid);
    }

    @Transactional
    private void deletePriceRefRenews(String renewUuid) {
        List<PriceRefRenewVO> priceRefRenewVOS = getPriceRefRenewVOs(renewUuid);
        if (priceRefRenewVOS != null && priceRefRenewVOS.size() > 0) {
            for (PriceRefRenewVO priceRefRenewVO : priceRefRenewVOS) {
                dbf.getEntityManager().remove(dbf.getEntityManager().merge(priceRefRenewVO));
            }
        }
    }

    @Transactional
    private List<PriceRefRenewVO> getPriceRefRenewVOs(String renewUuid) {
        SimpleQuery<PriceRefRenewVO> q = dbf.createQuery(PriceRefRenewVO.class);
        q.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewUuid);
        return q.list();
    }


    @Transactional
    private BigDecimal getDownGradeDiffMoney(String accountUuid, String productUuid, BigDecimal priceDownTo) {
        List<SLALogVO> slaLogVOS = getSLALogVO(accountUuid, productUuid);
        BigDecimal returnMoney = BigDecimal.ZERO;
        if (slaLogVOS != null && slaLogVOS.size() > 0) {
            for (SLALogVO slaLogVO : slaLogVOS) {
                LocalDateTime startTime = slaLogVO.getTimeStart().toLocalDateTime().minusDays(1);
                BigDecimal duration = getNotUseMonths(startTime, slaLogVO.getTimeEnd().toLocalDateTime());
                if (startTime.isBefore(LocalDateTime.now())) {
                    duration =  getNotUseMonths(LocalDateTime.now(), slaLogVO.getTimeEnd().toLocalDateTime());
                }
                if (priceDownTo.compareTo(slaLogVO.getSlaPrice()) < 0) {
                    returnMoney = returnMoney.add(slaLogVO.getSlaPrice().subtract(priceDownTo).multiply(duration));
                    slaLogVO.setSlaPrice(priceDownTo);
                    slaLogVO.setTimeStart(slaLogVO.getTimeStart());
                    slaLogVO.setTimeEnd(slaLogVO.getTimeEnd());
                    dbf.getEntityManager().merge(slaLogVO);
                }
            }
        }
        return returnMoney;
    }

    @Transactional
    private List<SLALogVO> getSLALogVO(String accountUuid, String productUuid) {
        SimpleQuery<SLALogVO> query = dbf.createQuery(SLALogVO.class);
        query.add(SLALogVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(SLALogVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        return query.list();
    }

    @Transactional
    private void saveNotifyOrderVO(APICreateOrderMsg msg, String orderUuid) {
        saveNotify(msg.getNotifyUrl(), msg.getAccountUuid(), msg.getProductUuid(), orderUuid);
    }

    @Transactional
    private void saveNotifyBuyOrderVO(ProductInfoForOrder msg, String orderUuid) {
        saveNotify(msg.getNotifyUrl(), msg.getAccountUuid(), msg.getProductUuid(), orderUuid);
    }

    @Transactional
    private void saveNotify(String notifyUrl, String accountUuid, String productUuid, String orderUuid) {
        NotifyOrderVO notifyOrderVO = new NotifyOrderVO();
        notifyOrderVO.setUuid(Platform.getUuid());
        notifyOrderVO.setUrl(notifyUrl);
        notifyOrderVO.setOrderUuid(orderUuid);
        notifyOrderVO.setStatus(NotifyOrderStatus.FAILURE);
        notifyOrderVO.setNotifyTimes(0);
        notifyOrderVO.setAccountUuid(accountUuid);
        notifyOrderVO.setProductUuid(productUuid);
        dbf.getEntityManager().persist(notifyOrderVO);
    }

    @Transactional
    private BigDecimal getNotUseMonths(LocalDateTime stateTime, LocalDateTime expiredTime) {
        long months = ChronoUnit.MONTHS.between(stateTime, expiredTime);
        long days = ChronoUnit.DAYS.between(stateTime, expiredTime.minusMonths(months));
        BigDecimal thisMonthDays = BigDecimal.valueOf(stateTime.toLocalDate().lengthOfMonth());
        return BigDecimal.valueOf(months).add(BigDecimal.valueOf(days).divide(thisMonthDays, 4, RoundingMode.HALF_UP)).setScale(2,RoundingMode.HALF_UP);
    }

    private boolean hasFailureNotify(String accountUuid, String productUuid) {
        SimpleQuery<NotifyOrderVO> q = dbf.createQuery(NotifyOrderVO.class);
        q.add(NotifyOrderVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        q.add(NotifyOrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        q.add(NotifyOrderVO_.status, SimpleQuery.Op.EQ, NotifyOrderStatus.FAILURE);
        List<NotifyOrderVO> notifyOrderVOS = q.list();
        if (notifyOrderVOS != null && notifyOrderVOS.size() > 0) {
            return true;
        }
        return false;
    }

    @Transactional
    private ProductCategoryVO getProductCategoryVO(Category categoryCode, ProductType productType) {
        SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
        query.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, categoryCode);
        query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, productType);
        return query.find();
    }


    @Transactional
    private BigDecimal getValuablePayCash(String accountUuid, String productUuid) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderVO> orderVOs = getValidOrder(accountUuid, productUuid);
        if (orderVOs == null || orderVOs.size() == 0) {//有有效订单才能退费
            throw new IllegalArgumentException("the productUuid is not valid");
        }
        for (OrderVO orderVO : orderVOs) {
            total = total.add(orderVO.getPayCash());
        }
        return total;
    }

    @Transactional
    private List<OrderVO> getValidOrder(String accountUuid, String productUuid) {
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        query.add(OrderVO_.productEffectTimeEnd, SimpleQuery.Op.GT, dbf.getCurrentSqlTime());
        query.orderBy(OrderVO_.createDate, SimpleQuery.Od.DESC);
        return query.list();
    }

    private void handle(APIGetUnscribeProductPriceDiffMsg msg) {
        APIGetUnscribeProductPriceDiffReply reply = new APIGetUnscribeProductPriceDiffReply();

        BigDecimal notUseMonth = getNotUseMonths(dbf.getCurrentSqlTime().toLocalDateTime(),msg.getExpiredTime().toLocalDateTime());
        RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
        BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());
        remainMoney = remainMoney.subtract(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), BigDecimal.ZERO));
        if (remainMoney.compareTo(valuePayCash) > 0) {
            remainMoney = valuePayCash;
        }
        BigDecimal refundPresent = BigDecimal.ZERO;
        updateMoneyIfCreateFailure(msg.isCreateFailure(), msg.getAccountUuid(), msg.getProductUuid(), remainMoney, refundPresent);
        reply.setReFoundMoney(refundPresent);
        reply.setInventory(remainMoney);
        bus.reply(msg, reply);
    }

    @Transactional
    private OrderVO getOrderVO(String accountUuid, String productUuid) {
        SimpleQuery<OrderVO> queryRefund = dbf.createQuery(OrderVO.class);
        queryRefund.add(OrderVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        queryRefund.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        queryRefund.add(OrderVO_.type, SimpleQuery.Op.EQ, OrderType.BUY);
        return queryRefund.find();
    }

    @Transactional
    private void handle(APIGetModifyProductPriceDiffMsg msg) {

        RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }
        OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
        BigDecimal discountPrice = orderTempProp.getDiscountPrice();
        BigDecimal originalPrice = orderTempProp.getOriginalPrice();

        BigDecimal notUseMonth = getNotUseMonths(dbf.getCurrentSqlTime().toLocalDateTime(),msg.getExpiredTime().toLocalDateTime());
        BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
        BigDecimal needPayMoney = discountPrice.multiply(notUseMonth);
        BigDecimal needPayOriginMoney = originalPrice.multiply(notUseMonth);
        BigDecimal subMoney = needPayMoney.subtract(remainMoney);

        if (subMoney.compareTo(BigDecimal.ZERO) < 0) {//downgrade substract sla
            subMoney = subMoney.add(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), discountPrice));
        }

        APIGetModifyProductPriceDiffReply reply = new APIGetModifyProductPriceDiffReply();
        reply.setNeedPayMoney(needPayMoney);
        reply.setRemainMoney(remainMoney);
        reply.setNeedPayOriginMoney(needPayOriginMoney);
        reply.setSubMoney(subMoney);
        bus.reply(msg, reply);
    }

    @Transactional
    private RenewVO getRenewVO(String accountUuid, String productUuid) {
        SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
        query.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        return query.find();
    }

    @Transactional
    private void handle(APICreateBuyOrderMsg apiCreateBuyOrderMsg) {

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        List<OrderInventory> inventories = new ArrayList<>();

        for (ProductInfoForOrder msg : apiCreateBuyOrderMsg.getProducts()) {

            OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
            BigDecimal discountPrice = orderTempProp.getDiscountPrice();
            BigDecimal originalPrice = orderTempProp.getOriginalPrice();
            List<String> productPriceUnitUuids = orderTempProp.getProductPriceUnitUuids();
            BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());

            AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
            BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额

            originalPrice = originalPrice.multiply(duration);
            BigDecimal discountPriceOneMonth = discountPrice;
            discountPrice = discountPrice.multiply(duration);

            if (discountPrice.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            OrderVO orderVo = new OrderVO();
            setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), msg.getDuration(), msg.getCallBackData());
            orderVo.setOriginalPrice(originalPrice);
            orderVo.setPrice(discountPrice);
            orderVo.setType(OrderType.BUY);
            if (msg.getProductType() == ProductType.TUNNEL) {
                orderVo.setProductStatus(0);
            }
            payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, discountPrice, currentTimestamp);
            LocalDateTime expiredTime = LocalDateTime.now().plusMonths(duration.intValue());
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(Timestamp.valueOf(expiredTime));

            RenewVO renewVO = saveRenewVO(orderVo.getProductChargeModel(), orderVo.getProductUuid(), orderVo.getAccountUuid(), orderVo.getProductName(), orderVo.getProductType(), orderVo.getDescriptionData(), Timestamp.valueOf(expiredTime), discountPriceOneMonth);
            savePriceRefRenewVO(productPriceUnitUuids, msg.getAccountUuid(), renewVO.getUuid());

            if (!StringUtils.isEmpty(msg.getNotifyUrl())) {
                saveNotifyBuyOrderVO(msg, orderVo.getUuid());
            }

            dbf.getEntityManager().merge(abvo);
            dbf.getEntityManager().persist(orderVo);
            dbf.getEntityManager().flush();
            inventories.add(OrderInventory.valueOf(orderVo));
        }

        APICreateBuyOrderReply reply = new APICreateBuyOrderReply();
        reply.setInventories(inventories);
        bus.reply(apiCreateBuyOrderMsg, reply);

    }

    @Transactional
    private void savePriceRefRenewVO(List<String> productPriceUnitUuids, String accountUuid, String renewUuid) {
        for (String productPriceUnitUuid : productPriceUnitUuids) {
            PriceRefRenewVO priceRefRenewVO = new PriceRefRenewVO();
            priceRefRenewVO.setUuid(Platform.getUuid());
            priceRefRenewVO.setProductPriceUnitUuid(productPriceUnitUuid);
            priceRefRenewVO.setAccountUuid(accountUuid);
            priceRefRenewVO.setRenewUuid(renewUuid);
            dbf.getEntityManager().persist(priceRefRenewVO);
        }
    }

    @Transactional
    private RenewVO saveRenewVO(ProductChargeModel model, String productUuid, String accountUuid, String productName, ProductType productType, String descriptionData, Timestamp expiredTime, BigDecimal discountPrice) {
        RenewVO renewVO = new RenewVO();
        renewVO.setUuid(Platform.getUuid());
        renewVO.setProductChargeModel(model);
        renewVO.setProductUuid(productUuid);
        renewVO.setAccountUuid(accountUuid);
        renewVO.setProductName(productName);
        renewVO.setProductType(productType);
        renewVO.setDescriptionData(descriptionData);
        renewVO.setRenewAuto(true);
        renewVO.setExpiredTime(expiredTime);
        renewVO.setPriceOneMonth(discountPrice);
        dbf.getEntityManager().persist(renewVO);
        return renewVO;
    }

    @Transactional
    private void setOrderValue(OrderVO orderVo, String accountUuid, String productName, ProductType productType, ProductChargeModel productChargeModel, Timestamp currentTimestamp, String descriptionData, String productUuid, int duration, String callbackData) {
        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(accountUuid);
        orderVo.setProductName(productName);
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(productType);
        orderVo.setProductChargeModel(productChargeModel);
        orderVo.setPayTime(currentTimestamp);
        orderVo.setDescriptionData(descriptionData);
        orderVo.setProductUuid(productUuid);
        orderVo.setDuration(duration);
        orderVo.setCallBackData(callbackData);
    }

    @Transactional
    private BigDecimal realDurationToMonth(int duration, ProductChargeModel model) {
        BigDecimal durationMonth = BigDecimal.valueOf(duration);
        if (model.equals(ProductChargeModel.BY_YEAR)) {
            durationMonth = durationMonth.multiply(BigDecimal.valueOf(12));
        }
        return durationMonth;
    }

    @Transactional
    private OrderTempProp calculatePrice(List<ProductPriceUnit> units, String accountUuid) {
        List<String> productPriceUnitUuids = new ArrayList<String>();
        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        for (ProductPriceUnit unit : units) {

            ProductCategoryVO productCategoryVO = getProductCategoryVO(unit.getCategoryCode(), unit.getProductTypeCode());
            if (productCategoryVO == null) {
                throw new IllegalArgumentException("can not find productType or category");
            }

            int base = catECPBaseWidth(unit);

            ProductPriceUnitVO productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), unit.getAreaCode(), unit.getLineCode(), unit.getConfigCode());
            if (productPriceUnitVO == null) {
                productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), unit.getAreaCode(), "DEFAULT", unit.getConfigCode());
                if(productCategoryVO == null){
                    productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), "DEFAULT", "DEFAULT", unit.getConfigCode());
                }
                if(productCategoryVO == null) throw new IllegalArgumentException("can not find the product price in database");
            }

            productPriceUnitUuids.add(productPriceUnitVO.getUuid());

            AccountDiscountVO accountDiscountVO = getAccountDiscountVO(productPriceUnitVO.getProductCategoryUuid(), accountUuid);
            int productDiscount = 100;
            if (accountDiscountVO != null) {
                productDiscount = accountDiscountVO.getDiscount() <= 0 ? 100 : accountDiscountVO.getDiscount();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice() * base));
            BigDecimal currentDiscount = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice() * base).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN).setScale(2,RoundingMode.HALF_UP);
            discountPrice = discountPrice.add(currentDiscount);

        }
        OrderTempProp orderTempProp = new OrderTempProp();
        orderTempProp.setDiscountPrice(discountPrice);
        orderTempProp.setOriginalPrice(originalPrice);
        orderTempProp.setProductPriceUnitUuids(productPriceUnitUuids);
        return orderTempProp;
    }

    @Transactional
    private AccountDiscountVO getAccountDiscountVO(String productCategoryUuid, String accountUuid) {
        SimpleQuery<AccountDiscountVO> qDiscount = dbf.createQuery(AccountDiscountVO.class);
        qDiscount.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryUuid);
        qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        return qDiscount.find();
    }

    @Transactional
    private ProductPriceUnitVO getProductPriceUnitVO(String productCategoryUuid, String areaCode, String lineCode, String configCode) {
        SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
        q.add(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryUuid);
        q.add(ProductPriceUnitVO_.areaCode, SimpleQuery.Op.EQ, areaCode);
        q.add(ProductPriceUnitVO_.lineCode, SimpleQuery.Op.EQ, lineCode);
        q.add(ProductPriceUnitVO_.configCode, SimpleQuery.Op.EQ, configCode);
        return q.find();
    }

    @Transactional
    private int catECPBaseWidth(ProductPriceUnit unit) {
        int base = 1;
        if (unit.getProductTypeCode().equals(ProductType.ECP) && unit.getCategoryCode().equals(Category.BANDWIDTH)) {
            String configCode = unit.getConfigCode().replaceAll("\\D", "");
            base = Integer.parseInt(configCode);
            unit.setConfigCode("1M");
        }
        return base;
    }

    private void handle(APIGetProductPriceMsg msg) {
        List<ProductPriceUnitInventory> productPriceUnits = new ArrayList<>();

        OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
        BigDecimal discountPrice = orderTempProp.getDiscountPrice();
        BigDecimal originalPrice = orderTempProp.getOriginalPrice();
        BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额

        originalPrice = originalPrice.multiply(duration);
        discountPrice = discountPrice.multiply(duration);
        boolean payable = discountPrice.compareTo(mayPayTotal) <= 0;

        APIGetProductPriceReply reply = new APIGetProductPriceReply();
        reply.setProductPriceInventories(productPriceUnits);
        reply.setMayPayTotal(mayPayTotal);
        reply.setOriginalPrice(originalPrice);
        reply.setDiscountPrice(discountPrice);
        reply.setPayable(payable);
        bus.reply(msg, reply);
    }


    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_ORDER);
    }

    @Override
    public boolean start() {
        return true;
    }


    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        if (msg instanceof APICreateBuyOrderMsg) {
            validate((APICreateBuyOrderMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateBuyOrderMsg msg) {

    }

}
