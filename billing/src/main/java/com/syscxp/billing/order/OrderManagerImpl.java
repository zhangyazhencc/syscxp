package com.syscxp.billing.order;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.syscxp.billing.balance.DealDetailVOHelper;
import com.syscxp.billing.header.balance.*;
import com.syscxp.header.billing.APIUpdateOrderExpiredTimeReply;
import com.syscxp.billing.header.renew.PriceRefRenewVO;
import com.syscxp.billing.header.renew.PriceRefRenewVO_;
import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.billing.header.renew.RenewVO_;
import com.syscxp.billing.header.sla.SLACompensateVO;
import com.syscxp.billing.header.sla.SLALogVO;
import com.syscxp.billing.header.sla.SLALogVO_;
import com.syscxp.billing.header.sla.SLAState;
import com.syscxp.core.db.GLock;
import com.syscxp.header.billing.*;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.utils.gson.JSONObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.header.billing.APIUpdateOrderExpiredTimeMsg;
import com.syscxp.billing.BillingErrors;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        } else if (msg instanceof APIGetRenewProductPriceMsg) {
            handle((APIGetRenewProductPriceMsg) msg);
        } else if (msg instanceof APICreateBuyEdgeLineOrderMsg) {
            handle((APICreateBuyEdgeLineOrderMsg) msg);
        }  else if (msg instanceof APICreateBuyIDCOrderMsg) {
            handle((APICreateBuyIDCOrderMsg) msg);
        } else if (msg instanceof APIRefundOrderMsg) {
            handle((APIRefundOrderMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateBuyIDCOrderMsg msg) {
        GLock lock = new GLock(String.format("id-%s", msg.getAccountUuid()), 3);
        lock.lock();
        try {
            Timestamp currentTimestamp = dbf.getCurrentSqlTime();
            AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
            BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额
            BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());
            BigDecimal amount = duration.multiply(BigDecimal.valueOf(msg.getPrice()));
            if (amount.compareTo(mayPayTotal) > 0) {
                throw new OperationFailureException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }

            OrderVO orderVo = new OrderVO();
            setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), ProductType.EDGELINE, msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), msg.getDuration(), msg.getCallBackData());
            orderVo.setOriginalPrice(amount);
            orderVo.setPrice(amount);
            orderVo.setType(OrderType.BUY);
            payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, amount, currentTimestamp);

            LocalDateTime expiredTime =getExpiredTime(msg.getProductChargeModel(),msg.getDuration());
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(Timestamp.valueOf(expiredTime));

            saveRenewVO(orderVo.getProductChargeModel(), orderVo.getProductUuid(), orderVo.getAccountUuid(), orderVo.getProductName(), orderVo.getProductType(), orderVo.getDescriptionData(), Timestamp.valueOf(expiredTime), BigDecimal.valueOf(msg.getPrice()));

            if (!StringUtils.isEmpty(msg.getNotifyUrl())) {
                saveNotify(msg.getNotifyUrl(), msg.getAccountUuid(), msg.getProductUuid(), orderVo.getUuid());
            }

            dbf.getEntityManager().persist(orderVo);
            dbf.getEntityManager().merge(abvo);
            dbf.getEntityManager().flush();
            APICreateBuyIDCOrderReply reply = new APICreateBuyIDCOrderReply();
            reply.setInventory(OrderInventory.valueOf(orderVo));
            bus.reply(msg, reply);
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    private void handle(APIRefundOrderMsg msg) {

        OrderVO orderVO = dbf.findByUuid(msg.getOrderUuid(), OrderVO.class);
        GLock lock = new GLock(String.format("id-%s", orderVO.getAccountUuid()), 3);
        lock.lock();
        try {
            if (orderVO.getState() != OrderState.CANCELED) {
                validRefundOrder(orderVO.getProductUuid(), orderVO.getPayTime());
                AccountBalanceVO abvo = dbf.findByUuid(orderVO.getAccountUuid(), AccountBalanceVO.class);
                abvo.setCashBalance(abvo.getCashBalance().add(orderVO.getPayCash()));
                abvo.setPresentBalance(abvo.getPresentBalance().add(orderVO.getPayPresent()));
                orderVO.setState(OrderState.CANCELED);
                RenewVO renewVO = getRenewVO(orderVO.getAccountUuid(), orderVO.getProductUuid());
                if (renewVO == null) {
                    throw new IllegalArgumentException("could not find the product purchased history ");
                }
                renewVO.setPriceOneMonth(orderVO.getLastPriceOneMonth());
                if (orderVO.getType().equals(OrderType.BUY)) {
                    dbf.getEntityManager().remove(dbf.getEntityManager().merge(renewVO));
                } else if (orderVO.getType().equals(OrderType.UPGRADE) || orderVO.getType().equals(OrderType.DOWNGRADE)) {
                    dbf.getEntityManager().merge(renewVO);
                }
                dbf.getEntityManager().merge(abvo);
                dbf.getEntityManager().merge(orderVO);
                dbf.getEntityManager().flush();
                saveDealDetail(orderVO, abvo);
            }
            APIRefundOrderReply reply = new APIRefundOrderReply();
            reply.setSuccess(true);
            bus.reply(msg, reply);
        } finally {
            lock.unlock();
        }


    }

    private void saveDealDetail(OrderVO orderVO, AccountBalanceVO abvo) {
        int hash = orderVO.getAccountUuid().hashCode() < 0 ? ~orderVO.getAccountUuid().hashCode() : orderVO.getAccountUuid().hashCode();
        String outTradeNO = dbf.getCurrentSqlTime().toString().replaceAll("\\D+", "").concat(String.valueOf(hash)) + atomicInteger.getAndIncrement();
        DealType dealType = DealType.REFUND;
        BigDecimal incomePresent = BigDecimal.ZERO;
        BigDecimal expendPresent = BigDecimal.ZERO;
        BigDecimal incomeCash = BigDecimal.ZERO;
        BigDecimal expendCash = BigDecimal.ZERO;
        if (orderVO.getType().equals(OrderType.DOWNGRADE)) {
            dealType = DealType.DEDUCTION;
            expendPresent = orderVO.getPayPresent();
            expendCash = orderVO.getPayCash();
        } else {
            incomeCash = orderVO.getPayCash();
            incomePresent = orderVO.getPayPresent();
        }
        int index = 0;
        if (orderVO.getPayPresent().compareTo(BigDecimal.ZERO) != 0) {
            new DealDetailVOHelper(dbf).saveDealDetailVO(orderVO.getAccountUuid(), DealWay.PRESENT_BILL, incomePresent, expendPresent, dbf.getCurrentSqlTime(), dealType, DealState.SUCCESS, abvo.getPresentBalance(), outTradeNO + "-" + (index++), orderVO.getUuid(), orderVO.getAccountUuid(), null, orderVO.getUuid(), null);
        }
        if (orderVO.getPayCash().compareTo(BigDecimal.ZERO) != 0) {
            new DealDetailVOHelper(dbf).saveDealDetailVO(orderVO.getAccountUuid(), DealWay.CASH_BILL, incomeCash, expendCash, dbf.getCurrentSqlTime(), dealType, DealState.SUCCESS, abvo.getCashBalance(), outTradeNO + "-" + (index++), orderVO.getUuid(), orderVO.getAccountUuid(), null, orderVO.getUuid(), null);
        }
    }

    private void validRefundOrder(String productUuid, Timestamp payTime) {
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        List<OrderVO> list = query.list();
        list.forEach(order -> {
            if (order.getPayTime().after(payTime)) {
                throw new IllegalArgumentException("have newer order ,this order can not cancel");
            }
        });

    }

    @Transactional
    private void handle(APICreateBuyEdgeLineOrderMsg msg) {
        GLock lock = new GLock(String.format("id-%s", msg.getAccountUuid()), 3);
        lock.lock();
        try {
            Timestamp currentTimestamp = dbf.getCurrentSqlTime();
            AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
            BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额
            BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());
            BigDecimal amount = duration.multiply(BigDecimal.valueOf(msg.getPrice()));
            if (amount.add(BigDecimal.valueOf(msg.getFixedCost())).compareTo(mayPayTotal) > 0) {
                throw new OperationFailureException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }

            OrderVO orderVo = new OrderVO();
            setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), ProductType.EDGELINE, msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), msg.getDuration(), msg.getCallBackData());
            orderVo.setOriginalPrice(amount);
            orderVo.setPrice(amount);
            orderVo.setType(OrderType.BUY);
            if (msg.getFixedCost() > 0) {
                OrderVO fixedOrder = new OrderVO();
                setOrderValue(fixedOrder, msg.getAccountUuid(), msg.getProductName(), ProductType.EDGELINE, msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), 0, msg.getCallBackData());
                fixedOrder.setOriginalPrice(BigDecimal.valueOf(msg.getFixedCost()));
                fixedOrder.setPrice(BigDecimal.valueOf(msg.getFixedCost()));
                fixedOrder.setType(OrderType.BUY);
                fixedOrder.setProductEffectTimeStart(currentTimestamp);
                fixedOrder.setProductEffectTimeEnd(currentTimestamp);
                payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), fixedOrder, abvo, BigDecimal.valueOf(msg.getFixedCost()), currentTimestamp);
                dbf.getEntityManager().persist(fixedOrder);
            }
            payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, amount, currentTimestamp);

            LocalDateTime expiredTime =getExpiredTime(msg.getProductChargeModel(),msg.getDuration());
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(Timestamp.valueOf(expiredTime));

            saveRenewVO(orderVo.getProductChargeModel(), orderVo.getProductUuid(), orderVo.getAccountUuid(), orderVo.getProductName(), orderVo.getProductType(), orderVo.getDescriptionData(), Timestamp.valueOf(expiredTime), BigDecimal.valueOf(msg.getPrice()));

            if (!StringUtils.isEmpty(msg.getNotifyUrl())) {
                saveNotify(msg.getNotifyUrl(), msg.getAccountUuid(), msg.getProductUuid(), orderVo.getUuid());
            }

            dbf.getEntityManager().persist(orderVo);
            dbf.getEntityManager().merge(abvo);
            dbf.getEntityManager().flush();
            APICreateBuyEdgeLineOrderReply reply = new APICreateBuyEdgeLineOrderReply();
            reply.setInventory(OrderInventory.valueOf(orderVo));
            bus.reply(msg, reply);
        } finally {
            lock.unlock();
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
        query.add(OrderVO_.type, SimpleQuery.Op.EQ, OrderType.BUY);
        query.add(OrderVO_.productStatus, SimpleQuery.Op.EQ, 0);
        OrderVO orderVO = query.find();
        if (orderVO == null) {
            throw new RuntimeException("cannot find the order");
        }
        orderVO.setProductEffectTimeStart(msg.getStartTime());
        orderVO.setProductEffectTimeEnd(msg.getEndTime());
        orderVO.setProductStatus(1);

        RenewVO renewVO = getRenewVO(orderVO.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }
        renewVO.setExpiredTime(msg.getEndTime());
        dbf.getEntityManager().merge(renewVO);
        dbf.getEntityManager().merge(orderVO);
        dbf.getEntityManager().flush();
        APIUpdateOrderExpiredTimeReply reply = new APIUpdateOrderExpiredTimeReply();
        reply.setInventory(OrderInventory.valueOf(orderVO));
        bus.reply(msg, reply);
    }

    @Transactional
    private void payMethod(String accountUuid, String opAccountUuid, OrderVO orderVo, AccountBalanceVO abvo, BigDecimal total, Timestamp currentTimeStamp) {

        int hash = accountUuid.hashCode() < 0 ? ~accountUuid.hashCode() : accountUuid.hashCode();
        String outTradeNO = currentTimeStamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash)) + atomicInteger.getAndIncrement();
        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) >= 0) {
                BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                abvo.setPresentBalance(presentNow);
                orderVo.setPayPresent(total);
                orderVo.setPayCash(BigDecimal.ZERO);
                new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.PRESENT_BILL, BigDecimal.ZERO, total, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, presentNow, outTradeNO, orderVo.getUuid(), opAccountUuid, null, orderVo.getUuid(), null);
            } else {
                BigDecimal payPresent = abvo.getPresentBalance();
                BigDecimal payCash = total.subtract(payPresent);
                BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                abvo.setCashBalance(remainCash);
                abvo.setPresentBalance(BigDecimal.ZERO);
                orderVo.setPayPresent(payPresent);
                orderVo.setPayCash(payCash);

                new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.PRESENT_BILL, BigDecimal.ZERO, payPresent, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, BigDecimal.ZERO, outTradeNO + "-1", orderVo.getUuid(), opAccountUuid, null, orderVo.getUuid(), null);
                new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.CASH_BILL, BigDecimal.ZERO, payCash, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, remainCash, outTradeNO + "-2", orderVo.getUuid(), opAccountUuid, null, orderVo.getUuid(), null);
            }
        } else {
            BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
            abvo.setCashBalance(remainCashBalance);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setPayCash(total);
            new DealDetailVOHelper(dbf).saveDealDetailVO(accountUuid, DealWay.CASH_BILL, BigDecimal.ZERO, total, currentTimeStamp, DealType.DEDUCTION, DealState.SUCCESS, remainCashBalance, outTradeNO, orderVo.getUuid(), opAccountUuid, null, orderVo.getUuid(), null);
        }
    }

    @Transactional
    private void handle(APICreateRenewOrderMsg msg) {

        APICreateOrderReply reply = new APICreateOrderReply();

        GLock lock = new GLock(String.format("autorenew-%s", msg.getProductUuid()), 10);
        lock.lock();
        GLock lock_account = new GLock(String.format("id-%s", msg.getAccountUuid()), 3);
        lock_account.lock();
        try {

            RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
            if (renewVO == null) {
                throw new IllegalArgumentException("续费产品不存在");
            }

            Timestamp currentTimestamp = dbf.getCurrentSqlTime();

            if (msg.isAutoRenew()) {
                if (!currentTimestamp.after(renewVO.getExpiredTime())) {
                    bus.reply(msg, reply);
                    return;
                }
            }

            BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());

            AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
            BigDecimal cashBalance = abvo.getCashBalance();
            BigDecimal presentBalance = abvo.getPresentBalance();
            BigDecimal creditPoint = abvo.getCreditPoint();
            BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

            OrderVO orderVo = new OrderVO();

            setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), msg.getDuration(), msg.getCallBackData());
            //BigDecimal discountPrice = renewVO.getPriceOneMonth().multiply(duration);//按上次买的价格续费
            BigDecimal discountPrice = renewVO.getPriceDiscount().multiply(duration);//-- 按续费的价格续费可能会调低
            if (discountPrice.compareTo(mayPayTotal) > 0) {
                throw new OperationFailureException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, discountPrice, currentTimestamp);

            if (msg.isAutoRenew()) {
                orderVo.setType(OrderType.AUTORENEW);
            } else {
                orderVo.setType(OrderType.RENEW);
            }

            orderVo.setOriginalPrice(discountPrice);
            orderVo.setPrice(discountPrice);
            orderVo.setProductEffectTimeStart(msg.getExpiredTime());
            orderVo.setProductEffectTimeEnd(Timestamp.valueOf(getExpiredTime(msg.getProductChargeModel(),msg.getDuration())));
            orderVo.setLastPriceOneMonth(renewVO.getPriceOneMonth());
            renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());
            renewVO.setProductChargeModel(msg.getProductChargeModel());
            renewVO.setPriceOneMonth(renewVO.getPriceDiscount());

            dbf.getEntityManager().merge(renewVO);
            dbf.getEntityManager().merge(abvo);
            dbf.getEntityManager().persist(orderVo);
            saveNotifyOrderVO(msg, orderVo.getUuid());
            dbf.getEntityManager().flush();

            reply.setInventory(OrderInventory.valueOf(orderVo));
        } finally {
            lock.unlock();
            lock_account.unlock();
        }

        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateSLACompensationOrderMsg msg) {

        GLock lock = new GLock(String.format("id-%s", msg.getAccountUuid()), 3);
        lock.lock();
        try {
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
            orderVo.setProductStatus(0);

            RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
            if (renewVO == null) {
                throw new IllegalArgumentException("please input the correct value");
            }
            renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());

            updateSLA(msg.getSlaUuid(), orderVo.getProductEffectTimeStart(), orderVo.getProductEffectTimeEnd());
            saveSLALogVO(msg.getAccountUuid(), msg.getProductUuid(), msg.getDuration(), orderVo.getProductEffectTimeStart(), orderVo.getProductEffectTimeEnd(), renewVO.getPriceOneMonth());
            dbf.getEntityManager().merge(renewVO);
            dbf.getEntityManager().persist(orderVo);
            dbf.getEntityManager().flush();

            saveNotifyOrderVO(msg, orderVo.getUuid());
            OrderInventory inventory = OrderInventory.valueOf(orderVo);
            APICreateOrderReply reply = new APICreateOrderReply();
            reply.setInventory(inventory);
            bus.reply(msg, reply);
        } finally {
            lock.unlock();
        }

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

        GLock lock = new GLock(String.format("id-%s", msg.getAccountUuid()), 3);
        lock.lock();
        try {
            Timestamp currentTimestamp = dbf.getCurrentSqlTime();
            AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
            if (currentTimestamp.toLocalDateTime().isAfter(msg.getExpiredTime().toLocalDateTime())) {
                throw new IllegalArgumentException("the expired time is gt now");
            }

            BigDecimal notUseMonth = getNotUseMonths(currentTimestamp.toLocalDateTime(), msg.getExpiredTime().toLocalDateTime());
            OrderVO orderVo = new OrderVO();
            setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), ProductChargeModel.BY_MONTH, currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), notUseMonth.intValue(), msg.getCallBackData());
            orderVo.setType(OrderType.UN_SUBCRIBE);

            RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
            if (renewVO == null) {
                throw new IllegalArgumentException("could not find the product purchased history ");
            }

            BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
            BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());

            remainMoney = remainMoney.subtract(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), BigDecimal.ZERO, true));
            if (remainMoney.compareTo(valuePayCash) > 0) {
                remainMoney = valuePayCash;
            }
            BigDecimal refundPresent = BigDecimal.ZERO;
            OrderVO buyOrder = updateMoneyIfCreateFailure(msg.getAccountUuid(), msg.getProductUuid());

            if (msg.isCreateFailure()) {
                remainMoney = buyOrder.getPayCash();
                refundPresent = buyOrder.getPayPresent();
            }
            orderVo.setOriginalPrice(remainMoney);
            orderVo.setPrice(remainMoney);
            orderVo.setProductEffectTimeStart(msg.getStartTime());
            orderVo.setProductEffectTimeEnd(msg.getExpiredTime());
            orderVo.setLastPriceOneMonth(renewVO.getPriceOneMonth());
            BigDecimal remainCash = abvo.getCashBalance().add(remainMoney);
            abvo.setCashBalance(remainCash);
            orderVo.setPayPresent(refundPresent);
            orderVo.setPayCash(remainMoney.negate());
            int hash = msg.getAccountUuid().hashCode() < 0 ? ~msg.getAccountUuid().hashCode() : msg.getAccountUuid().hashCode();
            String outTradeNO = currentTimestamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
            new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.CASH_BILL, remainMoney, BigDecimal.ZERO, currentTimestamp, DealType.REFUND, DealState.SUCCESS, remainCash, outTradeNO, orderVo.getUuid(), msg.getOpAccountUuid(), null, orderVo.getUuid(), null);
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
        } finally {
            lock.unlock();
        }

    }

    @Transactional
    private OrderVO updateMoneyIfCreateFailure(String accountUuid, String productUuid) {
        OrderVO refundOrder = getOrderVO(accountUuid, productUuid);
        if (refundOrder == null) {
            throw new IllegalArgumentException("can not find this product buy history ,please check up");
        }
        return refundOrder;
    }

    @Transactional
    private void handle(APICreateModifyOrderMsg msg) {
        GLock lock = new GLock(String.format("id-%s", msg.getAccountUuid()), 3);
        lock.lock();
        try {
            Timestamp currentTimestamp = dbf.getCurrentSqlTime();

            OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
            BigDecimal discountPrice = orderTempProp.getDiscountPrice();
            BigDecimal originalPrice = orderTempProp.getOriginalPrice();
            List<String> productPriceUnitUuids = orderTempProp.getProductPriceUnitUuids();

            AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
            BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额

            OrderVO orderVo = new OrderVO();
            setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), null, currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), 0, msg.getCallBackData());
            orderVo.setProductPriceDiscountDetail(JSONObjectUtil.toJsonString(orderTempProp.getOrderPriceDiscountDetails()));
            RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
            if (renewVO == null) {
                throw new IllegalArgumentException("could not find the product purchased history ");
            }

            BigDecimal notUseMonth = getNotUseMonths(currentTimestamp.toLocalDateTime(), msg.getExpiredTime().toLocalDateTime());
            BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
            BigDecimal needPayMoney = discountPrice.multiply(notUseMonth);
            BigDecimal subMoney = needPayMoney.subtract(remainMoney);
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(msg.getExpiredTime());
            orderVo.setLastPriceOneMonth(renewVO.getPriceOneMonth());

            if (subMoney.compareTo(BigDecimal.ZERO) >= 0) { //upgrade
                if (notUseMonth.intValue() == 0) {
                    orderVo.setType(OrderType.DOWNGRADE);
                }else{
                    orderVo.setType(OrderType.UPGRADE);
                }

                notUseMonth = getNotUseMonths(currentTimestamp.toLocalDateTime().minusDays(1), msg.getExpiredTime().toLocalDateTime());
                remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
                needPayMoney = discountPrice.multiply(notUseMonth);
                BigDecimal needPayOriginMoney = originalPrice.multiply(notUseMonth);
                subMoney = needPayMoney.subtract(remainMoney);
                if (subMoney.compareTo(mayPayTotal) > 0) {
                    throw new OperationFailureException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
                }


                orderVo.setOriginalPrice(needPayOriginMoney.subtract(remainMoney));
                orderVo.setPrice(subMoney);
                payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, subMoney, currentTimestamp);

            } else { //downgrade
                BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());
                orderVo.setType(OrderType.DOWNGRADE);
                subMoney = subMoney.add(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), discountPrice, true));
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
                new DealDetailVOHelper(dbf).saveDealDetailVO(msg.getAccountUuid(), DealWay.CASH_BILL, subMoney.negate(), BigDecimal.ZERO, currentTimestamp, DealType.REFUND, DealState.SUCCESS, remainCash, outTradeNO, orderVo.getUuid(), msg.getOpAccountUuid(), null, orderVo.getUuid(), null);

            }
            renewVO.setPriceOneMonth(discountPrice);
            renewVO.setPriceDiscount(discountPrice);
            renewVO.setDescriptionData(orderVo.getDescriptionData());

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
        } finally {
            lock.unlock();
        }

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
    private BigDecimal getDownGradeDiffMoney(String accountUuid, String productUuid, BigDecimal priceDownTo, boolean isUpdate) {
        List<SLALogVO> slaLogVOS = getSLALogVO(accountUuid, productUuid);
        BigDecimal returnMoney = BigDecimal.ZERO;
        if (slaLogVOS != null && slaLogVOS.size() > 0) {
            for (SLALogVO slaLogVO : slaLogVOS) {
                LocalDateTime startTime = slaLogVO.getTimeStart().toLocalDateTime();
                BigDecimal duration = getNotUseMonths(startTime, slaLogVO.getTimeEnd().toLocalDateTime());
                if (startTime.isBefore(LocalDateTime.now())) {
                    duration = getNotUseMonths(LocalDateTime.now().minusDays(1), slaLogVO.getTimeEnd().toLocalDateTime());
                }
                if (priceDownTo.compareTo(slaLogVO.getSlaPrice()) < 0) {
                    returnMoney = returnMoney.add(slaLogVO.getSlaPrice().subtract(priceDownTo).multiply(duration));
                    if (isUpdate) {
                        slaLogVO.setSlaPrice(priceDownTo);
                        slaLogVO.setTimeStart(slaLogVO.getTimeStart());
                        slaLogVO.setTimeEnd(slaLogVO.getTimeEnd());
                        dbf.getEntityManager().merge(slaLogVO);
                    }

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
        long months = Math.abs(ChronoUnit.MONTHS.between(stateTime, expiredTime));
        long days = 0;
        if (months > 0) {
            days = Math.abs(ChronoUnit.DAYS.between(stateTime, expiredTime.minusMonths(months)));
        } else {
            days = Math.abs(ChronoUnit.DAYS.between(stateTime, expiredTime));
        }
        BigDecimal thisMonthDays = BigDecimal.valueOf(stateTime.toLocalDate().lengthOfMonth());
        return BigDecimal.valueOf(months).add(BigDecimal.valueOf(days).divide(thisMonthDays, 4, RoundingMode.HALF_UP)).setScale(4, RoundingMode.HALF_UP);
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
    private ProductCategoryVO getProductCategoryVO(ProductCategory categoryCode, ProductType productType) {
        SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
        query.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, categoryCode);
        query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, productType);
        return query.find();
    }


    @Transactional
    private BigDecimal getValuablePayCash(String accountUuid, String productUuid) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderVO> orderVOs = getValidOrder(accountUuid, productUuid);
        if (orderVOs != null && orderVOs.size() > 0) {//有有效订单才能退费
            for (OrderVO orderVO : orderVOs) {
                total = total.add(orderVO.getPayCash());
            }
        }
        return total;
    }

    @Transactional
    private List<OrderVO> getValidOrder(String accountUuid, String productUuid) {
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        query.add(OrderVO_.state, SimpleQuery.Op.EQ, OrderState.PAID);
        query.add(OrderVO_.productEffectTimeEnd, SimpleQuery.Op.GT, dbf.getCurrentSqlTime());
        query.orderBy(OrderVO_.createDate, SimpleQuery.Od.DESC);
        return query.list();
    }

    private void handle(APIGetUnscribeProductPriceDiffMsg msg) {
        APIGetUnscribeProductPriceDiffReply reply = new APIGetUnscribeProductPriceDiffReply();
        if (dbf.getCurrentSqlTime().toLocalDateTime().isAfter(msg.getExpiredTime().toLocalDateTime())) {
            reply.setReFoundMoney(BigDecimal.ZERO);
            reply.setInventory(BigDecimal.ZERO);
        } else {
            BigDecimal notUseMonth = getNotUseMonths(dbf.getCurrentSqlTime().toLocalDateTime(), msg.getExpiredTime().toLocalDateTime());
            RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
            if (renewVO == null) {
                throw new IllegalArgumentException("could not find the product purchased history ");
            }

            BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
            BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());
            remainMoney = remainMoney.subtract(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), BigDecimal.ZERO, false));
            if (remainMoney.compareTo(valuePayCash) > 0) {
                remainMoney = valuePayCash;
            }
            BigDecimal refundPresent = BigDecimal.ZERO;
            OrderVO buyOrder = updateMoneyIfCreateFailure(msg.getAccountUuid(), msg.getProductUuid());
            if (msg.isCreateFailure()) {
                remainMoney = buyOrder.getPayCash();
                refundPresent = buyOrder.getPayPresent();
            }
            reply.setReFoundMoney(refundPresent);
            reply.setInventory(remainMoney);
        }

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

        BigDecimal notUseMonth = getNotUseMonths(dbf.getCurrentSqlTime().toLocalDateTime(), msg.getExpiredTime().toLocalDateTime());
        BigDecimal remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
        BigDecimal needPayMoney = discountPrice.multiply(notUseMonth);
        BigDecimal needPayOriginMoney = originalPrice.multiply(notUseMonth);
        BigDecimal subMoney = needPayMoney.subtract(remainMoney);

        if (subMoney.compareTo(BigDecimal.ZERO) >= 0) { //upgrade

            notUseMonth = getNotUseMonths(dbf.getCurrentSqlTime().toLocalDateTime().minusDays(1), msg.getExpiredTime().toLocalDateTime());
            remainMoney = renewVO.getPriceOneMonth().multiply(notUseMonth);
            needPayMoney = discountPrice.multiply(notUseMonth);
            needPayOriginMoney = originalPrice.multiply(notUseMonth);
            subMoney = needPayMoney.subtract(remainMoney);

        } else { //downgrade
            BigDecimal valuePayCash = getValuablePayCash(msg.getAccountUuid(), msg.getProductUuid());
            subMoney = subMoney.add(getDownGradeDiffMoney(msg.getAccountUuid(), msg.getProductUuid(), discountPrice, false));
            if (subMoney.compareTo(valuePayCash.negate()) < 0) {
                subMoney = valuePayCash.negate();
            }

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
        String accountUuid = apiCreateBuyOrderMsg.getProducts().get(0).getAccountUuid();
        GLock lock = new GLock(String.format("id-%s", accountUuid), 3);
        lock.lock();
        try {
            AccountBalanceVO abvo = dbf.findByUuid(accountUuid, AccountBalanceVO.class);
            BigDecimal mayPayTotal = BigDecimal.ZERO;
            for (ProductInfoForOrder msg : apiCreateBuyOrderMsg.getProducts()) {

                OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
                BigDecimal discountPrice = orderTempProp.getDiscountPrice();
                BigDecimal originalPrice = orderTempProp.getOriginalPrice();
                List<String> productPriceUnitUuids = orderTempProp.getProductPriceUnitUuids();
                BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());

                if (abvo != null)
                    mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额

                originalPrice = originalPrice.multiply(duration);
                BigDecimal discountPriceOneMonth = discountPrice;
                discountPrice = discountPrice.multiply(duration);

                if (discountPrice.compareTo(mayPayTotal) > 0) {
                    throw new OperationFailureException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
                }
                OrderVO orderVo = new OrderVO();
                setOrderValue(orderVo, msg.getAccountUuid(), msg.getProductName(), msg.getProductType(), msg.getProductChargeModel(), currentTimestamp, msg.getDescriptionData(), msg.getProductUuid(), msg.getDuration(), msg.getCallBackData());
                orderVo.setOriginalPrice(originalPrice);
                orderVo.setPrice(discountPrice);
                orderVo.setType(OrderType.BUY);
                orderVo.setProductPriceDiscountDetail(JSONObjectUtil.toJsonString(orderTempProp.getOrderPriceDiscountDetails()));
                if (msg.getProductType() == ProductType.TUNNEL) {
                    orderVo.setProductStatus(0);
                }
                payMethod(msg.getAccountUuid(), msg.getOpAccountUuid(), orderVo, abvo, discountPrice, currentTimestamp);
                LocalDateTime expiredTime =getExpiredTime(msg.getProductChargeModel(),msg.getDuration());
                orderVo.setProductEffectTimeStart(currentTimestamp);
                orderVo.setProductEffectTimeEnd(Timestamp.valueOf(expiredTime));

                RenewVO renewVO = saveRenewVO(orderVo.getProductChargeModel(), orderVo.getProductUuid(), orderVo.getAccountUuid(), orderVo.getProductName(), orderVo.getProductType(), orderVo.getDescriptionData(), Timestamp.valueOf(expiredTime), discountPriceOneMonth);
                savePriceRefRenewVO(productPriceUnitUuids, msg.getAccountUuid(), renewVO.getUuid());

                if (!StringUtils.isEmpty(msg.getNotifyUrl())) {
                    saveNotifyBuyOrderVO(msg, orderVo.getUuid());
                }

                dbf.getEntityManager().persist(orderVo);

                inventories.add(OrderInventory.valueOf(orderVo));
            }
            dbf.getEntityManager().merge(abvo);
            dbf.getEntityManager().flush();
            APICreateBuyOrderReply reply = new APICreateBuyOrderReply();
            reply.setInventories(inventories);
            bus.reply(apiCreateBuyOrderMsg, reply);
        } finally {
            lock.unlock();
        }

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
        renewVO.setPriceDiscount(discountPrice);
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
        }else if (model.equals(ProductChargeModel.BY_WEEK)) {
            durationMonth = durationMonth.divide(BigDecimal.valueOf(30),4,BigDecimal.ROUND_HALF_DOWN).multiply(BigDecimal.valueOf(7));
        }
        return durationMonth;
    }

    @Transactional
    private OrderTempProp calculatePrice(List<ProductPriceUnit> units, String accountUuid) {
        List<String> productPriceUnitUuids = new ArrayList<String>();
        List<OrderPriceDiscountDetail> orderPriceDiscountDetails = new ArrayList<>();
        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        for (ProductPriceUnit unit : units) {
            OrderPriceDiscountDetail orderPriceDiscountDetail = new OrderPriceDiscountDetail();
            ProductCategoryVO productCategoryVO = getProductCategoryVO(unit.getCategoryCode(), unit.getProductTypeCode());
            if (productCategoryVO == null) {
                throw new IllegalArgumentException("can not find productType or category");
            }

            int base = catECPBaseWidth(unit);

            ProductPriceUnitVO productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), unit.getAreaCode(), unit.getLineCode(), unit.getConfigCode());

            if (unit.getCategoryCode().equals(ProductCategory.ABROAD) && productPriceUnitVO == null) {
                String lineCode = unit.getLineCode();
                List<String> lineCodes = Splitter.on("/").splitToList(lineCode);
                if (lineCodes.size() == 2) {
                    String newLineCode = Joiner.on("/").join(lineCodes.get(1), lineCodes.get(0));
                    productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), unit.getAreaCode(), newLineCode, unit.getConfigCode());
                }
            }

            if (productPriceUnitVO == null) {
                productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), unit.getAreaCode(), "DEFAULT", unit.getConfigCode());
                if (productPriceUnitVO == null) {
                    productPriceUnitVO = getProductPriceUnitVO(productCategoryVO.getUuid(), "DEFAULT", "DEFAULT", unit.getConfigCode());
                }
                if (productPriceUnitVO == null)
                    throw new IllegalArgumentException("can not find the product price in database");
            }

            productPriceUnitUuids.add(productPriceUnitVO.getUuid());

            AccountDiscountVO accountDiscountVO = getAccountDiscountVO(productPriceUnitVO.getProductCategoryUuid(), accountUuid);
            int productDiscount = 100;
            if (accountDiscountVO != null) {
                productDiscount = accountDiscountVO.getDiscount() <= 0 ? 100 : accountDiscountVO.getDiscount();
            }
            BigDecimal singleOriginPrice = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice() * base);
            originalPrice = originalPrice.add(singleOriginPrice);
            BigDecimal currentDiscount = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice() * base).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_DOWN).setScale(2, RoundingMode.HALF_UP);
            discountPrice = discountPrice.add(currentDiscount);
            orderPriceDiscountDetail.setOriginalPrice(singleOriginPrice);
            orderPriceDiscountDetail.setDiscount(productDiscount);
            orderPriceDiscountDetail.setRealPayPrice(currentDiscount);
            orderPriceDiscountDetails.add(orderPriceDiscountDetail);
            orderPriceDiscountDetail.setConfigName(productPriceUnitVO.getConfigName());

        }
        OrderTempProp orderTempProp = new OrderTempProp();
        orderTempProp.setDiscountPrice(discountPrice);
        orderTempProp.setOriginalPrice(originalPrice);
        orderTempProp.setProductPriceUnitUuids(productPriceUnitUuids);
        orderTempProp.setOrderPriceDiscountDetails(orderPriceDiscountDetails);
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
        if (unit.getCategoryCode().equals(ProductCategory.IP)) {
            String configCode = unit.getConfigCode().replaceAll("\\D", "");
            base = Integer.parseInt(configCode);
            unit.setConfigCode("1IP");
        }else if (unit.getCategoryCode().equals(ProductCategory.BANDWIDTH)) {
            String configCode = unit.getConfigCode().replaceAll("\\D", "");
            base = Integer.parseInt(configCode);
            unit.setConfigCode("1M");
        }

        return base;
    }


    private void handle(APIGetRenewProductPriceMsg msg) {
        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        if (abvo == null) {
            throw new IllegalArgumentException("no enough money to continue");
        }
        BigDecimal mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额
        RenewVO renewVO = getRenewVO(msg.getAccountUuid(), msg.getProductUuid());
        if (renewVO == null) {
            throw new IllegalArgumentException("might your product had caught by ET.");
        }
        BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());
        BigDecimal originalPrice = renewVO.getPriceOneMonth().multiply(duration);
        BigDecimal discountPrice = renewVO.getPriceDiscount().multiply(duration);
        boolean payable = discountPrice.compareTo(mayPayTotal) <= 0;
        APIGetRenewProductPriceReply reply = new APIGetRenewProductPriceReply();
        reply.setMayPayTotal(mayPayTotal);
        reply.setOriginalPrice(originalPrice);
        reply.setDiscountPrice(discountPrice);
        reply.setPayable(payable);
        bus.reply(msg, reply);

    }


    private void handle(APIGetProductPriceMsg msg) {
//        List<ProductPriceUnitInventory> productPriceUnits = new ArrayList<>();

        OrderTempProp orderTempProp = calculatePrice(msg.getUnits(), msg.getAccountUuid());
        BigDecimal discountPrice = orderTempProp.getDiscountPrice();
        BigDecimal originalPrice = orderTempProp.getOriginalPrice();
        BigDecimal duration = realDurationToMonth(msg.getDuration(), msg.getProductChargeModel());

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal mayPayTotal = BigDecimal.ZERO;
        if (abvo != null)
            mayPayTotal = abvo.getCashBalance().add(abvo.getPresentBalance()).add(abvo.getCreditPoint());//可支付金额

        originalPrice = originalPrice.multiply(duration);
        discountPrice = discountPrice.multiply(duration);
        boolean payable = discountPrice.compareTo(mayPayTotal) <= 0;

        APIGetProductPriceReply reply = new APIGetProductPriceReply();
//        reply.setProductPriceInventories(productPriceUnits);
        reply.setMayPayTotal(mayPayTotal);
        reply.setOriginalPrice(originalPrice);
        reply.setDiscountPrice(discountPrice);
        reply.setPayable(payable);
        bus.reply(msg, reply);
    }

    private static LocalDateTime getExpiredTime(ProductChargeModel productChargeModel,int duration){
        try {
            Method m = LocalDateTime.class.getDeclaredMethod(getMethod(productChargeModel),new Class[]{long.class});
            return  (LocalDateTime) m.invoke(LocalDateTime.now(),new Long[]{Long.valueOf(duration)});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static String getMethod(ProductChargeModel productChargeModel) {
        switch (productChargeModel) {
            case BY_DAY:
                return "plusDays";
            case BY_WEEK:
                return "plusWeeks";
            case BY_YEAR:
                return "plusYears";
            case BY_MONTH:
                return "plusMonths";
            default:
                return "plusMonths";
        }
    }


    @Override
    public boolean stop() {
        return true;
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
