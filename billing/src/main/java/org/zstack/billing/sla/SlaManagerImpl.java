package org.zstack.billing.sla;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.billing.header.sla.*;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.billing.*;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.identity.AccountType;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.sql.Timestamp;

public class SlaManagerImpl  extends AbstractService implements  ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(SlaManagerImpl.class);

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

        if (msg instanceof APICreateSLACompensateMsg) {
            handle((APICreateSLACompensateMsg) msg);
        } else if (msg instanceof APIUpdateSLACompensateMsg) {
            handle((APIUpdateSLACompensateMsg) msg);
        }else if (msg instanceof APIUpdateSLACompensateStateMsg) {
            handle((APIUpdateSLACompensateStateMsg) msg);
        }  else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIUpdateSLACompensateStateMsg msg) {
        SLACompensateVO slaCompensateVO = dbf.findByUuid(msg.getUuid(), SLACompensateVO.class);
        String productUuid = slaCompensateVO.getProductUuid();
        //todo get product expired
        if(msg.getState() == SLAState.APPLIED){
            Timestamp expiredTime = dbf.getCurrentSqlTime();
            Timestamp endTime = new Timestamp(expiredTime.getTime()+slaCompensateVO.getDuration()*24*60*60*1000);
            slaCompensateVO.setTimeStart(expiredTime);
            slaCompensateVO.setTimeStart(endTime);
            slaCompensateVO.setState(SLAState.APPLIED);
            slaCompensateVO.setApplyTime(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(slaCompensateVO);
        } else if(msg.getState() == SLAState.DONE){
            if(msg.getSession().getType() != AccountType.SystemAdmin){
                throw new RuntimeException("you have not the permission to do this");
            }
            //todo modify the product expired time
            slaCompensateVO.setState(SLAState.DONE);
            dbf.updateAndRefresh(slaCompensateVO);
        }
        SLACompensateInventory ri = SLACompensateInventory.valueOf(slaCompensateVO);
        APIUpdateSLACompensateStateEvent evt = new APIUpdateSLACompensateStateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }


    private void handle(APIUpdateSLACompensateMsg msg) {
        SLACompensateVO vo = dbf.findByUuid(msg.getUuid(), SLACompensateVO.class);

        if (msg.getComment() != null) {
            vo.setComment(msg.getComment());
        }
        if (msg.getDuration() != null) {
            vo.setDuration(msg.getDuration());
        }
        if (msg.getProductName() != null) {
            vo.setProductName(msg.getProductName());
        }
        if (msg.getProductType() != null) {
            vo.setProductType(msg.getProductType());
        }
        if (msg.getReason() != null) {
            vo.setReason(msg.getReason());
        }
        if (msg.getTimeStart() != null) {
            vo.setTimeStart(msg.getTimeStart());
        }
        if (msg.getTimeEnd() != null) {
            vo.setTimeEnd(msg.getTimeEnd());
        }
        if (msg.getProductUuid() != null) {
            vo.setProductUuid(msg.getProductUuid());
        }
        dbf.updateAndRefresh(vo);
        SLACompensateInventory ri = SLACompensateInventory.valueOf(vo);
        APIUpdateSLACompensateEvent evt = new APIUpdateSLACompensateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);

    }

    private void handle(APICreateSLACompensateMsg msg) {
        SLACompensateVO vo = new SLACompensateVO();
        vo.setUuid(Platform.getUuid());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setComment(msg.getComment());
        vo.setDuration(msg.getDuration());
        vo.setProductUuid(msg.getProductUuid());
        vo.setProductName(msg.getProductName());
        vo.setProductType(msg.getProductType());
        vo.setReason(msg.getReason());
        vo.setState(SLAState.NOT_APPLY);
        dbf.persistAndRefresh(vo);
        SLACompensateInventory ri = SLACompensateInventory.valueOf(vo);
        APICreateSLACompensateEvent evt = new APICreateSLACompensateEvent(msg.getId());
        evt.setInventory(ri);
        bus.publish(evt);
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

        return msg;
    }

    private void validate(APICreateOrderMsg msg) {

    }

}

