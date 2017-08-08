package org.zstack.billing.identity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Map;

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
        } else if (msg instanceof APICreateOrderMsg){
            handle((APICreateOrderMsg) msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }

    }

    private void handle(APICreateOrderMsg msg) {
        switch (msg.getOrderState()){
            case PAID:
                transactionalHandle(msg);
                break;
            case NOTPAID:
                break;
            case CANCELED:
                break;

        }


    }

    @Transactional
    void transactionalHandle(APICreateOrderMsg msg) {
        AccountBalanceVO abvo = dbf.findByUuid(msg.getAccountUuid(),AccountBalanceVO.class);
        if(abvo == null){
            AccountBalanceVO vo = new AccountBalanceVO();
            vo.setUuid(msg.getAccountUuid());
            vo.setCashBalance(new BigDecimal("0"));
            vo.setPresentBalance(new BigDecimal("0"));
            vo.setCreditPoint(new BigDecimal("0"));
            dbf.persistAndRefresh(vo);
        }else{
            BigDecimal total = msg.getTotal().multiply(msg.getProductDiscount()).divide(new BigDecimal(100));
            BigDecimal cashBalance = abvo.getCashBalance();
            BigDecimal presentBalance = abvo.getPresentBalance();
            BigDecimal creditPoint = abvo.getCreditPoint();
            BigDecimal mayPayTotal = cashBalance.add(presentBalance).add(creditPoint);
            if(total.compareTo(mayPayTotal)>0){
                throw new RuntimeException(String.format("you have no enough balance to pay this product. your pay money can not greater than %d.please go to recharge",mayPayTotal.doubleValue()));
            }


            OrderVO orderVo = new OrderVO();
            String orderUuid = Platform.getUuid();
            orderVo.setUuid(orderUuid);
            orderVo.setAccountUuid(msg.getAccountUuid());
            orderVo.setProductName(msg.getProductName());
            orderVo.setOrderType(msg.getOrderType());
            orderVo.setProductEffectTimeEnd(msg.getProductEffectTimeEnd());
            orderVo.setProductEffectTimeStart(msg.getProductEffectTimeStart());
            orderVo.setProductChargeModel(msg.getProductChargeModel());
            Timestamp currentTimeStamp = dbf.getCurrentSqlTime();
            orderVo.setPayTime(currentTimeStamp);
            orderVo.setProductDiscount(msg.getProductDiscount());
            orderVo.setProductDescription(msg.getProductDescription());

            if(abvo.getPresentBalance()!=null && abvo.getPresentBalance().doubleValue()>0){
                if(abvo.getPresentBalance().compareTo(total)>0){
                    AccountBalanceVO vo = new AccountBalanceVO();
                    vo.setUuid(msg.getAccountUuid());
                    vo.setPresentBalance(abvo.getPresentBalance().subtract(total));
                    dbf.updateAndRefresh(vo);
                    orderVo.setOrderPayPresent(total);
                    orderVo.setOrderPayCash(new BigDecimal("0"));
                }else{
                    AccountBalanceVO vo = new AccountBalanceVO();
                    vo.setUuid(msg.getAccountUuid());
                    vo.setCashBalance(abvo.getCashBalance().subtract(total.subtract(abvo.getPresentBalance())));
                    vo.setPresentBalance(new BigDecimal("0"));
                    dbf.updateAndRefresh(vo);
                    orderVo.setOrderPayPresent(abvo.getPresentBalance());
                    orderVo.setOrderPayCash(total.subtract(abvo.getPresentBalance()));
                }
            }else {
                AccountBalanceVO vo = new AccountBalanceVO();
                vo.setUuid(msg.getAccountUuid());
                vo.setCashBalance(abvo.getCashBalance().subtract(total));
                dbf.updateAndRefresh(vo);
                orderVo.setOrderPayPresent(new BigDecimal("0"));
                orderVo.setOrderPayCash(total);
            }

            dbf.persistAndRefresh(orderVo);
            OrderInventory inventory = OrderInventory.valueOf(orderVo);
            APICreateOrderEvent evt = new APICreateOrderEvent(msg.getId());
            evt.setInventory(inventory);
            bus.publish(evt);

        }
    }

    @Transactional
    void handle(APIUpdateAccountBalanceMsg msg) {
        String accountUuid = msg.getAccountUuid();
        AccountBalanceVO vo = new AccountBalanceVO();
        vo.setUuid(accountUuid);

        if (!dbf.isExist(accountUuid, AccountBalanceVO.class)) {
            if (msg.getCashBalance() != null) {
                vo.setCashBalance(msg.getCashBalance());
            } else {
                vo.setCashBalance(new BigDecimal("0"));
            }
            if (msg.getPresentBalance() != null) {
                vo.setPresentBalance(msg.getPresentBalance());
            } else {
                vo.setPresentBalance(new BigDecimal("0"));
            }
            if (msg.getCreditPoint() != null) {
                vo.setCreditPoint(msg.getCreditPoint());
            } else {
                vo.setCreditPoint(new BigDecimal("0"));
            }

            dbf.persistAndRefresh(vo);
        } else {
            boolean isCashBalanceNeedUpdate = false;
            boolean isPresentBalanceNeedUpdate = false;
            boolean isCreditPointNeedUpdate = false;
            StringBuilder sql = new StringBuilder();
            sql.append(" update AccountBalanceVO set ");
            if (msg.getCashBalance() != null) {
                isCashBalanceNeedUpdate = true;
                vo.setCashBalance(msg.getCashBalance());
                sql.append(" cashBalance = :cashBalance,");
            }
            if (msg.getPresentBalance() != null) {
                vo.setPresentBalance(msg.getPresentBalance());
                isPresentBalanceNeedUpdate = true;
                sql.append(" presentBalance = :presentBalance,");
            }
            if (msg.getCreditPoint() != null) {
                vo.setCreditPoint(msg.getCreditPoint());
                isCreditPointNeedUpdate = true;
                sql.append(" creditPoint = :creditPoint,");
            }

            if(isCashBalanceNeedUpdate || isPresentBalanceNeedUpdate || isCreditPointNeedUpdate){
                sql.deleteCharAt(sql.length()-1);
                sql.append(" where uuid = :accountUuid ");
                Query q =  dbf.getEntityManager().createQuery(sql.toString());
                if(isCashBalanceNeedUpdate){
                    q.setParameter("cashBalance", msg.getCashBalance());
                }
                if(isPresentBalanceNeedUpdate){
                    q.setParameter("presentBalance", msg.getPresentBalance());
                }
                if(isCreditPointNeedUpdate){
                    q.setParameter("creditPoint", msg.getCreditPoint());
                }
                q.setParameter("accountUuid", msg.getAccountUuid());
                q.executeUpdate();
                dbf.getEntityManager().flush();
            }
        }
        AccountBalanceInventory abi = AccountBalanceInventory.valueOf(vo);
        APIUpdateAccountBalanceEvent evt = new APIUpdateAccountBalanceEvent(msg.getId());
        evt.setInventory(abi);
        bus.publish(evt);

    }


    private void handle(APIGetAccountBalanceMsg msg) {
        SimpleQuery<AccountBalanceVO> q = dbf.createQuery(AccountBalanceVO.class);
        q.add(AccountBalanceVO_.uuid, Op.EQ, msg.getAccountUuid());
        AccountBalanceVO a = q.find();
        AccountBalanceInventory inventory = new AccountBalanceInventory();
        if (a != null) {
            inventory.setUuid(a.getUuid());
            inventory.setCashBalance(a.getCashBalance());
            inventory.setPresentBalance(a.getPresentBalance());
            inventory.setCreditPoint(a.getCreditPoint());
        }

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
        }else if(msg instanceof APICreateOrderMsg){
            validate((APICreateOrderMsg)msg);
        }
        return msg;
    }

    private void validate(APICreateOrderMsg msg) {
        try{
            JSONObjectUtil.toObject(msg.getProductDescription(),Map.class);
        }catch (Exception e){
            throw new ApiMessageInterceptionException(Platform.argerr("product description must be a json syntactic"));
        }

    }

    private void validate(APIGetAccountBalanceMsg msg) {
        if (StringUtils.isEmpty(msg.getAccountUuid())) {
            throw new ApiMessageInterceptionException(Platform.argerr("%s must be not null", "uuid"));
        }
    }

}
