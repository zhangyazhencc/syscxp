package org.zstack.billing.identity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.billing.header.identity.*;
import org.zstack.billing.header.identity.balance.DealDetailVO;
import org.zstack.billing.header.identity.order.APIUpdateOrderStateEvent;
import org.zstack.billing.header.identity.order.APIUpdateOrderStateMsg;
import org.zstack.billing.header.identity.receipt.*;
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
import org.zstack.header.identity.IdentityErrors;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.utils.Utils;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
        } else if (msg instanceof APIUpdateOrderStateMsg) {
            handle((APIUpdateOrderStateMsg) msg);
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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
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
            vo.setState(msg.getState());
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
            dbf.removeByPrimaryKey(uuid,ReceiptInfoVO.class);
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
        String currentAccountUuid = msg.getSession().getUuid();
        BigDecimal consumeCash = getConsumeCashByAccountUuid(currentAccountUuid);
        BigDecimal hadReceiptCash = getHadReceiptCashByAccountUuid(currentAccountUuid);
        AccountBalanceVO  vo =  dbf.findByUuid(currentAccountUuid, AccountBalanceVO.class);
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

    private void handle(APIUpdateOrderStateMsg msg) {
        String uuid = msg.getUuid();
        OrderVO vo = dbf.findByUuid(uuid,OrderVO.class);
        if(msg.getState()!=null){
            vo.setState(msg.getState());
        }
        dbf.updateAndRefresh(vo);
        OrderInventory oi = OrderInventory.valueOf(vo);
        APIUpdateOrderStateEvent evt = new APIUpdateOrderStateEvent(msg.getId());
        evt.setInventory(oi);
        bus.publish(evt);
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
        q.setParameter("accountUuid",msg.getSession().getUuid());
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
        AccountBalanceVO abvo = dbf.findByUuid(msg.getSession().getUuid(), AccountBalanceVO.class);
        BigDecimal total = msg.getPrice();
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);
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
        orderVo.setProductChargeModel(msg.getProductChargeModel());
        Timestamp currentTimeStamp = dbf.getCurrentSqlTime();
        orderVo.setPayTime(currentTimeStamp);
        orderVo.setProductDiscount(msg.getProductDiscount());
        orderVo.setProductDescription(msg.getProductDescription());
        orderVo.setOriginalPrice(msg.getOriginalPrice());
        orderVo.setProductUuid(msg.getProductUuid());
        orderVo.setPrice(msg.getPrice());
        orderVo.setDuration(msg.getDuration());

        switch (msg.getType()) {
            case BUY: case UPGRADE: case RENEW:
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
                break;
            case DOWNGRADE: case UN_SUBCRIBE:
                if(msg.getReChargePresentBalance().compareTo(BigDecimal.ZERO)<0){
                    BigDecimal remainPresent = abvo.getPresentBalance().subtract(msg.getReChargePresentBalance());
                    abvo.setPresentBalance(remainPresent);

                    DealDetailVO dVO = new DealDetailVO();
                    dVO.setUuid(Platform.getUuid());
                    dVO.setAccountUuid(msg.getSession().getUuid());
                    dVO.setDealWay(DealWay.BALANCE_BILL);
                    dVO.setIncome(msg.getReChargePresentBalance());
                    dVO.setExpend(BigDecimal.ZERO);
                    dVO.setFinishTime(currentTimeStamp);
                    dVO.setType(DealType.REFUND);
                    dVO.setState(DealState.SUCCESS);
                    dVO.setBalance(remainPresent);
                    dbf.persistAndRefresh(dVO);
                }
                if(msg.getReChargeCashBalance().compareTo(BigDecimal.ZERO)<0){
                    BigDecimal remainCash = abvo.getCashBalance().subtract(msg.getReChargeCashBalance());
                    abvo.setCashBalance(remainCash);
                    DealDetailVO dVO = new DealDetailVO();
                    dVO.setUuid(Platform.getUuid());
                    dVO.setAccountUuid(msg.getSession().getUuid());
                    dVO.setDealWay(DealWay.CASH_BILL);
                    dVO.setIncome(msg.getReChargeCashBalance());
                    dVO.setExpend(BigDecimal.ZERO);
                    dVO.setFinishTime(currentTimeStamp);
                    dVO.setType(DealType.REFUND);
                    dVO.setState(DealState.SUCCESS);
                    dVO.setBalance(remainCash);
                    dbf.persistAndRefresh(dVO);
                }
                break;
            case SLA_COMPENSATION:
                break;

        }

        dbf.updateAndRefresh(abvo);
        OrderVO orderNewVO = dbf.persistAndRefresh(orderVo);
        SimpleQuery<RenewVO> q = dbf.createQuery(RenewVO.class);
        q.add(RenewVO_.accountUuid, Op.EQ, orderNewVO.getAccountUuid());
        q.add(RenewVO_.productUuid, Op.EQ, orderNewVO.getProductUuid());
        RenewVO renewVO = q.find();
        if(renewVO != null){//if bought this product then update it
            renewVO.setDuration(orderNewVO.getDuration());
            renewVO.setProductChargeModel(orderNewVO.getProductChargeModel());
            renewVO.setExpiredDate(orderNewVO.getProductEffectTimeEnd());
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
            dbf.persistAndRefresh(renewVO);
        }

        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderEvent evt = new APICreateOrderEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);

    }


    private void handle(APIUpdateAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getUuid(), AccountBalanceVO.class);
        if (msg.getCashBalance() != null) {
            vo.setCashBalance(msg.getCashBalance());
        }
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
        AccountBalanceVO vo = dbf.findByUuid(msg.getSession().getUuid(), AccountBalanceVO.class);
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
        if (msg instanceof APIGetAccountBalanceMsg) {
            validate((APIGetAccountBalanceMsg) msg);
        } else if (msg instanceof APICreateOrderMsg) {
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

    private void validate(APIGetAccountBalanceMsg msg) {

    }

}
