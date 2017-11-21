package com.syscxp.tunnel.solution;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.solution.*;
import com.syscxp.tunnel.tunnel.TunnelBase;
import com.syscxp.tunnel.tunnel.TunnelManagerImpl;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by wangwg on 2017/11/20.
 */

public class SolutionManagerImpl extends AbstractService implements SolutionManager , ApiMessageInterceptor{

    private static final CLogger logger = Utils.getLogger(TunnelManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;

    @Override
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        TunnelBase base = new TunnelBase();
        base.handleMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if(msg instanceof APICreateSolutionMsg){
            hand((APICreateSolutionMsg) msg);
        } else if(msg instanceof APICreateSolutionInterfaceMsg){
            hand((APICreateSolutionInterfaceMsg) msg);
        }else if(msg instanceof APICreateSolutionTunnelMsg){
            hand((APICreateSolutionTunnelMsg) msg);
        }else if(msg instanceof APICreateSolutionVpnMsg){
            hand((APICreateSolutionVpnMsg) msg);
        }else if(msg instanceof APIDeleteSolutionMsg){
            hand((APIDeleteSolutionMsg) msg);
        }else if(msg instanceof APIDeleteSolutionInterfaceMsg){
            hand((APIDeleteSolutionInterfaceMsg) msg);
        }else if(msg instanceof APIDeleteSolutionTunnelMsg){
            hand((APIDeleteSolutionTunnelMsg) msg);
        }else if(msg instanceof APIDeleteSolutionVpnMsg){
            hand((APIDeleteSolutionVpnMsg) msg);
        }else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void hand(APIDeleteSolutionVpnMsg msg) {
        UpdateQuery.New(SolutionVpnVO.class).condAnd(SolutionVpnVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        APIDeleteSolutionVpnEvent event = new APIDeleteSolutionVpnEvent(msg.getId());
        bus.publish(event);
    }

    private void hand(APIDeleteSolutionTunnelMsg msg) {
        UpdateQuery.New(SolutionTunnelVO.class).condAnd(SolutionTunnelVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        APIDeleteSolutionTunnelEvent event = new APIDeleteSolutionTunnelEvent(msg.getId());
        bus.publish(event);
    }

    private void hand(APIDeleteSolutionInterfaceMsg msg) {
        UpdateQuery.New(SolutionInterfaceVO.class).condAnd(SolutionInterfaceVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        APIDeleteSolutionInterfaceEvent event = new APIDeleteSolutionInterfaceEvent(msg.getId());
        bus.publish(event);
    }

    @Transactional
    private void hand(APIDeleteSolutionMsg msg) {

        UpdateQuery.New(SolutionInterfaceVO.class).condAnd(SolutionInterfaceVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();
        UpdateQuery.New(SolutionTunnelVO.class).condAnd(SolutionTunnelVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();
        UpdateQuery.New(SolutionVpnVO.class).condAnd(SolutionVpnVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        UpdateQuery.New(SolutionVO.class).condAnd(SolutionVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        APIDeleteSolutionEvent event = new APIDeleteSolutionEvent(msg.getId());
        bus.publish(event);
    }

    private void hand(APICreateSolutionVpnMsg msg) {

        SolutionVpnVO vo = new SolutionVpnVO();
        vo.setUuid(Platform.getUuid());
        vo.setCost(msg.getCost());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
        }

        vo.setBandwidth(msg.getBandwidth());
        vo.setEndpointName(msg.getEndpointName());
        vo.setZoneName(msg.getZoneName());

        APICreateSolutionVpnEvent event = new APICreateSolutionVpnEvent(msg.getId());
        event.setInventory(SolutionVpnInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);

    }

    private void hand(APICreateSolutionTunnelMsg msg) {
        SolutionTunnelVO vo = new SolutionTunnelVO();
        vo.setUuid(Platform.getUuid());
        vo.setCost(msg.getCost());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
        }

        vo.setBandwidth(msg.getBandwidth());
        vo.setEndpointNameA(msg.getEndpointNameA());
        vo.setEndpointNameZ(msg.getEndpointNameZ());

        APICreateSolutionTunnelEvent event = new APICreateSolutionTunnelEvent(msg.getId());
        event.setInventory(SolutionTunnelInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);

    }

    private void hand(APICreateSolutionInterfaceMsg msg) {
        SolutionInterfaceVO vo = new SolutionInterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setCost(msg.getCost());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
        }

        vo.setEndpointName(msg.getEndpointName());
        vo.setPortOfferingName(msg.getPortOfferingName());

        APICreateSolutionInterfaceEvent event = new APICreateSolutionInterfaceEvent(msg.getId());
        event.setInventory(SolutionInterfaceInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);

    }

    private void hand(APICreateSolutionMsg msg) {
        SolutionVO vo = new SolutionVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setDescription(msg.getDescription());

        APICreateSolutionEvent event = new APICreateSolutionEvent(msg.getId());
        event.setInventory(SolutionInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }


    @Override
    public String getId() {
        return bus.makeLocalServiceId(SolutionConstant.SERVICE_ID);
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
        return null;
    }


}
