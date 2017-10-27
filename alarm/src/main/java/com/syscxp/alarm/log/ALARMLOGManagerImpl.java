package com.syscxp.alarm.log;

import com.syscxp.alarm.header.contact.*;
import com.syscxp.alarm.header.log.APICreateAlarmLogMsg;
import com.syscxp.alarm.header.log.AlarmLogVO;
import com.syscxp.alarm.header.log.AlarmStatus;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.alarm.AlarmConstant;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Tuple;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ALARMLOGManagerImpl  extends AbstractService implements ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(ALARMLOGManagerImpl.class);

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
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateAlarmLogMsg) {
            handle((APICreateAlarmLogMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateAlarmLogMsg msg) {
        AlarmLogVO alarmLogVO = new AlarmLogVO();
        alarmLogVO.setUuid(Platform.getUuid());
        alarmLogVO.setProductUuid(msg.getTunnel_id());
        alarmLogVO.setProductName(msg.getTunnel_name());
        alarmLogVO.setAlarmContent(msg.getProblem());
        alarmLogVO.setResumeTime(msg.getResumed());
        alarmLogVO.setAlarmTime(msg.getCreated());
        alarmLogVO.setStatus(AlarmStatus.nameOf(msg.getStatus()));
        alarmLogVO.setAccountUuid(msg.getUser_id());
        alarmLogVO.setProductType(null);//todo  where should acquire
        //alarmLogVO.setDuration(0);//持续时间
        //alarmLogVO.setDurationTimeUnit(TimeUnit.MILLISECONDS);
        dbf.persistAndRefresh(alarmLogVO);
        SimpleQuery<ContactGroupVO> query = dbf.createQuery(ContactGroupVO.class);
        query.add(ContactGroupVO_.accountUuid, SimpleQuery.Op.EQ,msg.getUser_id());
        List<ContactGroupVO> groups = query.list();
        for(ContactGroupVO contactGroupVO : groups){
            for(ContactVO contactVO: contactGroupVO.getContactVOList()){
                Set<NotifyWayVO> notifyWayVOs = contactVO.getNotifyWayVOs();
                for(NotifyWayVO notifyWayVO: notifyWayVOs){

                }
            }
        }
        //foreach发送短信和邮件


    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(AlarmConstant.SERVICE_ID_ALARM_LOG);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        return msg;
    }
}
