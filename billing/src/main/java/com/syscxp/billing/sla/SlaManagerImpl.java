package com.syscxp.billing.sla;

import com.syscxp.billing.header.renew.RenewVO;
import com.syscxp.billing.header.renew.RenewVO_;
import com.syscxp.billing.header.sla.*;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.billing.*;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.tunnel.tunnel.APISLAInterfaceMsg;
import com.syscxp.header.tunnel.tunnel.APISLAInterfaceReply;
import com.syscxp.header.tunnel.tunnel.APISalTunnelMsg;
import com.syscxp.header.tunnel.tunnel.APISalTunnelReply;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
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

import java.sql.Timestamp;

public class SlaManagerImpl  extends AbstractService implements  ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(SlaManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
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
            SimpleQuery<RenewVO> query = dbf.createQuery(RenewVO.class);
            query.add(RenewVO_.accountUuid, SimpleQuery.Op.EQ, slaCompensateVO.getAccountUuid());
            query.add(RenewVO_.productUuid, SimpleQuery.Op.EQ, slaCompensateVO.getProductUuid());
            RenewVO renewVO = query.find();
            if(renewVO == null){
                throw new IllegalArgumentException("can not find the product");
            }

            Timestamp expiredTime = renewVO.getExpiredTime();
            Timestamp endTime = new Timestamp(expiredTime.getTime()+slaCompensateVO.getDuration()*24*60*60*1000);
            slaCompensateVO.setTimeStart(expiredTime);
            slaCompensateVO.setTimeEnd(endTime);
            slaCompensateVO.setState(SLAState.APPLIED);
            slaCompensateVO.setApplyTime(dbf.getCurrentSqlTime());
            dbf.updateAndRefresh(slaCompensateVO);
        } else if(msg.getState() == SLAState.DONE){
            if(msg.getSession().getType() != AccountType.SystemAdmin){
                throw new RuntimeException("you have not the permission to do this");
            }

            ProductCaller caller = new ProductCaller(slaCompensateVO.getProductType());
            if(slaCompensateVO.getProductType().equals(ProductType.TUNNEL)){
                APISalTunnelMsg aMsg = new APISalTunnelMsg();
                aMsg.setUuid(slaCompensateVO.getProductUuid());
                aMsg.setDuration(slaCompensateVO.getDuration());
                aMsg.setAccountUuid(slaCompensateVO.getAccountUuid());
                aMsg.setSession(msg.getSession());
                String gstr = RESTApiDecoder.dumpWithSession(aMsg);
                RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, RestAPIResponse.class);

                if (rsp.getState().equals(RestAPIState.Done.toString())) {
                    try {
                        APISalTunnelReply reply = (APISalTunnelReply) RESTApiDecoder.loads(rsp.getResult());
                        if(reply!=null && reply.getInventory()!=null){
                            slaCompensateVO.setTimeEnd(reply.getInventory().getExpireDate());
                            slaCompensateVO.setTimeStart(new Timestamp(reply.getInventory().getExpireDate().getTime()-slaCompensateVO.getDuration()*24*60*60*1000));
                        }
                    }catch (Exception e){
                        throw new IllegalArgumentException(e);
                    }
                } else {
                    throw new IllegalArgumentException("the network is not fine ,try for a moment");
                }
            } else if (slaCompensateVO.getProductType().equals(ProductType.PORT)) {
                APISLAInterfaceMsg aMsg = new APISLAInterfaceMsg();
                aMsg.setUuid(slaCompensateVO.getProductUuid());
                aMsg.setDuration(slaCompensateVO.getDuration());
                aMsg.setSession(msg.getSession());
                String gstr = RESTApiDecoder.dumpWithSession(aMsg);
                RestAPIResponse rsp = restf.syncJsonPost(caller.getProductUrl(), gstr, RestAPIResponse.class);

                if (rsp.getState().equals(RestAPIState.Done.toString())) {
                    try {
                        APISLAInterfaceReply reply = (APISLAInterfaceReply) RESTApiDecoder.loads(rsp.getResult());
                        if(reply!=null && reply.getInventory()!=null){
                            slaCompensateVO.setTimeEnd(reply.getInventory().getExpireDate());
                            slaCompensateVO.setTimeStart(new Timestamp(reply.getInventory().getExpireDate().getTime()-slaCompensateVO.getDuration()*24*60*60*1000));
                        }
                    }catch (Exception e){
                        throw new IllegalArgumentException(e);
                    }
                } else {
                    throw new IllegalArgumentException("the network is not fine ,try for a moment");
                }
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

