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
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetExpenseGrossMonthListMsg msg) {
        String sql = "select DATE_FORMAT(payTime,'%Y-%m') mon,sum(orderPayPresent)+sum(orderPayCash) as payTotal from OrderVO where accountUuid = :accountUuid and orderState = 'PAID' and payTime between :dateStart and :dateEnd group by mon order by mon asc";
        Query q =  dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid",msg.getAccountUuid());
        q.setParameter("dateStart", msg.getDateStart());
        q.setParameter("dateEnd", msg.getDateEnd());
        List<Object[]> objs  = q.getResultList();
        List<ExpenseGross> vos = objs.stream().map(ExpenseGross::new).collect(Collectors.toList());
        APIGetExpenseGrossMonthListReply reply = new APIGetExpenseGrossMonthListReply();
        reply.setInventories(vos);
        bus.reply(msg,reply);
    }

    private void handle(APICreateOrderMsg msg) {
        switch (msg.getOrderState()) {
            case PAID:
                createOrder(msg);
                break;
            case NOTPAID:
                break;
            case CANCELED:
                break;
        }
    }

    @Transactional
    public void createOrder(APICreateOrderMsg msg) {
        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
        BigDecimal total = msg.getTotal().multiply(msg.getProductDiscount()).divide(new BigDecimal(100));
        BigDecimal cashBalance = abvo.getCashBalance();
        BigDecimal presentBalance = abvo.getPresentBalance();
        BigDecimal creditPoint = abvo.getCreditPoint();
        BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);
        if (total.compareTo(mayPayTotal) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.INSUFFICIENT_BALANCE,String.format("you have no enough balance to pay this product. your pay money can not greater than %s. please go to recharge", mayPayTotal.toString())));
        }

        OrderVO orderVo = new OrderVO();
        String orderUuid = Platform.getUuid();
        orderVo.setUuid(orderUuid);
        orderVo.setAccountUuid(msg.getAccountUuid());
        orderVo.setProductName(msg.getProductName());
        orderVo.setOrderState(msg.getOrderState());
        orderVo.setProductType(msg.getProductType());
        orderVo.setOrderType(msg.getOrderType());
        orderVo.setProductEffectTimeEnd(msg.getProductEffectTimeEnd());
        orderVo.setProductEffectTimeStart(msg.getProductEffectTimeStart());
        orderVo.setProductChargeModel(msg.getProductChargeModel());
        Timestamp currentTimeStamp = dbf.getCurrentSqlTime();
        orderVo.setPayTime(currentTimeStamp);
        orderVo.setProductDiscount(msg.getProductDiscount());
        orderVo.setProductDescription(msg.getProductDescription());

        if (abvo.getPresentBalance().compareTo(BigDecimal.ZERO) > 0) {
            if (abvo.getPresentBalance().compareTo(total) > 0) {
                abvo.setPresentBalance(abvo.getPresentBalance().subtract(total));
                dbf.updateAndRefresh(abvo);
                orderVo.setOrderPayPresent(total);
                orderVo.setOrderPayCash(BigDecimal.ZERO);
            } else {
                BigDecimal payPresent = abvo.getPresentBalance();
                BigDecimal payCash = total.subtract(payPresent);
                abvo.setCashBalance(abvo.getCashBalance().subtract(payCash));
                abvo.setPresentBalance(BigDecimal.ZERO);
                dbf.updateAndRefresh(abvo);
                orderVo.setOrderPayPresent(payPresent);
                orderVo.setOrderPayCash(payCash);
            }
        } else {
            abvo.setCashBalance(abvo.getCashBalance().subtract(total));
            dbf.updateAndRefresh(abvo);
            orderVo.setOrderPayPresent(BigDecimal.ZERO);
            orderVo.setOrderPayCash(total);
        }

        dbf.persistAndRefresh(orderVo);
        OrderInventory inventory = OrderInventory.valueOf(orderVo);
        APICreateOrderEvent evt = new APICreateOrderEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);

    }


    private void handle(APIUpdateAccountBalanceMsg msg) {
        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
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
        AccountBalanceVO vo = dbf.findByUuid(msg.getAccountUuid(), AccountBalanceVO.class);
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
