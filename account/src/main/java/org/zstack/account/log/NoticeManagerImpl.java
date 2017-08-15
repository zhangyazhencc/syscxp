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

    @Autowired
    private EntityManagerFactory entityManagerFactory;


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
            hanle((APICreateNoticeMsg)msg);
        } else if (msg instanceof APIUpdateNoticeMsg) {
            hanle((APIUpdateNoticeMsg)msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void hanle(APIUpdateNoticeMsg msg) {
        NoticeVO noticeVO = dbf.findByUuid(msg.getUuid(), NoticeVO.class);

        if (msg.getTitle()!=null){
            noticeVO.setTitle(msg.getTitle());
        }
        if (msg.getLink()!=null){
            noticeVO.setLink(msg.getLink());
        }
        if (msg.getStartTime()!=null){
            noticeVO.setStartTime(msg.getStartTime());
        }
        if (msg.getEndTime()!=null){
            noticeVO.setEndTime(msg.getEndTime());
        }

        APIUpdateNoticeEvent evt = new APIUpdateNoticeEvent(msg.getId());
        NoticeInventory inv = NoticeInventory.valueOf(noticeVO);
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
        if (!(msg instanceof APICreateNoticeMsg)) {
            validate((APICreateNoticeMsg) msg);
        } else if (msg instanceof APIUpdateNoticeMsg) {
            validate((APIUpdateNoticeMsg) msg);
        }

        return msg;
    }

    private void validate(APIUpdateNoticeMsg msg) {
        if (dbf.isExist(msg.getUuid(), NoticeVO.class)) {
            throw new ApiMessageInterceptionException(argerr(
                    "The Notice[uuid:%S] does not exist."
            ));
        }

        checkStartAndEndTime(msg.getStartTime(),(msg.getEndTime()));
    }

    private void checkStartAndEndTime(Timestamp start, Timestamp end){
        if (start.after(end)){
            throw new ApiMessageInterceptionException(argerr(
                    "The Start time must be earlier than end time ."
            ));
        }
        if (dbf.getCurrentSqlTime().after(end)){
            throw new ApiMessageInterceptionException(argerr(
                    "The end time must be later than the current time  ."
            ));
        }
    }

    private void validate(APICreateNoticeMsg msg) {

        checkStartAndEndTime(msg.getStartTime(),(msg.getEndTime()));
    }

}
