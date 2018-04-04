package com.syscxp.idc.solution;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.idc.SolutionConstant;
import com.syscxp.header.idc.solution.*;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.idc.quota.SolutionQuotaOperator;
import com.syscxp.utils.*;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Create by DCY on 2018/4/4
 */
public class SolutionManagerImpl extends AbstractService implements SolutionManager , ApiMessageInterceptor, ReportQuotaExtensionPoint {
    private static final CLogger logger = Utils.getLogger(SolutionManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;

    @Override
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APICreateSolutionMsg){
            handle((APICreateSolutionMsg) msg);
        } else if (msg instanceof APIUpdateSolutionMsg){
            handle((APIUpdateSolutionMsg) msg);
        } else if (msg instanceof APIDeleteSolutionMsg){
            handle((APIDeleteSolutionMsg) msg);
        } else if (msg instanceof APICreateSolutionInterfaceMsg){
            handle((APICreateSolutionInterfaceMsg) msg);
        } else if (msg instanceof APIDeleteSolutionInterfaceMsg){
            handle((APIDeleteSolutionInterfaceMsg) msg);
        } else if (msg instanceof APICreateSolutionTunnelMsg){
            handle((APICreateSolutionTunnelMsg) msg);
        } else if (msg instanceof APIUpdateSolutionTunnelMsg){
            handle((APIUpdateSolutionTunnelMsg) msg);
        } else if (msg instanceof APIDeleteSolutionTunnelMsg){
            handle((APIDeleteSolutionTunnelMsg) msg);
        } else if (msg instanceof APICreateSolutionVpnMsg){
            handle((APICreateSolutionVpnMsg) msg);
        } else if (msg instanceof APIUpdateSolutionVpnMsg){
            handle((APIUpdateSolutionVpnMsg) msg);
        } else if (msg instanceof APIDeleteSolutionVpnMsg){
            handle((APIDeleteSolutionVpnMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    /**
     * 创建方案
     * */
    private void handle(APICreateSolutionMsg msg) {
        SolutionVO vo = new SolutionVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setDescription(msg.getDescription());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setShareAccountUuid(msg.getShareAccountUuid());

        APICreateSolutionEvent event = new APICreateSolutionEvent(msg.getId());
        event.setInventory(SolutionInventory.valueOf(dbf.persistAndRefresh(vo)));
        bus.publish(event);
    }

    /**
     * 修改方案--重命名
     * */
    private void handle(APIUpdateSolutionMsg msg) {

        SolutionVO vo = dbf.findByUuid(msg.getUuid(), SolutionVO.class);
        if(msg.getName() != null){
            vo.setName(msg.getName());
        }

        if(msg.getDescription() != null){
            vo.setDescription(msg.getDescription());
        }

        APIUpdateSolutionEvent event = new APIUpdateSolutionEvent(msg.getId());
        event.setInventory(SolutionInventory.valueOf(dbf.updateAndRefresh(vo)));
        bus.publish(event);
    }

    /**
     * 删除方案
     * */
    @Transactional
    private void handle(APIDeleteSolutionMsg msg) {

        UpdateQuery.New(SolutionVpnVO.class).condAnd(SolutionVpnVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        UpdateQuery.New(SolutionTunnelVO.class).condAnd(SolutionTunnelVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        UpdateQuery.New(SolutionInterfaceVO.class).condAnd(SolutionInterfaceVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        UpdateQuery.New(SolutionVO.class).condAnd(SolutionVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        APIDeleteSolutionEvent event = new APIDeleteSolutionEvent(msg.getId());
        bus.publish(event);
    }

    /**
     * 获取方案总价和折扣总价
     * */



    /**
     * 添加方案--物理接口
     * */
    private void handle(APICreateSolutionInterfaceMsg msg) {

        SolutionInterfaceVO vo = new SolutionInterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setName(msg.getName());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setPortOfferingUuid(msg.getPortOfferingUuid());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setCost(msg.getCost());
        vo.setDiscount(msg.getDiscount());
        vo.setShareDiscount(msg.getShareDiscount());

        vo = dbf.persistAndRefresh(vo);

        APICreateSolutionInterfaceEvent event = new APICreateSolutionInterfaceEvent(msg.getId());
        event.setInterfaceInventory(SolutionInterfaceInventory.valueOf(vo));
        bus.publish(event);
    }

    /**
     * 删除方案--物理接口
     * */
    private void handle(APIDeleteSolutionInterfaceMsg msg) {
        SolutionInterfaceVO vo = dbf.findByUuid(msg.getUuid(), SolutionInterfaceVO.class);
        dbf.remove(vo);

        APIDeleteSolutionInterfaceEvent event = new APIDeleteSolutionInterfaceEvent(msg.getId());
        bus.publish(event);
    }

    /**
     * 添加方案--云专线
     * */
    @Transactional
    private void handle(APICreateSolutionTunnelMsg msg) {
        SolutionBase solutionBase = new SolutionBase();

        //若新购，创建物理接口
        boolean isIfaceANew = false;
        if(msg.getInterfaceAUuid() == null){
            isIfaceANew = true;
        }
        boolean isIfaceZNew = false;
        if(msg.getInterfaceZUuid() == null){
            isIfaceZNew = true;
        }
        SolutionInterfaceVO interfaceVOA;
        SolutionInterfaceVO interfaceVOZ;
        if (!isIfaceANew){
            interfaceVOA = dbf.findByUuid(msg.getInterfaceAUuid(), SolutionInterfaceVO.class);
        } else {
            interfaceVOA = solutionBase.createInterfaceByTunnel(msg.getEndpointUuidA(), msg.getEndpointNameA(), msg);
        }
        if (!isIfaceZNew){
            interfaceVOZ = dbf.findByUuid(msg.getInterfaceZUuid(), SolutionInterfaceVO.class);
        } else {
            interfaceVOZ = solutionBase.createInterfaceByTunnel(msg.getEndpointUuidZ(), msg.getEndpointNameZ(), msg);
        }

        SolutionTunnelVO vo = new SolutionTunnelVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setInterfaceUuidA(interfaceVOA.getUuid());
        vo.setInterfaceUuidZ(interfaceVOZ.getUuid());
        vo.setEndpointUuidA(msg.getEndpointUuidA());
        vo.setEndpointUuidZ(msg.getEndpointUuidZ());
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setInnerEndpointUuid(msg.getInnerEndpointUuid());
        vo.setType(msg.getTunnelType());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setCost(msg.getCost());
        vo.setDiscount(msg.getDiscount());
        vo.setShareDiscount(msg.getShareDiscount());

        dbf.getEntityManager().persist(vo);

        APICreateSolutionTunnelEvent event = new APICreateSolutionTunnelEvent(msg.getId());
        event.setTunnelInventory(SolutionTunnelInventory.valueOf(vo));
        bus.publish(event);

    }

    /**
     * 修改云专线带宽--重新计算价格
     * */
    private void handle(APIUpdateSolutionTunnelMsg msg) {
        SolutionTunnelVO vo = dbf.findByUuid(msg.getUuid(), SolutionTunnelVO.class);

        if(msg.getBandwidthOfferingUuid() != null){
            vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        }

        vo.setCost(msg.getCost());
        vo.setDiscount(msg.getDiscount());
        vo.setShareDiscount(msg.getShareDiscount());
        vo = dbf.updateAndRefresh(vo);

        APIUpdateSolutionTunnelEvent event = new APIUpdateSolutionTunnelEvent(msg.getId());
        event.setTunnelInventory(SolutionTunnelInventory.valueOf(vo));
        bus.publish(event);
    }

    /**
     * 删除方案--云专线
     * */
    private void handle(APIDeleteSolutionTunnelMsg msg) {
        SolutionTunnelVO vo = dbf.findByUuid(msg.getUuid(), SolutionTunnelVO.class);

        dbf.remove(vo);

        APIDeleteSolutionTunnelEvent event = new APIDeleteSolutionTunnelEvent(msg.getId());
        bus.publish(event);
    }

    /**
     * 添加方案--VPN
     * */
    private void handle(APICreateSolutionVpnMsg msg) {

        SolutionVpnVO vo = new SolutionVpnVO();
        vo.setUuid(Platform.getUuid());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setName(msg.getName());
        vo.setSolutionTunnelUuid(msg.getSolutionTunnelUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setCost(msg.getCost());
        vo.setDiscount(msg.getDiscount());
        vo.setShareDiscount(msg.getShareDiscount());

        vo = dbf.persistAndRefresh(vo);

        APICreateSolutionVpnEvent event = new APICreateSolutionVpnEvent(msg.getId());
        event.setVpnInventory(SolutionVpnInventory.valueOf(vo));
        bus.publish(event);

    }

    /**
     * 修改VPN带宽--重新计算价格
     * */
    private void handle(APIUpdateSolutionVpnMsg msg) {

        SolutionVpnVO vo = dbf.findByUuid(msg.getUuid(), SolutionVpnVO.class);

        if(msg.getBandwidthOfferingUuid() != null){
            vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        }
        vo.setCost(msg.getCost());
        vo.setDiscount(msg.getDiscount());
        vo.setShareDiscount(msg.getShareDiscount());
        vo = dbf.updateAndRefresh(vo);

        APIUpdateSolutionVpnEvent event = new APIUpdateSolutionVpnEvent(msg.getId());
        event.setVpnInventory(SolutionVpnInventory.valueOf(vo));
        bus.publish(event);

    }

    /**
     * 删除方案VPN
     * */
    private void handle(APIDeleteSolutionVpnMsg msg) {
        SolutionVpnVO vo = dbf.findByUuid(msg.getUuid(), SolutionVpnVO.class);

        dbf.remove(vo);

        APIDeleteSolutionVpnEvent event = new APIDeleteSolutionVpnEvent(msg.getId());
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
        if (msg instanceof APICreateSolutionMsg) {
        }
        return msg;
    }

    @Override
    public List<Quota> reportQuota() {

        SolutionQuotaOperator quotaOperator = new SolutionQuotaOperator();
        // interface quota
        Quota quota = new Quota();
        quota.setOperator(quotaOperator);
        quota.addMessageNeedValidation(APICreateSolutionMsg.class);
        quota.addMessageNeedValidation(APICreateSolutionInterfaceMsg.class);
        quota.addMessageNeedValidation(APICreateSolutionTunnelMsg.class);
        quota.addMessageNeedValidation(APICreateSolutionVpnMsg.class);

        Quota.QuotaPair p = new Quota.QuotaPair();
        p.setName(SolutionConstant.QUOTA_SOLUTION_NUM);
        p.setValue(50);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(SolutionConstant.QUOTA_SOLUTION_INTERFACE_NUM);
        p.setValue(100);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(SolutionConstant.QUOTA_SOLUTION_TUNNEL_NUM);
        p.setValue(50);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(SolutionConstant.QUOTA_SOLUTION_VPN_NUM);
        p.setValue(100);
        quota.addPair(p);

        return CollectionDSL.list(quota);

    }
}
