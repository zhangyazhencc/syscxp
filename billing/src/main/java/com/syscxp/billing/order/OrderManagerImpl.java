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
        } else if (msg instanceof APIGetHasNotifyMsg) {
            handle((APIGetHasNotifyMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetHasNotifyMsg msg) {
        APIGetHasNotifyReply reply = new APIGetHasNotifyReply();
        reply.setInventory(hasFailureNotify(msg.getAccountUuid(), msg.getProductUuid()));
        bus.reply(msg,reply);
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
                dealDetailVO.setTradeNO(orderVo.getUuid());
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
                dealDetailVO.setTradeNO(orderVo.getUuid());
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
                dVO.setTradeNO(orderVo.getUuid());
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
            dVO.setTradeNO(orderVo.getUuid());
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
        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        SimpleQuery<PriceRefRenewVO> queryPriceRefRenewVO = dbf.createQuery(PriceRefRenewVO.class);
        queryPriceRefRenewVO.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
        List<PriceRefRenewVO> PriceRefRenewVOs = queryPriceRefRenewVO.list();

        for (PriceRefRenewVO priceUuid : PriceRefRenewVOs) {
            ProductPriceUnitVO productPriceUnitVO = dbf.findByUuid(priceUuid.getProductPriceUnitUuid(), ProductPriceUnitVO.class);
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            SimpleQuery<AccountDiscountVO> qDiscount = dbf.createQuery(AccountDiscountVO.class);
            qDiscount.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productPriceUnitVO.getProductCategoryUuid());
            qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDiscountVO accountDiscountVO = qDiscount.find();
            int productDiscount = 100;
            if (accountDiscountVO != null) {
                productDiscount = accountDiscountVO.getDiscount() <= 0 ? 100 : accountDiscountVO.getDiscount();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()));
            discountPrice = discountPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN));

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
        //discountPrice = discountPrice.multiply(duration);//按现在的价格续费
        discountPrice = renewVO.getPriceOneMonth().multiply(duration);//按上次买的价格续费
        if (originalPrice.compareTo(mayPayTotal) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
        }
        payMethod(msg, orderVo, abvo, discountPrice, currentTimestamp);
        orderVo.setType(OrderType.RENEW);
        orderVo.setOriginalPrice(originalPrice);
        orderVo.setPrice(discountPrice);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTimestamp);
        calendar.add(Calendar.MONTH, duration.intValue());
        if(msg.getStartTime().getTime()<msg.getExpiredTime().getTime()){
            orderVo.setProductEffectTimeStart(msg.getExpiredTime());
        }else{
            orderVo.setProductEffectTimeStart(msg.getStartTime());
            msg.setExpiredTime(msg.getStartTime());
        }


        LocalDateTime localDateTime = msg.getExpiredTime().toLocalDateTime();
        localDateTime = localDateTime.plusMonths(duration.intValue());
        Timestamp endTime = Timestamp.valueOf(localDateTime);
        orderVo.setProductEffectTimeEnd(endTime);
        long notUseDays = (endTime.getTime() - currentTimestamp.getTime()) /( 1000 * 60 * 60 * 24);
        notUseDays = notUseDays<0?0:notUseDays;

        renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());
        renewVO.setProductChargeModel(msg.getProductChargeModel());

        //renewVO.setPriceOneMonth(renewVO.getPriceOneMonth().divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays)).add(discountPrice).divide(BigDecimal.valueOf(notUseDays).add(duration.multiply(BigDecimal.valueOf(30))), 4, BigDecimal.ROUND_HALF_EVEN).multiply(BigDecimal.valueOf(30)));
        dbf.getEntityManager().merge(renewVO);

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(renewVO.getAccountUuid());
        orderVo.setProductName(renewVO.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(renewVO.getProductType());
        orderVo.setProductChargeModel(renewVO.getProductChargeModel());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setDescriptionData(renewVO.getDescriptionData());
        orderVo.setProductUuid(renewVO.getProductUuid());
        orderVo.setDuration(originDuration);
        orderVo.setCallBackData(msg.getCallBackData());

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
        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setProductChargeModel(ProductChargeModel.BY_DAY);
        orderVo.setPayTime(currentTimestamp);
        orderVo.setDescriptionData(msg.getDescriptionData());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setDuration(msg.getDuration());
        orderVo.setCallBackData(msg.getCallBackData());

        orderVo.setPayCash(BigDecimal.ZERO);
        orderVo.setPayPresent(BigDecimal.ZERO);
        orderVo.setType(OrderType.SLA_COMPENSATION);
        orderVo.setOriginalPrice(BigDecimal.ZERO);
        orderVo.setPrice(BigDecimal.ZERO);
        if(msg.getStartTime().getTime()<msg.getExpiredTime().getTime()){
            orderVo.setProductEffectTimeStart(msg.getExpiredTime());
        }else{
            orderVo.setProductEffectTimeStart(msg.getStartTime());
            msg.setExpiredTime(msg.getStartTime());
        }
        LocalDateTime localDateTime = msg.getExpiredTime().toLocalDateTime();
        localDateTime = localDateTime.plusDays(msg.getDuration());
        orderVo.setProductEffectTimeEnd(Timestamp.valueOf(localDateTime));
        orderVo.setProductStatus(1);

        dbf.getEntityManager().persist(orderVo);
        saveNotifyOrderVO(msg, orderVo.getUuid());
        dbf.getEntityManager().flush();

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderReply reply = new APICreateOrderReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Transactional
    private void handle(APICreateUnsubcribeOrderMsg msg) {

        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        OrderVO orderVo = new OrderVO();

        orderVo.setUuid(Platform.getUuid());
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setState(OrderState.PAID);
        orderVo.setProductType(msg.getProductType());
        orderVo.setPayTime(currentTimestamp);
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setDescriptionData(msg.getDescriptionData());
        orderVo.setCallBackData(msg.getCallBackData());

        Timestamp startTime = msg.getStartTime();
        Timestamp endTime = msg.getExpiredTime();
        long notUseDays = Math.abs(endTime.getTime() - currentTimestamp.getTime()) / (1000 * 60 * 60 * 24);
        SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
        query.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        query.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        RenewVO renewVO = query.find();

        if (renewVO == null) {
            throw new IllegalArgumentException("could not find the product purchased history ");
        }

        BigDecimal remainMoney = renewVO.getPriceOneMonth().divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal valuePayCash = getValueblePayCash(msg.getAccountUuid(), msg.getProductUuid());
        orderVo.setType(OrderType.UN_SUBCRIBE);
        if (remainMoney.compareTo(valuePayCash) > 0) {
            remainMoney = valuePayCash;
        }
        BigDecimal refundPresent = BigDecimal.ZERO;
        if(msg.isCreateFailure()){
            SimpleQuery<OrderVO> queryRefund = dbf.createQuery(OrderVO.class);
            queryRefund.add(OrderVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            queryRefund.add(OrderVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
            queryRefund.add(OrderVO_.type, SimpleQuery.Op.EQ, OrderType.BUY);
            OrderVO refundOrder = queryRefund.find();
            if(refundOrder == null){
                throw new IllegalArgumentException("can not find this product buy history ,please check up");
            }
            remainMoney = refundOrder.getPayCash();
            refundPresent = refundOrder.getPayPresent();
           dbf.getEntityManager().remove(renewVO);
        }
        orderVo.setOriginalPrice(remainMoney);
        orderVo.setProductName(msg.getProductName());
        orderVo.setPrice(remainMoney);
        orderVo.setProductEffectTimeEnd(currentTimestamp);
        orderVo.setProductEffectTimeEnd(startTime);
        BigDecimal remainCash = abvo.getCashBalance().add(remainMoney);
        abvo.setCashBalance(remainCash);
        orderVo.setPayPresent(refundPresent);
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
        dVO.setTradeNO(orderVo.getUuid());
        dVO.setOpAccountUuid(msg.getOpAccountUuid());
        dbf.getEntityManager().persist(dVO);
        dbf.getEntityManager().remove(dbf.getEntityManager().find(RenewVO.class, renewVO.getUuid()));
        SimpleQuery<PriceRefRenewVO> q = dbf.createQuery(PriceRefRenewVO.class);
        q.add(PriceRefRenewVO_.renewUuid, SimpleQuery.Op.EQ, renewVO.getUuid());
        List<PriceRefRenewVO> renewVOs = q.list();
        dbf.removeCollection(renewVOs, PriceRefRenewVO.class);

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
    private void handle(APICreateModifyOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<ProductPriceUnit> units = msg.getUnits();
        List<String> productPriceUnitUuids = new ArrayList<String>();
        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
            query.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, unit.getCategoryCode());
            query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, unit.getProductTypeCode());
            ProductCategoryVO productCategoryVO = query.find();
            if(productCategoryVO ==null){
                throw new IllegalArgumentException("can not find productType or category");
            }
            int times = 1;

            if(unit.getProductTypeCode().equals(ProductType.ECP) && unit.getCategoryCode().equals(Category.BANDWIDTH)){
                String configCode = unit.getConfigCode().replaceAll("\\D","");
                times = Integer.parseInt(configCode);
                unit.setConfigCode("1M");
            }
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryVO.getUuid());
            q.add(ProductPriceUnitVO_.areaCode, SimpleQuery.Op.EQ, unit.getAreaCode());
            q.add(ProductPriceUnitVO_.lineCode, SimpleQuery.Op.EQ, unit.getLineCode());
            q.add(ProductPriceUnitVO_.configCode, SimpleQuery.Op.EQ, unit.getConfigCode());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            productPriceUnitUuids.add(productPriceUnitVO.getUuid());

            SimpleQuery<AccountDiscountVO> qDiscount = dbf.createQuery(AccountDiscountVO.class);
            qDiscount.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productPriceUnitVO.getProductCategoryUuid());
            qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDiscountVO accountDiscountVO = qDiscount.find();
            int productDiscount = 100;
            if (accountDiscountVO != null) {
                productDiscount = accountDiscountVO.getDiscount() <= 0 ? 100 : accountDiscountVO.getDiscount();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()*times));
            BigDecimal currentDiscount = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            discountPrice = discountPrice.add(currentDiscount);

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
        orderVo.setDescriptionData(msg.getDescriptionData());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setCallBackData(msg.getCallBackData());


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

        BigDecimal remainMoney = renewVO.getPriceOneMonth().divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
        BigDecimal needPayMoney = discountPrice.divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(notUseDays));
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
            dVO.setTradeNO(orderVo.getUuid());
            dVO.setOpAccountUuid(msg.getOpAccountUuid());
            dbf.getEntityManager().persist(dVO);
        }
        renewVO.setPriceOneMonth(discountPrice);
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
    private void saveNotifyOrderVO(APICreateOrderMsg msg, String orderUuid) {
        NotifyOrderVO notifyOrderVO = new NotifyOrderVO();
        notifyOrderVO.setUuid(Platform.getUuid());
        notifyOrderVO.setUrl(msg.getNotifyUrl());
        notifyOrderVO.setOrderUuid(orderUuid);
        notifyOrderVO.setStatus(NotifyOrderStatus.FAILURE);
        notifyOrderVO.setNotifyTimes(0);
        notifyOrderVO.setAccountUuid(msg.getAccountUuid());
        notifyOrderVO.setProductUuid(msg.getProductUuid());
        dbf.getEntityManager().persist(notifyOrderVO);
    }

    private boolean hasFailureNotify(String accountUuid,String productUuid){
        SimpleQuery<NotifyOrderVO> q = dbf.createQuery(NotifyOrderVO.class);
        q.add(NotifyOrderVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
        q.add(NotifyOrderVO_.productUuid, SimpleQuery.Op.EQ, productUuid);
        q.add(NotifyOrderVO_.status, SimpleQuery.Op.EQ, NotifyOrderStatus.FAILURE);
        List<NotifyOrderVO> notifyOrderVOS = q.list();
        if(notifyOrderVOS!=null && notifyOrderVOS.size()>0){
            return true;
        }
        return false;
    }


    @Transactional
    private void handle(APICreateBuyOrderMsg msg) {
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();

        BigDecimal discountPrice = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;

        List<ProductPriceUnit> units = msg.getUnits();
        List<String> productPriceUnitUuids = new ArrayList<String>();
        for (ProductPriceUnit unit : units) {
            SimpleQuery<ProductCategoryVO> query = dbf.createQuery(ProductCategoryVO.class);
            query.add(ProductCategoryVO_.code, SimpleQuery.Op.EQ, unit.getCategoryCode());
            query.add(ProductCategoryVO_.productTypeCode, SimpleQuery.Op.EQ, unit.getProductTypeCode());
            ProductCategoryVO productCategoryVO = query.find();
            if(productCategoryVO ==null){
                throw new IllegalArgumentException("can not find productType or category");
            }
            int times = 1;

            if(unit.getProductTypeCode().equals(ProductType.ECP) && unit.getCategoryCode().equals(Category.BANDWIDTH)){
                String configCode = unit.getConfigCode().replaceAll("\\D","");
                times = Integer.parseInt(configCode);
                unit.setConfigCode("1M");
            }
            SimpleQuery<ProductPriceUnitVO> q = dbf.createQuery(ProductPriceUnitVO.class);
            q.add(ProductPriceUnitVO_.productCategoryUuid, SimpleQuery.Op.EQ, productCategoryVO.getUuid());
            q.add(ProductPriceUnitVO_.areaCode, SimpleQuery.Op.EQ, unit.getAreaCode());
            q.add(ProductPriceUnitVO_.lineCode, SimpleQuery.Op.EQ, unit.getLineCode());
            q.add(ProductPriceUnitVO_.configCode, SimpleQuery.Op.EQ, unit.getConfigCode());
            ProductPriceUnitVO productPriceUnitVO = q.find();
            if (productPriceUnitVO == null) {
                throw new IllegalArgumentException("price uuid is not valid");
            }
            productPriceUnitUuids.add(productPriceUnitVO.getUuid());
            SimpleQuery<AccountDiscountVO> qDiscount = dbf.createQuery(AccountDiscountVO.class);
            qDiscount.add(AccountDiscountVO_.productCategoryUuid, SimpleQuery.Op.EQ, productPriceUnitVO.getProductCategoryUuid());
            qDiscount.add(AccountDiscountVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
            AccountDiscountVO accountDiscountVO = qDiscount.find();
            int productDiscount = 100;
            if (accountDiscountVO != null) {
                productDiscount = accountDiscountVO.getDiscount() <= 0 ? 100 : accountDiscountVO.getDiscount();
            }
            originalPrice = originalPrice.add(BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()*times));
            BigDecimal currentDiscount = BigDecimal.valueOf(productPriceUnitVO.getUnitPrice()).multiply(BigDecimal.valueOf(productDiscount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_EVEN);
            discountPrice = discountPrice.add(currentDiscount);

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
        orderVo.setDescriptionData(msg.getDescriptionData());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setDuration(msg.getDuration());
        orderVo.setCallBackData(msg.getCallBackData());

        originalPrice = originalPrice.multiply(duration);
        discountPrice = discountPrice.multiply(duration);

        if (discountPrice.compareTo(mayPayTotal) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE, String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
        }
        orderVo.setOriginalPrice(originalPrice);
        orderVo.setPrice(discountPrice);
        orderVo.setType(OrderType.BUY);
        if (msg.getProductType() == ProductType.TUNNEL) {
            orderVo.setProductStatus(0);
        }
        payMethod(msg, orderVo, abvo, discountPrice, currentTimestamp);
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
        renewVO.setDescriptionData(orderVo.getDescriptionData());
        renewVO.setRenewAuto(true);
        renewVO.setExpiredTime(orderVo.getProductEffectTimeEnd());
        renewVO.setPriceOneMonth(discountPrice);
        dbf.getEntityManager().persist(renewVO);

        for (String productPriceUnitUuid : productPriceUnitUuids) {
            PriceRefRenewVO priceRefRenewVO = new PriceRefRenewVO();
            priceRefRenewVO.setUuid(Platform.getUuid());
            priceRefRenewVO.setProductPriceUnitUuid(productPriceUnitUuid);
            priceRefRenewVO.setAccountUuid(msg.getAccountUuid());
            priceRefRenewVO.setRenewUuid(renewVO.getUuid());
            dbf.getEntityManager().persist(priceRefRenewVO);
        }

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
