package com.syscxp.billing.bill;

import com.syscxp.billing.header.bill.*;
import com.syscxp.core.db.GLock;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.OrderType;
import org.springframework.beans.factory.annotation.Autowired;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

public class BillManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(BillManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

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
        if (msg instanceof APIGetBillMsg) {
            handle((APIGetBillMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetBillMsg msg) {
        BillVO vo = dbf.findByUuid(msg.getUuid(), BillVO.class);
        Timestamp billTimestamp = vo.getBillDate();
        Timestamp startTime = new BillJob(dbf).getLastMonthFirstDay(billTimestamp);
        Timestamp endTime = new BillJob(dbf).getLastMonthLastDay(billTimestamp);
        List<MonetaryResult> bills = getProudctTypeCount(msg.getSession().getAccountUuid(), startTime, endTime);
        Map<String, MonetaryResult> map = new HashMap<>();
        if (bills != null && bills.size() > 0) {
            map = list2Map(bills);
            List<MonetaryOrderType> monetaries = getMonetaryOrderType(msg.getSession().getAccountUuid(), startTime, endTime);
            for (MonetaryOrderType monetary : monetaries) {
                MonetaryResult result = map.get(monetary.getType().name());
                result.setRefundPresent(BigDecimal.ZERO);
                result.setRefundCash(BigDecimal.ZERO);
                result.setDeductionPresent(BigDecimal.ZERO);
                result.setDeductionCash(BigDecimal.ZERO);
                if (monetary.getOrderType() == OrderType.BUY || monetary.getOrderType() == OrderType.RENEW || monetary.getOrderType() == OrderType.UPGRADE) {
                    result.setDeductionCash((result.getDeductionCash() == null ? BigDecimal.ZERO : result.getDeductionCash()).add(monetary.getPayCashTotal()));
                    result.setDeductionPresent((result.getDeductionPresent() == null ? BigDecimal.ZERO : result.getDeductionPresent()).add(monetary.getPayPresentTotal()));
                } else if (monetary.getOrderType() == OrderType.UN_SUBCRIBE || monetary.getOrderType() == OrderType.DOWNGRADE) {
                    result.setRefundCash((result.getRefundCash() == null ? BigDecimal.ZERO : result.getRefundCash()).add(monetary.getPayCashTotal()));
                    result.setRefundPresent((result.getRefundPresent() == null ? BigDecimal.ZERO : result.getRefundPresent()).add(monetary.getPayPresentTotal()));
                }
            }
        }
        bills = map2List(map);
        BillInventory inventory = BillInventory.valueOf(vo);
        APIGetBillReply reply = new APIGetBillReply();
        inventory.setBills(bills);
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    private List<MonetaryResult> map2List(Map<String, MonetaryResult> map) {
        List<MonetaryResult> list = new ArrayList<>();
        for (Map.Entry<String, MonetaryResult> entry : map.entrySet()) {
            list.add(entry.getValue());
        }
        return list;
    }

    private Map<String, MonetaryResult> list2Map(List<MonetaryResult> list) {
        Map<String, MonetaryResult> map = new HashMap<>();
        for (MonetaryResult t : list) {
            map.put(t.getType().name(), t);
        }
        return map;
    }


    private List<MonetaryResult> getProudctTypeCount(String accountUuid, Timestamp startTime, Timestamp endTime) {
        String sql = "select productType,count(DISTINCT productUuid) as categoryCount from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime BETWEEN :dateStart and  :dateEnd  group by productType ";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", accountUuid);
        q.setParameter("dateStart", startTime);
        q.setParameter("dateEnd", endTime);
        List<Object[]> objs = q.getResultList();
        List<MonetaryResult> bills = objs.stream().map(MonetaryResult::new).collect(Collectors.toList());
        return bills;
    }

    private List<MonetaryOrderType> getMonetaryOrderType(String accountUuid, Timestamp startTime, Timestamp endTime) {
        String sql = "select productType,TYPE as orderType, SUM(payPresent) AS payPresentTotal,SUM(payCash) AS payCashTotal  from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime BETWEEN :dateStart and  :dateEnd  group by productType,type ";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", accountUuid);
        q.setParameter("dateStart", startTime);
        q.setParameter("dateEnd", endTime);
        List<Object[]> objs = q.getResultList();
        List<MonetaryOrderType> bills = objs.stream().map(MonetaryOrderType::new).collect(Collectors.toList());
        return bills;
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_BILL);
    }

    @Override
    public boolean start() {
        GLock lock = new GLock(String.format("id-%s", "initBill"), 120);
        lock.lock();
        try {
            new BillJob(dbf).generateBill();
        } catch (Exception e) {
            throw new CloudRuntimeException(e);
        } finally {
            lock.unlock();
        }
        return true;
    }


    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        return msg;
    }


}
