package com.syscxp.billing.renew;

import com.syscxp.billing.BillingGlobalConfig;
import com.syscxp.billing.header.bill.BillJob;
import com.syscxp.billing.header.renew.*;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.config.GlobalConfigFacadeImpl;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.GLock;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class RenewManagerImpl  extends AbstractService implements  ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(RenewManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private GlobalConfigFacadeImpl globalConfigFacadeImpl;
    private static final String RenewName ="bill";
    private static final String RENEWGROUP ="bill";


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
        if (msg instanceof APIUpdateRenewMsg) {
            handle((APIUpdateRenewMsg) msg);
        } else if (msg instanceof APIUpdateRenewPriceMsg) {
            handle((APIUpdateRenewPriceMsg) msg);
        }  else if (msg instanceof APIDeleteExpiredRenewMsg) {
            handle((APIDeleteExpiredRenewMsg) msg);
        }  else if (msg instanceof APIRenameProductNameMsg) {
            handle((APIRenameProductNameMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIRenameProductNameMsg msg) {
        UpdateQuery.New(RenewVO.class).condAnd(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid()).set(RenewVO_.productName, msg.getProductName()).update();
        APIRenameProductNameReply reply = new APIRenameProductNameReply();
        reply.setSuccess(true);
        bus.reply(msg,reply);
    }

    private void handle(APIDeleteExpiredRenewMsg msg) {
        UpdateQuery q = UpdateQuery.New(RenewVO.class);
        q.condAnd(RenewVO_.accountUuid, SimpleQuery.Op.EQ, msg.getAccountUuid());
        q.condAnd(RenewVO_.productUuid, SimpleQuery.Op.EQ, msg.getProductUuid());
        q.delete();
        APIDeleteExpiredRenewReply reply = new APIDeleteExpiredRenewReply();
        reply.setInventory(true);
        bus.reply(msg,reply);
    }

    private void handle(APIUpdateRenewPriceMsg msg) {
        if (msg.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(" input valuable vlue");
        }
        RenewVO vo = dbf.findByUuid(msg.getUuid(), RenewVO.class);
        vo.setPriceDiscount(msg.getPrice());
        dbf.updateAndRefresh(vo);
        saveRenewPriceLog(vo.getAccountUuid(), msg.getSession().getAccountUuid(), vo.getProductUuid(), msg.getSession().getUserUuid(), vo.getPriceDiscount(), msg.getPrice());
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewPriceEvent evt = new APIUpdateRenewPriceEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
    }

    private void saveRenewPriceLog(String accountUuid,String opAccountUuid,String productUuid,String opUserUuid,BigDecimal originPrice,BigDecimal nowPrice) {
        RenewPriceLogVO vo = new RenewPriceLogVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(accountUuid);
        vo.setOpAccountUuid(opAccountUuid);
        vo.setProductUuid(productUuid);
        vo.setOpUserUuid(opUserUuid);
        vo.setOriginPrice(originPrice);
        vo.setNowPrice(nowPrice);
        dbf.persistAndRefresh(vo);
    }

    private void handle(APIUpdateRenewMsg msg) {
        RenewVO vo = dbf.findByUuid(msg.getUuid(), RenewVO.class);
        if (vo.isRenewAuto() != msg.isRenewAuto()) {
            vo.setRenewAuto(msg.isRenewAuto());
        }
        dbf.updateAndRefresh(vo);
        RenewInventory ri = RenewInventory.valueOf(vo);
        APIUpdateRenewEvent evt = new APIUpdateRenewEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_RENEW);
    }

    @Override
    public boolean start() {
        startGenerateRenewJob();
        BillingGlobalConfig.RENEW_JOB.installUpdateExtension((oldConfig, newConfig) -> {
            updateCron(oldConfig.value(), newConfig.value());
        });
        return true;
    }

    private void startGenerateRenewJob(){
        GLock lock = new GLock(String.format("id-%s", "initRenew"), 120);
        lock.lock();
        try {
            scheduler.start();
            String value = globalConfigFacadeImpl.getConfigValue(BillingGlobalConfig.CATEGORY, BillingGlobalConfig.RENEWJOBEXPRESSION, String.class);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(value);
            JobDetail job = JobBuilder.newJob(RenewJob.class)
                    .withIdentity(RenewName, RENEWGROUP)
                    .build();
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(RenewName, RENEWGROUP)
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
        TriggerKey triggerKey = TriggerKey.triggerKey(RenewName, RENEWGROUP);
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
            triggerBuilder.withIdentity(RenewName, RENEWGROUP);
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
    public boolean stop() {
        return true;
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
