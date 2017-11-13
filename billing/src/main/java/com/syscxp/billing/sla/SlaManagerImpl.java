package com.syscxp.billing.sla;

import com.syscxp.billing.BillingGlobalProperty;
import com.syscxp.billing.header.sla.*;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.*;
import com.syscxp.header.message.APISyncCallMessage;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.tunnel.tunnel.APIUpdateExpireDateMsg;
import com.syscxp.header.tunnel.tunnel.APIUpdateExpireDateReply;
import com.syscxp.header.tunnel.tunnel.APIUpdateInterfaceExpireDateMsg;
import com.syscxp.header.tunnel.tunnel.APIUpdateTunnelExpireDateMsg;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.DbEntityLister;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.identity.AccountType;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.transaction.annotation.Transactional;

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

            ProductCaller caller = new ProductCaller(slaCompensateVO.getProductType());
            APIUpdateExpireDateMsg aMsg = new APIUpdateExpireDateMsg();

            aMsg.setUuid(slaCompensateVO.getProductUuid());
            aMsg.setDuration(slaCompensateVO.getDuration());
            aMsg.setProductChargeModel(ProductChargeModel.BY_DAY);
            aMsg.setType(OrderType.SLA_COMPENSATION);
            aMsg.setAccountUuid(msg.getSession().getAccountUuid());
            aMsg.setSession(msg.getSession());

            String gstr = RESTApiDecoder.dumpWithSession(aMsg);
            RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, RestAPIResponse.class);

            if (rsp.getState().equals(RestAPIState.Done.toString())) {
                try {
                    APIUpdateExpireDateReply productReply = (APIUpdateExpireDateReply) RESTApiDecoder.loads(rsp.getResult());
                }catch (Exception e){
                   throw new IllegalArgumentException(e);
                }
            } else {
                throw new IllegalArgumentException("the network is not fine ,try for a moment");
            }

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
        return bus.makeLocalServiceId(BillingConstant.SERVICE_ID_SLA);
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

