package org.zstack.account.log;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.zstack.account.header.account.AccountVO;
import org.zstack.account.header.account.AccountVO_;
import org.zstack.account.header.log.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.notification.APICreateNotificationMsg;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.sql.Timestamp;

import static org.zstack.core.Platform.argerr;

public class NoticeManagerImpl extends AbstractService implements NoticeManager, ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(NoticeManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private DbEntityLister dl;

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
            handle((APICreateNoticeMsg) msg);
        } else if (msg instanceof APIUpdateNoticeMsg) {
            handle((APIUpdateNoticeMsg) msg);
        } else if (msg instanceof APIDeleteNoticeMsg) {
            handle((APIDeleteNoticeMsg) msg);
        } else if (msg instanceof APICreateAlarmContactMsg) {
            handle((APICreateAlarmContactMsg) msg);
        } else if (msg instanceof APIUpdateAlarmContactMsg) {
            handle((APIUpdateAlarmContactMsg) msg);
        } else if (msg instanceof APIDeleteAlarmContactMsg) {
            handle((APIDeleteAlarmContactMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateAlarmContactMsg msg) {
        AlarmContactVO vo = new AlarmContactVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setPhone(msg.getPhone());
        vo.setEmail(msg.getEmail());
        vo.setChannel(msg.getChannel());
        vo.setAccountName(msg.getAccountName());
        vo.setCompany(msg.getCompany());

        AlarmContactVO alvo = dbf.persistAndRefresh(vo);
        AlarmContactInventory inv = AlarmContactInventory.valueOf(alvo);
        APICreateAlarmContactEvent evt = new APICreateAlarmContactEvent(msg.getId());
        evt.setInventory(inv);
        bus.publish(evt);

    }

    private void handle(APIUpdateAlarmContactMsg msg) {
        AlarmContactVO vo = dbf.findByUuid(msg.getUuid(), AlarmContactVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getName())) {
            vo.setName(msg.getName());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getPhone())) {
            vo.setPhone(msg.getPhone());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getEmail())) {
            vo.setEmail(msg.getEmail());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getChannel())) {
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

    private void handle(APIDeleteAlarmContactMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), AlarmContactVO.class);

        APIDeleteAlarmContactEvent evt = new APIDeleteAlarmContactEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIDeleteNoticeMsg msg) {
        dbf.removeByPrimaryKey(msg.getUuid(), NoticeVO.class);

        APIDeleteNoticeEvent evt = new APIDeleteNoticeEvent(msg.getId());
        bus.publish(evt);
    }

    private void handle(APIUpdateNoticeMsg msg) {
        NoticeVO nvo = dbf.findByUuid(msg.getUuid(), NoticeVO.class);
        boolean update = false;
        if (!StringUtils.isEmpty(msg.getTitle())) {
            nvo.setTitle(msg.getTitle());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getLink())) {
            nvo.setLink(msg.getLink());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getStartTime())) {
            nvo.setStartTime(msg.getStartTime());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getEndTime())) {
            nvo.setEndTime(msg.getEndTime());
            update = true;
        }
        if (!StringUtils.isEmpty(msg.getStatus())) {
            nvo.setStatus(NoticeStatus.valueOf(msg.getStatus()));
            update = true;
        }
        if (update)
            nvo = dbf.updateAndRefresh(nvo);

        APIUpdateNoticeEvent evt = new APIUpdateNoticeEvent(msg.getId());
        NoticeInventory inv = NoticeInventory.valueOf(nvo);
        evt.setInventory(inv);
        bus.publish(evt);

    }

    private void handle(APICreateNoticeMsg msg) {
        NoticeVO noticeVO = new NoticeVO();
        noticeVO.setUuid(Platform.getUuid());
        noticeVO.setTitle(msg.getTitle());
        noticeVO.setLink(msg.getLink());
        noticeVO.setStartTime(msg.getStartTime());
        noticeVO.setEndTime(msg.getEndTime());
        noticeVO.setStatus(NoticeStatus.INVALID);

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
        } else if (msg instanceof APICreateNotificationMsg) {
            validate((APICreateNotificationMsg) msg);
        }

        return msg;
    }

    private void validate(APICreateNotificationMsg msg) {
    }

    private void validate(APIUpdateAlarmContactMsg msg) {
    }

    private void validate(APICreateAlarmContactMsg msg) {
        SimpleQuery<AccountVO> q = dbf.createQuery(AccountVO.class);
        q.add(AccountVO_.name, SimpleQuery.Op.EQ, msg.getAccountName());
        AccountVO account = q.find();
        if (account == null) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Account[name:%S] does not exist.", msg.getAccountName()
            ));
        }
        if (StringUtils.isEmpty(msg.getPhone()) && StringUtils.isEmpty(msg.getEmail())){
            throw new ApiMessageInterceptionException(argerr(
                    "The phone or email must be not null at least."
            ));
        }
    }
    private void validate(APIUpdateNoticeMsg msg) {
        if (!dbf.isExist(msg.getUuid(), NoticeVO.class)) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Notice[uuid:%S] does not exist.", msg.getUuid()
            ));
        }

        checkStartAndEndTime(msg.getStartTime(), msg.getEndTime());
    }

    private void checkStartAndEndTime(Timestamp start, Timestamp end) {
        if (start.after(end)) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Start time must be earlier than end time ."
            ));
        }
        if (dbf.getCurrentSqlTime().after(end)) {
            throw new ApiMessageInterceptionException(argerr(
                    "The end time must be later than the current time ."
            ));
        }
    }

    private void validate(APICreateNoticeMsg msg) {

        checkStartAndEndTime(msg.getStartTime(), msg.getEndTime());
    }

}
