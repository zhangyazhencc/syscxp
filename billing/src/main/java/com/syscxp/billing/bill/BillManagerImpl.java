package com.syscxp.billing.bill;

import com.syscxp.billing.header.bill.*;
import com.syscxp.header.billing.BillingConstant;
import org.springframework.beans.factory.annotation.Autowired;

import com.syscxp.billing.header.bill.*;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Query;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class BillManagerImpl extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(BillManagerImpl.class);

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
        if (msg instanceof APIGetBillMsg) {
            handle((APIGetBillMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetBillMsg msg) {
        BillVO vo = dbf.findByUuid(msg.getUuid(), BillVO.class);

        Timestamp billTimestamp = vo.getBillDate();
        LocalDateTime localDateTime =  billTimestamp.toLocalDateTime();
        LocalDate date = LocalDate.of(localDateTime.getYear(),localDateTime.getMonth(),1);
        LocalTime time = LocalTime.MIN;
        localDateTime =  LocalDateTime.of(date,time);
        String accountUuid = msg.getSession().getAccountUuid();
        String sql = "select productType, count(*) as categoryCount, sum(payPresent) as payPresentTotal,sum(payCash) as payCashTotal from OrderVO where accountUuid = :accountUuid and state = 'PAID' and payTime BETWEEN :dateStart and  :dateEnd  group by productType ";
        Query q = dbf.getEntityManager().createNativeQuery(sql);
        q.setParameter("accountUuid", accountUuid);
        q.setParameter("dateStart", Timestamp.valueOf(localDateTime));
        q.setParameter("dateEnd", Timestamp.valueOf(localDateTime.plusMonths(1).minusSeconds(1)));
        List<Object[]> objs = q.getResultList();
        List<Monetary> bills = objs.stream().map(Monetary::new).collect(Collectors.toList());
        BillInventory inventory = BillInventory.valueOf(vo);
        APIGetBillReply reply = new APIGetBillReply();
        inventory.setBills(bills);
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_BILL);
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

        return msg;
    }


}
