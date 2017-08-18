package org.zstack.billing.manage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.billing.header.identity.balance.*;
import org.zstack.billing.header.identity.order.*;
import org.zstack.billing.header.identity.receipt.*;
import org.zstack.billing.header.identity.renew.*;
import org.zstack.billing.header.identity.sla.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.*;
import org.zstack.core.db.SimpleQuery.Op;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Order;

public class BillingManagerImpl extends AbstractService implements BillingManager, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(BillingManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private EventFacade evtf;
    @Autowired
    private GlobalConfigFacade gcf;

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
        if (msg instanceof APIGetAccountBalanceMsg) {
            handle((APIGetAccountBalanceMsg) msg);
        } else if (msg instanceof APIUpdateAccountBalanceMsg) {
            handle((APIUpdateAccountBalanceMsg) msg);
        } else if (msg instanceof APICreateOrderMsg) {
            handle((APICreateOrderMsg) msg);
        } else if (msg instanceof APIGetExpenseGrossMonthListMsg) {
            handle((APIGetExpenseGrossMonthListMsg) msg);
        }else if (msg instanceof APIUpdateRenewMsg) {
            handle((APIUpdateRenewMsg) msg);
        } else if (msg instanceof APIPayRenewOrderMsg) {
            handle((APIPayRenewOrderMsg) msg);
        } else if (msg instanceof APIGetValuebleReceiptMsg) {
            handle((APIGetValuebleReceiptMsg) msg);
        } else if (msg instanceof APICreateReceiptPostAddressMsg) {
            handle((APICreateReceiptPostAddressMsg) msg);
        }else if (msg instanceof APIUpdateReceiptPostAddressMsg) {
            handle((APIUpdateReceiptPostAddressMsg) msg);
        } else if (msg instanceof APIDeleteReceiptPostAddressMsg) {
            handle((APIDeleteReceiptPostAddressMsg) msg);
        } else if (msg instanceof APICreateReceiptInfoMsg) {
            handle((APICreateReceiptInfoMsg) msg);
        } else if (msg instanceof APIUpdateReceiptInfoMsg) {
            handle((APIUpdateReceiptInfoMsg) msg);
        }  else if (msg instanceof APIDeleteReceiptInfoMsg) {
            handle((APIDeleteReceiptInfoMsg) msg);
        } else if (msg instanceof APICreateSLACompensateMsg) {
            handle((APICreateSLACompensateMsg) msg);
        }else if (msg instanceof APIUpdateSLACompensateMsg) {
            handle((APIUpdateSLACompensateMsg) msg);
        } else if (msg instanceof APIDeleteCanceledOrderMsg) {
            handle((APIDeleteCanceledOrderMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIDeleteCanceledOrderMsg msg) {
        String orderUuid = msg.getUuid();
        OrderVO vo = dbf.findByUuid(orderUuid,OrderVO.class);
        if(vo==null || !vo.getState().equals(OrderState.CANCELED)){
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.NOT_PERMIT_UPDATE,String.format("if order not this state, can not be deleted")));
        }
        dbf.remove(vo);
        OrderInventory ri = OrderInventory.valueOf(vo);
        APIDeleteCanceledOrderEvent evt = new APIDeleteCanceledOrderEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APIUpdateSLACompensateMsg msg) {
        SLACompensateVO vo = dbf.findByUuid(msg.getUuid(), SLACompensateVO.class);
        if(msg.getAccountUuid()!=null){
            vo.setAccountUuid(msg.getAccountUuid());
        }
        if(msg.getDescription()!=null){
            vo.setDescription(msg.getDescription());
        }
        if(msg.getDuration()!=null){
            vo.setDuration(msg.getDuration());
        }
        if(msg.getProductName()!=null){
            vo.setProductName(msg.getProductName());
        }
        if(msg.getProductType()!=null){
            vo.setProductType(msg.getProductType());
        }
        if(msg.getReason()!=null){
            vo.setReason(msg.getReason());
        }
        if(msg.getTimeStart()!=null){
            vo.setTimeStart(msg.getTimeStart());
        }
        if(msg.getTimeEnd()!=null){
            vo.setTimeEnd(msg.getTimeEnd());
        }
        if(msg.getProductUuid()!=null){
            vo.setProductUuid(msg.getProductUuid());
        }
        if(msg.getState()!=null){
            vo.setState(msg.getState());//todo this would handle product interface
        }

        dbf.updateAndRefresh(vo);
        SLACompensateInventory ri = SLACompensateInventory.valueOf(vo);
        APIUpdateSLACompensateEvent evt = new APIUpdateSLACompensateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APICreateSLACompensateMsg msg) {
        SLACompensateVO vo = new SLACompensateVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setDescription(msg.getDescription());
        vo.setDuration(msg.getDuration());
        vo.setProductUuid(msg.getProductUuid());
        vo.setProductName(msg.getProductName());
        vo.setProductType(msg.getProductType());
        vo.setReason(msg.getReason());
        vo.setState(SLAState.NOT_APPLY);

        dbf.persistAndRefresh(vo);
        SLACompensateInventory ri = SLACompensateInventory.valueOf(vo);
        APICreateSLACompensateEvent evt = new APICreateSLACompensateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIDeleteReceiptInfoMsg msg) {
        String uuid = msg.getUuid();
        ReceiptInfoVO vo = dbf.findByUuid(msg.getUuid(),ReceiptInfoVO.class);
        if(vo != null){
            dbf.remove(vo);
        }
        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APIDeleteReceiptInfoEvent evt = new APIDeleteReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIUpdateReceiptInfoMsg msg) {
        ReceiptInfoVO vo = dbf.findByUuid(msg.getUuid(),ReceiptInfoVO.class);
        if(msg.getAddress()!=null){
            vo.setAddress(msg.getAddress());
        }
        if(msg.getBankAccountNumber()!=null){
            vo.setBankAccountNumber(msg.getBankAccountNumber());
        }
        if(msg.getBankName()!=null){
            vo.setBankName(msg.getBankName());
        }
        if(msg.getIdentifyNumber()!=null){
            vo.setIdentifyNumber(msg.getIdentifyNumber());
        }
        if(msg.getTelephone()!=null){
            vo.setTelephone(msg.getTelephone());
        }
        if(msg.getTitle()!=null){
            vo.setTitle(msg.getTitle());
        }
        if(msg.getType()!=null){
            vo.setType(msg.getType());
        }
        if(vo.isDefault()!= msg.isDefault()){
            vo.setDefault(msg.isDefault());
            SimpleQuery<ReceiptInfoVO> q = dbf.createQuery(ReceiptInfoVO.class);
            q.add(ReceiptInfoVO_.accountUuid, Op.EQ, vo.getAccountUuid());
            List<String> ids = q.list();
            for(String id : ids){
                if(id.equals(msg.getUuid())){
                    continue;
                }
                ReceiptInfoVO v = dbf.findByUuid(id,ReceiptInfoVO.class);
                if(v.isDefault()){
                    v.setDefault(false);
                    dbf.updateAndRefresh(v);
                }
            }

        }
        dbf.updateAndRefresh(vo);
        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APIUpdateReceiptInfoEvent evt = new APIUpdateReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APICreateReceiptInfoMsg msg) {
        ReceiptInfoVO vo = new ReceiptInfoVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getUuid());
        vo.setAddress(msg.getAddress());
        vo.setBankAccountNumber(msg.getBankAccountNumber());
        vo.setBankName(msg.getBankName());
        vo.setIdentifyNumber(msg.getIdentifyNumber());
        vo.setTitle(msg.getTitle());
        vo.setType(msg.getType());
        vo.setTelephone(msg.getTelephone());
        dbf.persistAndRefresh(vo);

        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APICreateReceiptInfoEvent evt = new APICreateReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIDeleteReceiptPostAddressMsg msg) {
        String uuid = msg.getUuid();
        ReceiptPostAddressVO vo = dbf.findByUuid(msg.getUuid(),ReceiptPostAddressVO.class);
        if(vo != null){
            dbf.removeByPrimaryKey(uuid,ReceiptPostAddressVO.class);
        }
        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APIDeleteReceiptPostAddressEvent evt = new APIDeleteReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIUpdateReceiptPostAddressMsg msg) {
        ReceiptPostAddressVO vo = dbf.findByUuid(msg.getUuid(),ReceiptPostAddressVO.class);
        if(msg.getName()!= null){
            vo.setName(msg.getName());
        }
        if(msg.getTelephone()!=null){
            vo.setTelephone(msg.getTelephone());
        }
        if(msg.getAddress()!=null){
            vo.setAddress(msg.getAddress());
        }
        if(vo.isDefault()!= msg.isDefault()){
            vo.setDefault(msg.isDefault());
            SimpleQuery<ReceiptPostAddressVO> q = dbf.createQuery(ReceiptPostAddressVO.class);
            q.add(ReceiptPostAddressVO_.accountUuid, Op.EQ, vo.getAccountUuid());
            List<String> ids = q.list();
            for(String id : ids){
                if(id.equals(msg.getUuid())){
                    continue;
                }
                ReceiptPostAddressVO v = dbf.findByUuid(id,ReceiptPostAddressVO.class);
                if(v.isDefault()){
                    v.setDefault(false);
                    dbf.updateAndRefresh(v);
                }
            }

        }
        dbf.updateAndRefresh(vo);
        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APIUpdateReceiptPostAddressEvent evt = new APIUpdateReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APICreateReceiptPostAddressMsg msg) {
        ReceiptPostAddressVO vo = new ReceiptPostAddressVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getSession().getUuid());
        vo.setAddress(msg.getAddress());
        vo.setName(msg.getName());
        vo.setTelephone(msg.getTelephone());
        dbf.persistAndRefresh(vo);

        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APICreateReceiptPostAddressEvent evt = new APICreateReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APIGetValuebleReceiptMsg msg) {
        String currentAccountUuid = msg.getSession().getAccountUuid();
        BigDecimal consumeCash = getConsumeCashByAccountUuid(currentAccountUuid);
        BigDecimal hadReceiptCash = getHadReceiptCashByAccountUuid(currentAccountUuid);
        AccountBalanceVO vo =  dbf.findByUuid(currentAccountUuid, AccountBalanceVO.class);
        BigDecimal valuebleReceipt = consumeCash.subtract(hadReceiptCash);
        BigDecimal hadConsumeCreditPoint = BigDecimal.ZERO;
        if(vo.getCashBalance().compareTo(BigDecimal.ZERO)<0){
            hadConsumeCreditPoint = vo.getCashBalance();
            valuebleReceipt = valuebleReceipt.add(hadConsumeCreditPoint);
        }
        APIGetValuebleReceiptReply reply = new APIGetValuebleReceiptReply();
        reply.setValuebleReceipt(valuebleReceipt);
        reply.setConsumeCash(consumeCash);
        reply.setHadConsumeCreditPoint(hadConsumeCreditPoint);
        reply.setHadReceiptCash(hadReceiptCash);
        bus.reply(msg,reply);

    }

    @Transactional(readOnly = true)
    BigDecimal getConsumeCashByAccountUuid(String accountUuid){
        String sql = "select sum(vo.payCash)" +
                " from OrderVO vo " +
                " where vo.accountUuid = :accountUuid" ;
        TypedQuery<BigDecimal> vq = dbf.getEntityManager().createQuery(sql, BigDecimal.class);
        vq.setParameter("accountUuid", accountUuid);
        BigDecimal hadReceiptCash = vq.getSingleResult();
        hadReceiptCash = hadReceiptCash == null ? BigDecimal.ZERO : hadReceiptCash;
        return hadReceiptCash;
    }

    @Transactional(readOnly = true)
    BigDecimal getHadReceiptCashByAccountUuid(String accountUuid){
        String sql = "select sum(vo.total)" +
                " from ReceiptVO vo " +
                " where vo.accountUuid = :accountUuid" ;
        TypedQuery<BigDecimal> vq = dbf.getEntityManager().createQuery(sql, BigDecimal.class);
        vq.setParameter("accountUuid", accountUuid);
        BigDecimal consumeCash = vq.getSingleResult();
        consumeCash = consumeCash == null ? BigDecimal.ZERO : consumeCash;
        return consumeCash;
    }

     @Transactional
     void handle(APIPayRenewOrderMsg msg) {
        String orderUuid = msg.getOrderUuid();
        OrderVO orderVo = dbf.findByUuid(orderUuid,OrderVO.class);
        AccountBalanceVO abvo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        if(!orderVo.getState().equals(OrderState.NOTPAID)){
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.NOT_PERMIT_UPDATE,String.format("if order not this state, can not be updated")));
        }
        BigDecimal total = orderVo.getPayCash();
        Timestamp currentTimeStamp = dbf.getCurrentSqlTime();

        if(msg.getConfirm().equals(Confirm.OK)){
            payMethod(msg, orderVo, abvo, total, currentTimeStamp);
            orderVo.setState(OrderState.PAID);
        } else if(msg.getConfirm().equals(Confirm.CANCEL)){
            orderVo.setState(OrderState.CANCELED);
        }

        dbf.updateAndRefresh(orderVo);
        OrderInventory oi = OrderInventory.valueOf(orderVo);
        APIPayRenewOrderEvent evt = new APIPayRenewOrderEvent(msg.getId());
        evt.setInventory(oi);
        bus.publish(evt);
    }

    @Transactional(propagation= Propagation.REQUIRED)
     void payMethod(APIMessage msg, OrderVO orderVo, AccountBalanceVO abvo, BigDecimal total, Timestamp currentTimeStamp) {
        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) > 0) {
                BigDecimal presentNow = abvo.getPresentBalance().subtract(total);
                abvo.setPresentBalance(presentNow);
                orderVo.setPayPresent(total);
                orderVo.setPayCash(BigDecimal.ZERO);
                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(msg.getSession().getUuid());
                dealDetailVO.setDealWay(DealWay.BALANCE_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(total.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(presentNow);
                dealDetailVO.setOrderUuid(orderVo.getUuid());
                dbf.persistAndRefresh(dealDetailVO);

            } else {
                BigDecimal payPresent = abvo.getPresentBalance();
                BigDecimal payCash = total.subtract(payPresent);
                BigDecimal remainCash = abvo.getCashBalance().subtract(payCash);
                abvo.setCashBalance(remainCash);
                abvo.setPresentBalance(BigDecimal.ZERO);
                orderVo.setPayPresent(payPresent);

                DealDetailVO dealDetailVO = new DealDetailVO();
                dealDetailVO.setUuid(Platform.getUuid());
                dealDetailVO.setAccountUuid(msg.getSession().getUuid());
                dealDetailVO.setDealWay(DealWay.BALANCE_BILL);
                dealDetailVO.setIncome(BigDecimal.ZERO);
                dealDetailVO.setExpend(payPresent.negate());
                dealDetailVO.setFinishTime(currentTimeStamp);
                dealDetailVO.setType(DealType.DEDUCTION);
                dealDetailVO.setState(DealState.SUCCESS);
                dealDetailVO.setBalance(BigDecimal.ZERO);
                dealDetailVO.setOrderUuid(orderVo.getUuid());
                dbf.persistAndRefresh(dealDetailVO);

                orderVo.setPayCash(payCash);

                DealDetailVO dVO = new DealDetailVO();
                dVO.setUuid(Platform.getUuid());
                dVO.setAccountUuid(msg.getSession().getUuid());
                dVO.setDealWay(DealWay.CASH_BILL);
                dVO.setIncome(BigDecimal.ZERO);
                dVO.setExpend(payCash.negate());
                dVO.setFinishTime(currentTimeStamp);
                dVO.setType(DealType.DEDUCTION);
                dVO.setState(DealState.SUCCESS);
                dVO.setBalance(remainCash);
                dVO.setOrderUuid(orderVo.getUuid());
                dbf.persistAndRefresh(dVO);
            }
        } else {
            BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
            abvo.setCashBalance(remainCashBalance);
            orderVo.setPayPresent(BigDecimal.ZERO);
            orderVo.setPayCash(total);

            DealDetailVO dVO = new DealDetailVO();
            dVO.setUuid(Platform.getUuid());
            dVO.setAccountUuid(msg.getSession().getUuid());
            dVO.setDealWay(DealWay.CASH_BILL);
            dVO.setIncome(BigDecimal.ZERO);
            dVO.setExpend(total.negate());
            dVO.setFinishTime(currentTimeStamp);
            dVO.setType(DealType.DEDUCTION);
            dVO.setState(DealState.SUCCESS);
            dVO.setBalance(remainCashBalance);
            dbf.persistAndRefresh(dVO);
        }
    }

    private void handle(APIUpdateRenewMsg msg) {
        boolean isRenewAuto = msg.isRenewAuto();
        String uuid = msg.getUuid();
        RenewVO vo = dbf.findByUuid(uuid,RenewVO.class);
        if(vo.isRenewAuto()!=isRenewAuto){
            vo.setRenewAuto(msg.isRenewAuto());
        }
        dbf.updateAndRefresh(vo);
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewEvent evt = new APIUpdateRenewEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APIGetExpenseGrossMonthListMsg msg) {
        String sql = "select DATE_FORMAT(payTime,'%Y-%m') mon,sum(payPresent)+sum(payCash) as payTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime between :dateStart and :dateEnd group by mon order by mon asc";
        Query q =  dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid",msg.getSession().getAccountUuid());
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs  = q.getResultList();
        List<ExpenseGross> vos = objs.stream().map(ExpenseGross::new).collect(Collectors.toList());
        APIGetExpenseGrossMonthListReply reply = new APIGetExpenseGrossMonthListReply();
        reply.setInventories(vos);
        bus.reply(msg,reply);
    }

    private void handle(APICreateOrderMsg msg) {
        createOrder(msg);
    }

    @Transactional
    public void createOrder(APICreateOrderMsg msg) {
        String priceUnitUuid =  msg.getPriceUnitUuid();
        ProductPriceUnitVO productPriceUnitVO = dbf.findByUuid(priceUnitUuid,ProductPriceUnitVO.class);
        if(productPriceUnitVO == null){
            throw new IllegalArgumentException(String.format("priceUnitUuid is invalid"));
        }
        int productDisCharge = 100;//todo This value would get from account
        BigDecimal duration = BigDecimal.valueOf(msg.getDuration());
        if(msg.getProductChargeModel().equals(ProductChargeModel.BY_DAY)){
            duration = duration.divide(BigDecimal.valueOf(30),4, RoundingMode.HALF_DOWN);

        }
        if(msg.getProductChargeModel().equals(ProductChargeModel.BY_YEAR)){
            duration = duration.multiply(BigDecimal.valueOf(12));
        }

        AccountBalanceVO abvo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);
        BigDecimal total = BigDecimal.ZERO;
        BigDecimal originalPrice = BigDecimal.ZERO;
        BigDecimal reCash = BigDecimal.ZERO;//drop product
        BigDecimal downCash =  BigDecimal.ZERO;//downgrade
        switch (msg.getType()){//upgrade money need specially deal
            case UPGRADE:case DOWNGRADE: case UN_SUBCRIBE:
                OrderVO oldOrderVO = dbf.findByUuid(msg.getOldOrderUuid(),OrderVO.class);
                if(oldOrderVO == null){
                    throw new IllegalArgumentException("the order that wanna upgrade is not find,please check it out");
                }
                BigDecimal oldPayPreset = oldOrderVO.getPayPresent();
                BigDecimal oldPayCash = oldOrderVO.getPayCash();
                Timestamp startTime = oldOrderVO.getProductEffectTimeStart();
                Timestamp endTime = oldOrderVO.getProductEffectTimeEnd();
                Timestamp currentTime = dbf.getCurrentSqlTime();
                long useDays = (currentTime.getTime()-startTime.getTime())/1000*60*60*24;
                long needPayDays = (endTime.getTime()-currentTime.getTime())/1000*60*60*24+1;
                long days = (endTime.getTime()-startTime.getTime())/1000*60*60*24;
                BigDecimal avgOldPriceByDay = oldPayCash.add(oldPayPreset).divide(BigDecimal.valueOf(days),4,RoundingMode.HALF_DOWN);
                BigDecimal usedMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(useDays));
                BigDecimal remainMoney = avgOldPriceByDay.multiply(BigDecimal.valueOf(needPayDays));
                originalPrice = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()*needPayDays).subtract(remainMoney).divide(BigDecimal.valueOf(30),4,RoundingMode.HALF_DOWN);
                total = originalPrice.multiply(BigDecimal.valueOf(productDisCharge).divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_DOWN));
                if(usedMoney.compareTo(oldPayPreset)<=0){
                    reCash = oldPayCash;
                } else {
                    reCash = oldPayPreset.add(oldPayCash).subtract(usedMoney);
                }
                break;

            case BUY: case RENEW: case SLA_COMPENSATION:
                originalPrice = BigDecimal.valueOf(productPriceUnitVO.getPriceUnit()).multiply(duration);
                total =originalPrice.multiply(BigDecimal.valueOf(productDisCharge).divide(BigDecimal.valueOf(100),4,RoundingMode.HALF_DOWN));
                break;
        }
        if (total.compareTo(mayPayTotal) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE,String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
        }

        OrderVO orderVo = new OrderVO();
        orderVo.setUuid( Platform.getUuid());
        orderVo.setAccountUuid(msg.getSession().getUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setState(msg.getState());
        orderVo.setProductType(msg.getProductType());
        orderVo.setType(msg.getType());
        orderVo.setProductEffectTimeEnd(msg.getProductEffectTimeEnd());
        orderVo.setProductEffectTimeStart(msg.getProductEffectTimeStart());
        orderVo.setProductChargeModel(msg.getProductChargeModel());//todo this value would be got from account
        Timestamp currentTimeStamp = dbf.getCurrentSqlTime();
        orderVo.setPayTime(currentTimeStamp);
        orderVo.setProductDiscount(msg.getProductDiscount());
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setOriginalPrice(originalPrice);
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setPrice(total);
        orderVo.setDuration(msg.getDuration());

        switch (msg.getType()) {
            case BUY: case UPGRADE:
                payMethod(msg, orderVo, abvo, total, currentTimeStamp);
                break;

            case UN_SUBCRIBE: case DOWNGRADE:
                    BigDecimal remainCash = abvo.getCashBalance().subtract(reCash);
                    abvo.setCashBalance(remainCash);
                    DealDetailVO dVO = new DealDetailVO();
                    dVO.setUuid(Platform.getUuid());
                    dVO.setAccountUuid(msg.getSession().getUuid());
                    dVO.setDealWay(DealWay.CASH_BILL);
                    dVO.setIncome(reCash.negate());
                    dVO.setExpend(BigDecimal.ZERO);
                    dVO.setFinishTime(currentTimeStamp);
                    dVO.setType(DealType.REFUND);
                    dVO.setState(DealState.SUCCESS);
                    dVO.setBalance(remainCash);
                    dVO.setOrderUuid(orderVo.getUuid());
                    dbf.persistAndRefresh(dVO);
                break;

            case SLA_COMPENSATION:
                orderVo.setPayCash(BigDecimal.ZERO);
                orderVo.setPayPresent(BigDecimal.ZERO);
                break;

            case RENEW:
                if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
                    if (abvo.getPresentBalance().compareTo(total) > 0) {
                        orderVo.setPayPresent(total);
                        orderVo.setPayCash(BigDecimal.ZERO);

                    } else {
                        BigDecimal payPresent = abvo.getPresentBalance();
                        BigDecimal payCash = total.subtract(payPresent);
                        orderVo.setPayPresent(payPresent);
                        orderVo.setPayCash(payCash);
                    }

                } else {
                    BigDecimal remainCashBalance = abvo.getCashBalance().subtract(total);
                    orderVo.setPayPresent(BigDecimal.ZERO);
                    orderVo.setPayCash(total);

                }
                break;
        }

        dbf.updateAndRefresh(abvo);
        OrderVO orderNewVO = dbf.persistAndRefresh(orderVo);
        switch (orderNewVO.getType()){
            case BUY: case UPGRADE: case DOWNGRADE:
                SimpleQuery<RenewVO> q = dbf.createQuery(RenewVO.class);
                q.add(RenewVO_.accountUuid, Op.EQ, orderNewVO.getAccountUuid());
                q.add(RenewVO_.productUuid, Op.EQ, orderNewVO.getProductUuid());
                RenewVO renewVO = q.find();
                if(renewVO != null){//if bought this product then update it
                    renewVO.setDuration(orderNewVO.getDuration());
                    renewVO.setProductChargeModel(orderNewVO.getProductChargeModel());
                    renewVO.setExpiredDate(orderNewVO.getProductEffectTimeEnd());
                    renewVO.setProductUnitPriceUuid(orderNewVO.getProductUnitPriceUuid());
                    dbf.updateAndRefresh(renewVO);
                }else {
                    renewVO = new RenewVO();
                    renewVO.setUuid(Platform.getUuid());
                    renewVO.setProductChargeModel(orderNewVO.getProductChargeModel());
                    renewVO.setDuration(orderNewVO.getDuration());
                    renewVO.setProductUuid(orderNewVO.getProductUuid());
                    renewVO.setAccountUuid(orderNewVO.getAccountUuid());
                    renewVO.setProductName(orderNewVO.getProductName());
                    renewVO.setProductType(orderNewVO.getProductType());
                    renewVO.setExpiredDate(orderNewVO.getProductEffectTimeEnd());
                    renewVO.setProductUnitPriceUuid(orderNewVO.getProductUnitPriceUuid());
                    dbf.persistAndRefresh(renewVO);
                }
                break;

        }

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderEvent evt = new APICreateOrderEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);

    }


    private void handle(APIUpdateAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid()==null?msg.getSession().getUuid():msg.getAccountUuid(), AccountBalanceVO.class);

        if (msg.getPresentBalance() != null) {
            vo.setPresentBalance(msg.getPresentBalance());
        }
        if (msg.getCreditPoint() != null) {
            vo.setCreditPoint(msg.getCreditPoint());
        }
        vo = dbf.updateAndRefresh(vo);
        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        APIUpdateAccountBalanceEvent evt = new APIUpdateAccountBalanceEvent(msg.getId());
        evt.setInventory(abi);
        bus.publish(evt);

    }

    private void handle(APIGetAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getAccountUuid(), AccountBalanceVO.class);
        AccountBalanceInventory inventory = AccountBalanceInventory.valueOf(vo);
        APIGetAccountBalanceReply reply = new APIGetAccountBalanceReply();
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID);
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
        try {
            JSONObjectUtil.toObject(msg.getProductDescription(), Map.class);
        } catch (Exception e) {
            throw new ApiMessageInterceptionException(Platform.argerr("product description must be a json syntactic"));
        }

    }

}
