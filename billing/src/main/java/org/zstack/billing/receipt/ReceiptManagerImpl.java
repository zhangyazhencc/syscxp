package org.zstack.billing.receipt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.billing.header.receipt.*;
import org.zstack.billing.BillingErrors;
import org.zstack.billing.BillingServiceException;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.billing.*;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class ReceiptManagerImpl  extends AbstractService implements  ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ReceiptManagerImpl.class);

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
        if (msg instanceof APIGetValuebleReceiptMsg) {
            handle((APIGetValuebleReceiptMsg) msg);
        } else if (msg instanceof APICreateReceiptPostAddressMsg) {
            handle((APICreateReceiptPostAddressMsg) msg);
        } else if (msg instanceof APIUpdateReceiptPostAddressMsg) {
            handle((APIUpdateReceiptPostAddressMsg) msg);
        } else if (msg instanceof APIDeleteReceiptPostAddressMsg) {
            handle((APIDeleteReceiptPostAddressMsg) msg);
        } else if (msg instanceof APICreateReceiptInfoMsg) {
            handle((APICreateReceiptInfoMsg) msg);
        } else if (msg instanceof APIUpdateReceiptInfoMsg) {
            handle((APIUpdateReceiptInfoMsg) msg);
        } else if (msg instanceof APIDeleteReceiptInfoMsg) {
            handle((APIDeleteReceiptInfoMsg) msg);
        } else if (msg instanceof APICreateReceiptMsg) {
            handle((APICreateReceiptMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }
    private void handle(APICreateReceiptMsg msg) {
        String accountUuid = msg.getSession().getAccountUuid();
        Timestamp currentTimestamp = dbf.getCurrentSqlTime();
        BigDecimal total = msg.getTotal();
        BigDecimal consumeCash = getConsumeCashByAccountUuid(accountUuid);
        BigDecimal hadReceiptCash = getHadReceiptCashByAccountUuid(accountUuid);
        AccountBalanceVO vo = dbf.findByUuid(accountUuid, AccountBalanceVO.class);
        BigDecimal valuebleReceipt = consumeCash.subtract(hadReceiptCash);
        BigDecimal hadConsumeCreditPoint = BigDecimal.ZERO;
        if (vo.getCashBalance().compareTo(BigDecimal.ZERO) < 0) {
            hadConsumeCreditPoint = vo.getCashBalance();
            valuebleReceipt = valuebleReceipt.add(hadConsumeCreditPoint);
        }
        if (total.compareTo(valuebleReceipt) > 0) {
            throw new BillingServiceException(errf.instantiateErrorCode(BillingErrors.NOT_VALID_VALUE, String.format("create receipt money must not great than %s", valuebleReceipt.toString())));
        }
        ReceiptVO receiptVO = new ReceiptVO();
        receiptVO.setUuid(Platform.getUuid());
        receiptVO.setAccountUuid(accountUuid);
        receiptVO.setApplyTime(currentTimestamp);
        receiptVO.setState(ReceiptState.UNDONE);
        receiptVO.setTotal(total);
        ReceiptPostAddressVO receiptPostAddressVO = dbf.findByUuid(msg.getReceiptAddressUuid(), ReceiptPostAddressVO.class);
        receiptVO.setReceiptPostAddressVO(receiptPostAddressVO);
        String receiptInfoUuid = msg.getReceiptInfoUuid();
        ReceiptInfoVO receiptInfoVO = dbf.findByUuid(receiptInfoUuid, ReceiptInfoVO.class);
        receiptVO.setReceiptInfoVO(receiptInfoVO);
        dbf.persistAndRefresh(receiptVO);
        ReceiptInventory inventory = ReceiptInventory.valueOf(receiptVO);
        APICreateReceiptEvent evt = new APICreateReceiptEvent(msg.getId());
        evt.setInventory(inventory);
        bus.publish(evt);
    }


    private void handle(APIDeleteReceiptInfoMsg msg) {
        String uuid = msg.getUuid();
        ReceiptInfoVO vo = dbf.findByUuid(msg.getUuid(), ReceiptInfoVO.class);
        if (vo != null) {
            dbf.remove(vo);
        }
        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APIDeleteReceiptInfoEvent evt = new APIDeleteReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIUpdateReceiptInfoMsg msg) {
        ReceiptInfoVO vo = dbf.findByUuid(msg.getUuid(), ReceiptInfoVO.class);
        if (msg.getAddress() != null) {
            vo.setAddress(msg.getAddress());
        }
        if (msg.getBankAccountNumber() != null) {
            vo.setBankAccountNumber(msg.getBankAccountNumber());
        }
        if (msg.getBankName() != null) {
            vo.setBankName(msg.getBankName());
        }
        if (msg.getIdentifyNumber() != null) {
            vo.setIdentifyNumber(msg.getIdentifyNumber());
        }
        if (msg.getTelephone() != null) {
            vo.setTelephone(msg.getTelephone());
        }
        if (msg.getTitle() != null) {
            vo.setTitle(msg.getTitle());
        }
        if (msg.getType() != null) {
            vo.setType(msg.getType());
        }
        if (vo.isDefault() != msg.isDefault()) {
            vo.setDefault(msg.isDefault());
            SimpleQuery<ReceiptInfoVO> q = dbf.createQuery(ReceiptInfoVO.class);
            q.add(ReceiptInfoVO_.accountUuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
            List<ReceiptInfoVO> ids = q.list();
            for (ReceiptInfoVO riVO : ids) {
                if (riVO.getUuid().equals(msg.getUuid())) {
                    continue;
                }
                if (riVO.isDefault()) {
                    riVO.setDefault(false);
                    dbf.updateAndRefresh(riVO);
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
        vo.setTitle(msg.getTitle());
        vo.setType(msg.getType());
        vo.setAccountUuid(msg.getSession().getAccountUuid());
        if (msg.getType().equals(ReceiptType.VAT_SPECIAL_RECEIPT)) {
            vo.setAddress(msg.getAddress());
            vo.setBankAccountNumber(msg.getBankAccountNumber());
            vo.setBankName(msg.getBankName());
            vo.setIdentifyNumber(msg.getIdentifyNumber());
            vo.setComment(msg.getComment());
            vo.setTelephone(msg.getTelephone());
        }

        dbf.persistAndRefresh(vo);

        ReceiptInfoInventory ri = ReceiptInfoInventory.valueOf(vo);
        APICreateReceiptInfoEvent evt = new APICreateReceiptInfoEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIDeleteReceiptPostAddressMsg msg) {
        String uuid = msg.getUuid();
        ReceiptPostAddressVO vo = dbf.findByUuid(msg.getUuid(), ReceiptPostAddressVO.class);
        if (vo != null) {
            dbf.removeByPrimaryKey(uuid, ReceiptPostAddressVO.class);
        }
        ReceiptPostAddressInventory ri = ReceiptPostAddressInventory.valueOf(vo);
        APIDeleteReceiptPostAddressEvent evt = new APIDeleteReceiptPostAddressEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void handle(APIUpdateReceiptPostAddressMsg msg) {
        ReceiptPostAddressVO vo = dbf.findByUuid(msg.getUuid(), ReceiptPostAddressVO.class);
        if (msg.getName() != null) {
            vo.setName(msg.getName());
        }
        if (msg.getTelephone() != null) {
            vo.setTelephone(msg.getTelephone());
        }
        if (msg.getAddress() != null) {
            vo.setAddress(msg.getAddress());
        }
        if (vo.isDefault() != msg.isDefault()) {
            vo.setDefault(msg.isDefault());
            SimpleQuery<ReceiptPostAddressVO> q = dbf.createQuery(ReceiptPostAddressVO.class);
            q.add(ReceiptPostAddressVO_.accountUuid, SimpleQuery.Op.EQ, vo.getAccountUuid());
            List<ReceiptPostAddressVO> all = q.list();
            for (ReceiptPostAddressVO receiptPostAddressVO : all) {
                if (receiptPostAddressVO.getUuid().equals(msg.getUuid())) {
                    continue;
                }
                ReceiptPostAddressVO v = dbf.findByUuid(receiptPostAddressVO.getUuid(), ReceiptPostAddressVO.class);
                if (v.isDefault()) {
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
        vo.setAccountUuid(msg.getSession().getAccountUuid());
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
        AccountBalanceVO vo = dbf.findByUuid(currentAccountUuid, AccountBalanceVO.class);
        BigDecimal valuebleReceipt = consumeCash.subtract(hadReceiptCash);
        BigDecimal hadConsumeCreditPoint = BigDecimal.ZERO;
        if (vo.getCashBalance().compareTo(BigDecimal.ZERO) < 0) {
            hadConsumeCreditPoint = vo.getCashBalance();
            valuebleReceipt = valuebleReceipt.add(hadConsumeCreditPoint);
        }
        APIGetValuebleReceiptReply reply = new APIGetValuebleReceiptReply();
        reply.setValuebleReceipt(valuebleReceipt);
        reply.setConsumeCash(consumeCash);
        reply.setHadConsumeCreditPoint(hadConsumeCreditPoint);
        reply.setHadReceiptCash(hadReceiptCash);
        bus.reply(msg, reply);

    }

    @Transactional(readOnly = true)
    BigDecimal getConsumeCashByAccountUuid(String accountUuid) {
        String sql = "select sum(vo.payCash)" + " from OrderVO vo " + " where vo.accountUuid = :accountUuid";
        TypedQuery<BigDecimal> vq = dbf.getEntityManager().createQuery(sql, BigDecimal.class);
        vq.setParameter("accountUuid", accountUuid);
        BigDecimal hadReceiptCash = vq.getSingleResult();
        hadReceiptCash = hadReceiptCash == null ? BigDecimal.ZERO : hadReceiptCash;
        return hadReceiptCash;
    }

    @Transactional(readOnly = true)
    BigDecimal getHadReceiptCashByAccountUuid(String accountUuid) {
        String sql = "select sum(vo.total)" + " from ReceiptVO vo " + " where vo.accountUuid = :accountUuid ";
        TypedQuery<BigDecimal> vq = dbf.getEntityManager().createQuery(sql, BigDecimal.class);
        vq.setParameter("accountUuid", accountUuid);
        BigDecimal consumeCash = vq.getSingleResult();
        consumeCash = consumeCash == null ? BigDecimal.ZERO : consumeCash;
        return consumeCash;
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

    }

}
