package org.zstack.account.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.account.header.log.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusEventListener;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Event;
import org.zstack.header.message.Message;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import javax.persistence.EntityManagerFactory;
import java.sql.Timestamp;

import static org.zstack.core.Platform.argerr;

public class NoticeManagerImpl extends AbstractService implements NoticeManager, CloudBusEventListener, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(LogManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;


    @Override
    public boolean handleEvent(Event e) {
        return false;
    }

    @Override
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
        if (msg instanceof APICreateNoticeMsg) {
            hanle((APICreateNoticeMsg) msg);
        } else if (msg instanceof APIUpdateNoticeMsg) {
            hanle((APIUpdateNoticeMsg) msg);
        } else if (msg instanceof APIDeleteNoticeMsg) {
            hanle((APIDeleteNoticeMsg) msg);
        } else if (msg instanceof APICreateAlarmContactMsg) {
            hanle((APICreateAlarmContactMsg) msg);
        } else if (msg instanceof APIUpdateAlarmContactMsg) {
            hanle((APIUpdateAlarmContactMsg) msg);
        } else if (msg instanceof APIDeleteAlarmContactMsg) {
            hanle((APIDeleteAlarmContactMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void hanle(APICreateAlarmContactMsg msg) {
        AlarmContactVO vo = new AlarmContactVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setPhone(msg.getPhone());
        vo.setEmail(msg.getEmail());
        vo.setChannel(msg.getChannel());
        vo.setAccountUuid(msg.getAccountUuid());

        AlarmContactVO alvo = dbf.persistAndRefresh(vo);
        AlarmContactInventory inv = AlarmContactInventory.valueOf(alvo);
        APICreateAlarmContactEvent evt = new APICreateAlarmContactEvent(msg.getId());
        evt.setInventory(inv);
        bus.publish(evt);

    }
    private void hanle(APIUpdateAlarmContactMsg msg) {
        AlarmContactVO vo = dbf.findByUuid(msg.getUuid(), AlarmContactVO.class);
        boolean update = false;
        if (msg.getName()!=null){
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getPhone()!=null){
            vo.setPhone(msg.getPhone());
            update = true;
        }
        if (msg.getEmail()!=null){
            vo.setEmail(msg.getEmail());
            update = true;
        }
        if (msg.getChannel()!=null){
            vo.setChannel(msg.getChannel());
            update = true;
        }
        if (update)
            vo = dbf.updateAndRefresh(vo);
        APIUpdateAlarmContactEvent evt = new APIUpdateAlarmContactEvent(msg.getId());
        AlarmContactInventory inv = AlarmContactInventory.valueOf(vo);
        evt.setInventory(inv);
        bus.publish(evt);

    }
    private void hanle(APIDeleteAlarmContactMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(),AlarmContactVO.class);

        APIDeleteAlarmContactEvent evt = new APIDeleteAlarmContactEvent(msg.getId());
        bus.publish(evt);
    }

    private void hanle(APIDeleteNoticeMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), NoticeVO.class);

        APIDeleteNoticeEvent evt = new APIDeleteNoticeEvent(msg.getId());
        bus.publish(evt);
    }

    private void hanle(APIUpdateNoticeMsg msg) {
        NoticeVO nvo = dbf.findByUuid(msg.getUuid(), NoticeVO.class);
        boolean update = false;
        if (msg.getTitle() != null) {
            nvo.setTitle(msg.getTitle());
            update = true;
        }
        if (msg.getLink() != null) {
            nvo.setLink(msg.getLink());
            update = true;
        }
        if (msg.getStartTime() != null) {
            nvo.setStartTime(msg.getStartTime());
            update = true;
        }
        if (msg.getEndTime() != null) {
            nvo.setEndTime(msg.getEndTime());
            update = true;
        }
        if (update)
            nvo = dbf.updateAndRefresh(nvo);

        APIUpdateNoticeEvent evt = new APIUpdateNoticeEvent(msg.getId());
        NoticeInventory inv = NoticeInventory.valueOf(nvo);
        evt.setInventory(inv);
        bus.publish(evt);

    }

    private void hanle(APICreateNoticeMsg msg) {
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setUuid(Platform.getUuid());
        noticeVO.setTitle(msg.getTitle());
        noticeVO.setLink(msg.getLink());
        noticeVO.setStartTime(msg.getStartTime());
        noticeVO.setEndTime(msg.getEndTime());
        noticeVO.setStatus(NoticeStatus.NORMAL);

        NoticeVO nvo = dbf.persistAndRefresh(noticeVO);

        APICreateNoticeEvent evt = new APICreateNoticeEvent(msg.getId());
        NoticeInventory inv = NoticeInventory.valueOf(nvo);
        evt.setInventory(inv);
        bus.publish(evt);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(NoticeConstant.SERVICE_ID);
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
        if (msg instanceof APICreateNoticeMsg) {
            validate((APICreateNoticeMsg) msg);
        } else if (msg instanceof APIUpdateNoticeMsg) {
            validate((APIUpdateNoticeMsg) msg);
        } else if (msg instanceof APICreateAlarmContactMsg) {
            validate((APICreateAlarmContactMsg) msg);
        } else if (msg instanceof APIUpdateAlarmContactMsg) {
            validate((APIUpdateAlarmContactMsg) msg);
        }

        return msg;
    }

    private void validate(APIUpdateAlarmContactMsg msg) {
        if (!dbf.isExist(msg.getUuid(), AlarmContactVO.class)){
            throw new ApiMessageInterceptionException(argerr(
                    "The AlarmContact[uuid:%S] does not exist.", msg.getUuid()
            ));
        }
    }

    private void validate(APICreateAlarmContactMsg msg) {
    }

    private void validate(APIUpdateNoticeMsg msg) {
        if (!dbf.isExist(msg.getUuid(), NoticeVO.class)) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Notice[uuid:%S] does not exist.", msg.getUuid()
            ));
        }

        checkStartAndEndTime(msg.getStartTime(), (msg.getEndTime()));
    }

    private void checkStartAndEndTime(Timestamp start, Timestamp end) {
        if (start.after(end)) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Start time must be earlier than end time ."
            ));
        }
    }

    private void validate(APICreateNoticeMsg msg) {

        checkStartAndEndTime(msg.getStartTime(), msg.getEndTime());
    }

}
