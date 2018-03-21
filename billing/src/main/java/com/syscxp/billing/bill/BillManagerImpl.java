package com.syscxp.billing.bill;

import com.syscxp.billing.BillingGlobalConfig;
import com.syscxp.billing.header.bill.*;
import com.syscxp.core.config.GlobalConfigFacadeImpl;
import com.syscxp.core.config.GlobalConfigVO;
import com.syscxp.core.db.GLock;
import com.syscxp.header.billing.BillingConstant;
import com.syscxp.header.billing.OrderType;
import net.sf.cglib.core.Local;
import org.quartz.*;
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
    private Scheduler scheduler;

    @Autowired
    private GlobalConfigFacadeImpl globalConfigFacadeImpl;
    private static final String billName="bill";
    private static final String billGroup="bill";

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
        }else if(msg instanceof APIGetCurrentMonthBillMsg){
            handle((APIGetCurrentMonthBillMsg)msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetCurrentMonthBillMsg msg) {
        APIGetCurrentMonthBillReply reply = new APIGetCurrentMonthBillReply();
        LocalDateTime now = dbf.getCurrentSqlTime().toLocalDateTime();
        LocalDate localDate = LocalDate.of(now.getYear(), now.getMonth(), 1);
        List<MonetaryResult> bills = getBills(msg.getAccountUuid(), Timestamp.valueOf(LocalDateTime.of(localDate, LocalTime.MIN)), Timestamp.valueOf(now));
        reply.setInventory(bills);
        bus.reply(msg, reply);
    }

    private void handle(APIGetBillMsg msg) {
        BillVO vo = dbf.findByUuid(msg.getUuid(), BillVO.class);
        Timestamp billTimestamp = vo.getBillDate();
        Timestamp startTime = new BillJob(dbf).getLastMonthFirstDay(billTimestamp);
        Timestamp endTime = new BillJob(dbf).getLastMonthLastDay(billTimestamp);
        List<MonetaryResult> bills = getBills(vo.getAccountUuid(), startTime, endTime);
        BillInventory inventory = BillInventory.valueOf(vo);
        APIGetBillReply reply = new APIGetBillReply();
        inventory.setBills(bills);
        reply.setInventory(inventory);
        bus.reply(msg, reply);

    }

    private List<MonetaryResult> getBills(String accountUuid, Timestamp startTime,Timestamp endTime ){

        List<MonetaryResult> bills = getProudctTypeCount(accountUuid, startTime, endTime);
        Map<String, MonetaryResult> map = new HashMap<>();
        if (bills != null && bills.size() > 0) {
            map = list2Map(bills);
            List<MonetaryOrderType> monetaries = getMonetaryOrderType(accountUuid, startTime, endTime);
            for (MonetaryOrderType monetary : monetaries) {
                MonetaryResult result = map.get(monetary.getType().name());
                if (monetary.getOrderType() == OrderType.BUY || monetary.getOrderType() == OrderType.RENEW || monetary.getOrderType() == OrderType.AUTORENEW || monetary.getOrderType() == OrderType.UPGRADE) {
                    result.setDeductionCash((result.getDeductionCash() == null ? BigDecimal.ZERO : result.getDeductionCash()).add(monetary.getPayCashTotal()));
                    result.setDeductionPresent((result.getDeductionPresent() == null ? BigDecimal.ZERO : result.getDeductionPresent()).add(monetary.getPayPresentTotal()));
                } else if (monetary.getOrderType() == OrderType.UN_SUBCRIBE || monetary.getOrderType() == OrderType.DOWNGRADE) {
                    result.setRefundCash((result.getRefundCash() == null ? BigDecimal.ZERO : result.getRefundCash()).add(monetary.getPayCashTotal()));
                    result.setRefundPresent((result.getRefundPresent() == null ? BigDecimal.ZERO : result.getRefundPresent()).add(monetary.getPayPresentTotal()));
                }
            }
        }
        return map2List(map);
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

        startGenerateBillJob();
        BillingGlobalConfig.BILL_JOB.installUpdateExtension((oldConfig, newConfig) -> {
            updateCron(oldConfig.value(), newConfig.value());
        });
            return true;
        }

        private void startGenerateBillJob(){
            GLock lock = new GLock(String.format("id-%s", "initBill"), 120);
            lock.lock();
            try {
                scheduler.start();
                String value = globalConfigFacadeImpl.getConfigValue(BillingGlobalConfig.CATEGORY, BillingGlobalConfig.BILLJOBEXPRESSION, String.class);
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(value);
                JobDetail job = JobBuilder.newJob(BillJob.class)
                        .withIdentity(billName, billGroup)
                        .build();
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(billName, billGroup)
                        .withSchedule(
                                scheduleBuilder)
                        .build();
                // JobDataMap map =  job.getJobDataMap();
                scheduler.scheduleJob(job, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }

        private void updateCron(String oldValue,String  newValue) {
            TriggerKey triggerKey = TriggerKey.triggerKey(billName, billGroup);
            CronTrigger trigger = null;
            try {
                trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
            if (trigger == null) {
                return;
            }
            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(newValue)) {
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
                triggerBuilder.withIdentity(billName, billGroup);
                triggerBuilder.startNow();
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newValue));
                trigger = (CronTrigger) triggerBuilder.build();
                try {
                    scheduler.rescheduleJob(triggerKey, trigger);
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
            }
        }


        @Override
        public boolean stop () {
            return true;
        }

        @Override
        public APIMessage intercept (APIMessage msg) throws ApiMessageInterceptionException {

            return msg;
        }


    }
