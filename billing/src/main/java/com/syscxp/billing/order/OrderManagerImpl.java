package com.syscxp.billing.order;

import com.syscxp.billing.header.balance.*;
import com.syscxp.billing.header.order.APIUpdateOrderExpiredTimeEvent;
import com.syscxp.billing.header.renew.PriceRefRenewVO;
import com.syscxp.billing.header.renew.PriceRefRenewVO_;
import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.billing.header.renew.RenewVO_;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.header.billing.*;
import com.syscxp.header.rest.TimeoutRestTemplate;
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
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;

import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

public class OrderManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(OrderManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ThreadFacade threadFacade;
    private TimeoutRestTemplate template;

    public OrderManagerImpl() {
        template = RESTFacade.createRestTemplate(CoreGlobalProperty.REST_FACADE_READ_TIMEOUT, CoreGlobalProperty.REST_FACADE_CONNECT_TIMEOUT);
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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

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

        SimpleQuery<RenewVO> queryRenew = dbf.createQuery(RenewVO.class);
        queryRenew.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
        queryRenew.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        RenewVO renewVO = queryRenew.find();
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
    private void payMethod(APIMessage msg, OrderVO orderVo, AccountBalanceVO abvo, BigDecimal total, Timestamp currentTimeStamp) {
        String accountUuid = null;
        String opAccountUuid = null;
        if (msg instanceof APICreateOrderMsg) {
            accountUuid = ((APICreateOrderMsg) msg).getAccountUuid();
            opAccountUuid = ((APICreateOrderMsg) msg).getOpAccountUuid();
        }
        int hash = accountUuid.hashCode();
        if (hash < 0) {
            hash = ~hash;
        }
        String outTradeNO = currentTimeStamp.toString().replaceAll("\\D+", "").concat(String.valueOf(hash));
        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) > 0) {
                BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                abvo.setPresentBalance(presentNow);
                orderVo.setPayPresent(total);
                orderVo.setPayCash(BigDecimal.ZERO);
                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(accountUuid);
                dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(total.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(presentNow);
                dealDetailVO.setOutTradeNO(outTradeNO);
                dealDetailVO.setOpAccountUuid(opAccountUuid);
                dbf.getEntityManager().persist(dealDetailVO);

            } else {
                BigDecimal payPresent = abvo.getPresentBalance();
                BigDecimal payCash = total.subtract(payPresent);
                BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                abvo.setCashBalance(remainCash);
                abvo.setPresentBalance(BigDecimal.ZERO);
                orderVo.setPayPresent(payPresent);

                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(accountUuid);
                dealDetailVO.setDealWay(DealWay.PRESENT_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(payPresent.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(BigDecimal.ZERO);
                dealDetailVO.setOutTradeNO(outTradeNO + "-1");
                dealDetailVO.setOpAccountUuid(opAccountUuid);
                dbf.getEntityManager().persist(dealDetailVO);

                orderVo.setPayCash(payCash);

                DealDetailVO dVO = new DealDetailVO();
                dVO.setUuid(Platform.getUuid());
                dVO.setAccountUuid(accountUuid);
                dVO.setDealWay(DealWay.CASH_BILL);
                dVO.setIncome(BigDecimal.ZERO);
                dVO.setExpend(payCash.negate());
                dVO.setFinishTime(currentTimeStamp);
                dVO.setType(DealType.DEDUCTION);
                dVO.setState(DealState.SUCCESS);
                dVO.setBalance(remainCash);
                dVO.setOutTradeNO(outTradeNO + "-2");
                dVO.setOpAccountUuid(opAccountUuid);
                dbf.getEntityManager().persist(dVO);
            }
        } else {
            BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
            abvo.setCashBalance(remainCashBalance);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setPayCash(total);

            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(accountUuid);
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(BigDecimal.ZERO);
            dVO.setExpend(total.negate());
            dVO.setFinishTime(currentTimeStamp);
            dVO.setType(DealType.DEDUCTION);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(remainCashBalance);
            dVO.setOpAccountUuid(opAccountUuid);
            dVO.setOutTradeNO(outTradeNO);
            dbf.getEntityManager().persist(dVO);
        }
    }

    private BigDecimal getValue(String s, List<ExpenseGross> vos) {
        for (ExpenseGross e : vos) {
            if (s.equals(e.getMon())) {
                return e.getTotal();
            }
        }
        return BigDecimal.ZERO;
    }

    @Transactional
    private void handle(APICreateRenewOrderMsg msg) {

        SimpleQuery<RenewVO> qRenew = dbf.createQuery(RenewVO.class);
        qRenew.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        qRenew.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        RenewVO renewVO = qRenew.find();

        if (renewVO == null) {
            throw new IllegalArgumentException("please input the correct value");
        }
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        BigDecimal dischargePrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        SimpleQuery<PriceRefRenewVO> queryPriceRefRenewVO = dbf.createQuery(PriceRefRenewVO.class);
        queryPriceRefRenewVO.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
        List<PriceRefRenewVO> PriceRefRenewVOs = queryPriceRefRenewVO.list();

        for (PriceRefRenewVO priceUuid : PriceRefRenewVOs) {
            ProductPriceUnitVO productPriceUnitVO = dbf.findByUuid(priceUuid.getProductPriceUnitUuid(), ProductPriceUnitVO.class);
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
            qDischarge.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, productPriceUnitVO.getCategory());
            qDischarge.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, productPriceUnitVO.getProductType());
            qDischarge.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDischargeVO accountDischargeVO = qDischarge.find();
            int productDisCharge = 100;
            if (accountDischargeVO != null) {
                productDisCharge = accountDischargeVO.getDisCharge() == 0 ? 100 : accountDischargeVO.getDisCharge();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()));
            dischargePrice = dischargePrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(BigDecimal.valueOf(productDisCharge)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));

        }
        int originDuration = msg.getDuration();
        BigDecimal duration = BigDecimal.valueOf(originDuration);
        if (msg.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)) {
            duration = duration.multiply(BigDecimal.valueOf(12));
        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
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
        payMethod(msg, orderVo, abvo, dischargePrice, currentTimestamp);
        orderVo.setType(OrderType.RENEW);
        orderVo.setOriginalPrice(originalPrice);
        orderVo.setPrice(dischargePrice);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimestamp);
        calendar.add(Calendar.MONTH, duration.intValue());
        orderVo.setProductEffectTimeEnd(msg.getStartTime());

        LocalDateTime localDateTime = msg.getExpiredTime().toLocalDateTime();
        localDateTime.plusMonths(duration.intValue());
        Timestamp endTime = Timestamp.valueOf(localDateTime);
        orderVo.setProductEffectTimeStart(endTime);
        long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);

        renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());
        renewVO.setProductChargeModel(msg.getProductChargeModel());

        renewVO.setPricePerDay(renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays)).add(dischargePrice).divide(BigDecimal.valueOf(notUseDays).add(duration), 4, BigDecimal.ROUND_HALF_EVEN));
        dbf.getEntityManager().merge(renewVO);

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(renewVO.getAccountUuid());
        orderVo.setProductName(renewVO.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(renewVO.getProductType());
        orderVo.setProductChargeModel(renewVO.getProductChargeModel());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductDescription(renewVO.getProductDescription());
        orderVo.setProductUuid(renewVO.getProductUuid());
        orderVo.setDuration(originDuration);

        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(OrderInventory.valueOf(orderVo));
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APICreateSLACompensationOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        OrderVO orderVo = new OrderVO();
        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setProductChargeModel(ProductChargeModel.BY_DAY);
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setDuration(msg.getDuration());

        orderVo.setPayCash(BigDecimal.ZERO);
        orderVo.setPayPresent(BigDecimal.ZERO);
        orderVo.setType(OrderType.SLA_COMPENSATION);
        orderVo.setOriginalPrice(BigDecimal.ZERO);
        orderVo.setPrice(BigDecimal.ZERO);
        orderVo.setProductEffectTimeStart(msg.getStartTime());
        LocalDateTime localDateTime = msg.getExpiredTime().toLocalDateTime();
        localDateTime.plusDays(msg.getDuration());
        orderVo.setProductEffectTimeEnd(Timestamp.valueOf(localDateTime));
        orderVo.setProductStatus(1);

        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APICreateUnsubcribeOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal dischargePrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();

        OrderVO orderVo = new OrderVO();

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductUuid(msg.getProductUuid());

        Timestamp startTime = msg.getStartTime();
        Timestamp endTime = msg.getExpiredTime();
        long useDays = Math.abs(currentTimestamp.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
        long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
        long days = Math.abs(endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);

        SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
        query.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        query.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        RenewVO renewVO = query.find();
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal valuePayCash = getValueblePayCash(msg.getAccountUuid(), msg.getProductUuid());
        orderVo.setType(OrderType.UN_SUBCRIBE);
        if (remainMoney.compareTo(valuePayCash) < 0) {
            remainMoney = valuePayCash;
        }
        orderVo.setOriginalPrice(remainMoney);
        orderVo.setProductName(msg.getProductName());
        orderVo.setPrice(remainMoney);
        orderVo.setProductEffectTimeEnd(currentTimestamp);
        orderVo.setProductEffectTimeEnd(startTime);
        BigDecimal remainCash = abvo.getCashBalance().add(remainMoney);
        abvo.setCashBalance(remainCash);
        orderVo.setPayPresent(BigDecimal.ZERO);
        orderVo.setPayCash(remainMoney.negate());

        DealDetailVO dVO = new DealDetailVO();
        dVO.setUuid(Platform.getUuid());
        dVO.setAccountUuid(msg.getAccountUuid());
        dVO.setDealWay(DealWay.CASH_BILL);
        dVO.setIncome(remainMoney == null ? BigDecimal.ZERO : remainMoney);
        dVO.setExpend(BigDecimal.ZERO);
        dVO.setFinishTime(currentTimestamp);
        dVO.setType(DealType.REFUND);
        dVO.setState(DealState.SUCCESS);
        dVO.setBalance(remainCash == null ? BigDecimal.ZERO : remainCash);
        dVO.setOutTradeNO(orderVo.getUuid());
        dVO.setOpAccountUuid(msg.getOpAccountUuid());
        dbf.getEntityManager().persist(dVO);
        dbf.getEntityManager().remove(dbf.getEntityManager().find(RenewVO.class, renewVO.getUuid()));
        SimpleQuery<PriceRefRenewVO> q = dbf.createQuery(PriceRefRenewVO.class);
        q.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
        List<PriceRefRenewVO> renewVOs = q.list();
        dbf.removeCollection(renewVOs, PriceRefRenewVO.class);


        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APICreateModifyOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal dischargePrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<ProductPriceUnit> units = msg.getUnits();
        List<String> productPriceUnitUuids = new ArrayList<String>();
        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.category, SimpleQuery.Op.EQ, unit.getCategory());
            q.add(ProductPriceUnitVO_.productType, SimpleQuery.Op.EQ, unit.getProductType());
            q.add(ProductPriceUnitVO_.config, SimpleQuery.Op.EQ, unit.getConfig());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            productPriceUnitUuids.add(productPriceUnitVO.getUuid());

            SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
            qDischarge.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, productPriceUnitVO.getCategory());
            qDischarge.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, productPriceUnitVO.getProductType());
            qDischarge.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDischargeVO accountDischargeVO = qDischarge.find();
            int productDisCharge = 100;
            if (accountDischargeVO != null) {
                productDisCharge = accountDischargeVO.getDisCharge() <= 0 ? 100 : accountDischargeVO.getDisCharge();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()));
            BigDecimal currentDischarge = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(BigDecimal.valueOf(productDisCharge)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            dischargePrice = dischargePrice.add(currentDischarge);

        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

        OrderVO orderVo = new OrderVO();

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setProductUuid(msg.getProductUuid());


        Timestamp startTime = msg.getStartTime();
        Timestamp endTime = msg.getExpiredTime();
        long useDays = Math.abs(currentTimestamp.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);
        long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
        long days = Math.abs(endTime.getTime() - startTime.getTime()) / (1000 * 60 * 60 * 24);

        SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
        query.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        query.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        RenewVO renewVO = query.find();
        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPricePerDay().multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal needPayMoney = dischargePrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal needPayOriginMoney = originalPrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal subMoney = needPayMoney.subtract(remainMoney);
        if (subMoney.compareTo(BigDecimal.ZERO) >= 0) { //upgrade
            if (subMoney.compareTo(mayPayTotal) > 0) {
                throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
            }
            orderVo.setType(OrderType.UPGRADE);
            orderVo.setOriginalPrice(needPayOriginMoney.subtract(remainMoney));
            orderVo.setPrice(subMoney);
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(endTime);
            payMethod(msg, orderVo, abvo, subMoney, currentTimestamp);

        } else { //downgrade
            BigDecimal valuePayCash = getValueblePayCash(msg.getAccountUuid(), msg.getProductUuid());
            orderVo.setType(OrderType.DOWNGRADE);
            if (subMoney.compareTo(valuePayCash.negate()) < 0) {
                subMoney = valuePayCash.negate();
            }
            orderVo.setPayCash(subMoney);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setOriginalPrice(subMoney);
            orderVo.setPrice(subMoney);
            orderVo.setProductEffectTimeStart(currentTimestamp);
            orderVo.setProductEffectTimeEnd(endTime);
            BigDecimal remainCash = abvo.getCashBalance().add(subMoney.negate());
            abvo.setCashBalance(remainCash);

            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getAccountUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(subMoney.negate());
            dVO.setExpend(BigDecimal.ZERO);
            dVO.setFinishTime(currentTimestamp);
            dVO.setType(DealType.REFUND);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(remainCash == null ? BigDecimal.ZERO : remainCash);
            dVO.setOutTradeNO(orderVo.getUuid());
            dVO.setOpAccountUuid(msg.getOpAccountUuid());
            dbf.getEntityManager().persist(dVO);
        }
        renewVO.setPricePerDay(dischargePrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN));
        dbf.getEntityManager().merge(renewVO);
        SimpleQuery<PriceRefRenewVO> q = dbf.createQuery(PriceRefRenewVO.class);
        q.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
        List<PriceRefRenewVO> renewVOs = q.list();
        dbf.removeCollection(renewVOs, PriceRefRenewVO.class);
        for (String productPriceUnitUuid : productPriceUnitUuids) {
            PriceRefRenewVO priceRefRenewVO = new PriceRefRenewVO();
            priceRefRenewVO.setUuid(Platform.getUuid());
            priceRefRenewVO.setProductPriceUnitUuid(productPriceUnitUuid);
            priceRefRenewVO.setAccountUuid(msg.getAccountUuid());
            priceRefRenewVO.setRenewUuid(renewVO.getUuid());
            dbf.getEntityManager().persist(priceRefRenewVO);
        }

        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }


    @Transactional
    private void handle(APICreateBuyOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal dischargePrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<ProductPriceUnit> units = msg.getUnits();
        List<String> productPriceUnitUuids = new ArrayList<String>();
        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.category, SimpleQuery.Op.EQ, unit.getCategory());
            q.add(ProductPriceUnitVO_.productType, SimpleQuery.Op.EQ, unit.getProductType());
            q.add(ProductPriceUnitVO_.config, SimpleQuery.Op.EQ, unit.getConfig());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            productPriceUnitUuids.add(productPriceUnitVO.getUuid());
            SimpleQuery<AccountDischargeVO> qDischarge = dbf.createQuery(AccountDischargeVO.class);
            qDischarge.add(AccountDischargeVO_.category, SimpleQuery.Op.EQ, productPriceUnitVO.getCategory());
            qDischarge.add(AccountDischargeVO_.productType, SimpleQuery.Op.EQ, productPriceUnitVO.getProductType());
            qDischarge.add(AccountDischargeVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDischargeVO accountDischargeVO = qDischarge.find();
            int productDisCharge = 100;
            if (accountDischargeVO != null) {
                productDisCharge = accountDischargeVO.getDisCharge() <= 0 ? 100 : accountDischargeVO.getDisCharge();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()));
            BigDecimal currentDischarge = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(BigDecimal.valueOf(productDisCharge)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            dischargePrice = dischargePrice.add(currentDischarge);

        }

        BigDecimal duration = BigDecimal.valueOf(msg.getDuration());
        if (msg.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)) {
            duration = duration.multiply(BigDecimal.valueOf(12));
        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);//可支付金额

        OrderVO orderVo = new OrderVO();

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setProductChargeModel(msg.getProductChargeModel());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setDuration(msg.getDuration());

        originalPrice = originalPrice.multiply(duration);
        dischargePrice = dischargePrice.multiply(duration);

        if (dischargePrice.compareTo(mayPayTotal) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
        }
        orderVo.setOriginalPrice(originalPrice);
        orderVo.setPrice(dischargePrice);
        orderVo.setType(OrderType.BUY);
        if (msg.getProductType() == ProductType.TUNNEL) {
            orderVo.setProductStatus(0);
        }
        payMethod(msg, orderVo, abvo, dischargePrice, currentTimestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimestamp);
        calendar.add(Calendar.MONTH, duration.intValue());
        orderVo.setProductEffectTimeStart(currentTimestamp);
        orderVo.setProductEffectTimeEnd(new Timestamp(calendar.getTime().getTime()));

        RenewVO renewVO = new RenewVO();
        renewVO.setUuid(Platform.getUuid());
        renewVO.setProductChargeModel(orderVo.getProductChargeModel());
        renewVO.setProductUuid(orderVo.getProductUuid());
        renewVO.setAccountUuid(orderVo.getAccountUuid());
        renewVO.setProductName(orderVo.getProductName());
        renewVO.setProductType(orderVo.getProductType());
        renewVO.setProductDescription(orderVo.getProductDescription());
        renewVO.setRenewAuto(true);
        renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());
        renewVO.setPricePerDay(dischargePrice.divide(BigDecimal.valueOf(30).multiply(duration), 4, BigDecimal.ROUND_HALF_EVEN));
        dbf.getEntityManager().persist(renewVO);

        for (String productPriceUnitUuid : productPriceUnitUuids) {
            PriceRefRenewVO priceRefRenewVO = new PriceRefRenewVO();
            priceRefRenewVO.setUuid(Platform.getUuid());
            priceRefRenewVO.setProductPriceUnitUuid(productPriceUnitUuid);
            priceRefRenewVO.setAccountUuid(msg.getAccountUuid());
            priceRefRenewVO.setRenewUuid(renewVO.getUuid());
            dbf.getEntityManager().persist(priceRefRenewVO);
        }

        NotifyOrderVO notifyOrderVO = new NotifyOrderVO();
        notifyOrderVO.setUuid(Platform.getUuid());
        notifyOrderVO.setUrl(msg.getNotifyUrl());
        notifyOrderVO.setOrderUuid(orderVo.getUuid());
        dbf.getEntityManager().persist(notifyOrderVO);

        dbf.getEntityManager().merge(abvo);
        dbf.getEntityManager().persist(orderVo);
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);

        bus.reply(msg, reply);

    }

    @Transactional(readOnly = true)
    private BigDecimal getValueblePayCash(String accountUuid, String productUuid) {
        BigDecimal total = BigDecimal.ZERO;
        SimpleQuery<OrderVO> query = dbf.createQuery(OrderVO.class);
        query.add(OrderVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        query.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        query.add(OrderVO_.productEffectTimeEnd, SimpleQuery.Op.GT, dbf.getCurrentSqlTime());
        query.orderBy(OrderVO_.createDate, SimpleQuery.Od.DESC);
        List<OrderVO> orderVOs = query.list();
        if (orderVOs.size() == 0) {
            throw new IllegalArgumentException("the productUuid is not valid");
        }
        for (OrderVO orderVO : orderVOs) {
            if (orderVO.getType() == OrderType.DOWNGRADE || orderVO.getType() == OrderType.UN_SUBCRIBE) {
                break;
            }
            total = total.add(orderVO.getPayCash());
        }

        return total;

    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_ORDER);
    }

    @Override
    public boolean start() {
        try {

        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        }
        return true;
    }


    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        if (msg instanceof APICreateOrderMsg) {
            validate((APICreateOrderMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateOrderMsg msg) {

    }



}
