package com.syscxp.tunnel.solution;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SQL;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.billing.*;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.message.Message;
import com.syscxp.header.quota.Quota;
import com.syscxp.header.quota.ReportQuotaExtensionPoint;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.tunnel.TunnelConstant;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.endpoint.InnerConnectedEndpointVO;
import com.syscxp.header.tunnel.solution.*;
import com.syscxp.tunnel.quota.SolutionQuotaOperator;
import com.syscxp.tunnel.tunnel.TunnelBillingBase;
import com.syscxp.tunnel.tunnel.TunnelControllerBase;
import com.syscxp.tunnel.tunnel.BillingRESTCaller;
import com.syscxp.utils.CollectionDSL;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.syscxp.utils.CollectionDSL.list;


/**
 * Created by wangwg on 2017/11/20.
 */

public class SolutionManagerImpl extends AbstractService implements SolutionManager , ApiMessageInterceptor, ReportQuotaExtensionPoint{

    private static final CLogger logger = Utils.getLogger(SolutionManagerImpl.class);

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
        TunnelControllerBase base = new TunnelControllerBase();
        base.handleMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if(msg instanceof APICreateSolutionMsg){
            handle((APICreateSolutionMsg) msg);
        } else if(msg instanceof APICreateSolutionInterfaceMsg){
            handle((APICreateSolutionInterfaceMsg) msg);
        }else if(msg instanceof APICreateSolutionTunnelMsg){
            handle((APICreateSolutionTunnelMsg) msg);
        }else if(msg instanceof APICreateSolutionVpnMsg){
            handle((APICreateSolutionVpnMsg) msg);
        }else if(msg instanceof APIDeleteSolutionMsg){
            handle((APIDeleteSolutionMsg) msg);
        }else if(msg instanceof APIDeleteSolutionInterfaceMsg){
            handle((APIDeleteSolutionInterfaceMsg) msg);
        }else if(msg instanceof APIDeleteSolutionTunnelMsg){
            handle((APIDeleteSolutionTunnelMsg) msg);
        }else if(msg instanceof APIDeleteSolutionVpnMsg){
            handle((APIDeleteSolutionVpnMsg) msg);
        }else if(msg instanceof APIUpdateSolutionMsg){
            handle((APIUpdateSolutionMsg) msg);
        }else if(msg instanceof APIUpdateSolutionTunnelMsg){
            handle((APIUpdateSolutionTunnelMsg) msg);
        }else if(msg instanceof APIUpdateSolutionVpnMsg){
            handle((APIUpdateSolutionVpnMsg) msg);
        }else if(msg instanceof APIRecountInterfacePriceMsg){
            handle((APIRecountInterfacePriceMsg) msg);
        }else if(msg instanceof APIRecountTunnelPriceMsg){
            handle((APIRecountTunnelPriceMsg) msg);
        }else if(msg instanceof APIModifyBandwidthTunnelPriceMsg){
            handle((APIModifyBandwidthTunnelPriceMsg) msg);
        }else if(msg instanceof APIRecountVPNPriceMsg){
            handle((APIRecountVPNPriceMsg) msg);
        } else if(msg instanceof APIGetVPNPriceMsg){
            handle((APIGetVPNPriceMsg) msg);
        } else if(msg instanceof APIGetTunnelPriceMsg){
            handle((APIGetTunnelPriceMsg) msg);
        } else if(msg instanceof APICreateShareSolutionMsg){
            handle((APICreateShareSolutionMsg) msg);
        } else if(msg instanceof APIDeleteShareSolutionMsg){
            handle((APIDeleteShareSolutionMsg) msg);
        }else if(msg instanceof APIGetShareSolutionMsg){
            handle((APIGetShareSolutionMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetShareSolutionMsg msg) {
        APIGetShareSolutionReply reply = new APIGetShareSolutionReply();
        List<SolutionInventory> solutionInventories = new ArrayList<>();

        SimpleQuery<ShareSolutionVO> query = dbf.createQuery(ShareSolutionVO.class);
        query.add(ShareSolutionVO_.accountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
        List<ShareSolutionVO> shareSolutionVOS = query.list();

        for(ShareSolutionVO vo : shareSolutionVOS){
            solutionInventories.add(SolutionInventory.valueOf(dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class)));
        }

        reply.setSolutionInventories(solutionInventories);
        bus.reply(msg, reply);
    }

    private void handle(APIDeleteShareSolutionMsg msg) {
        ShareSolutionVO vo = dbf.findByUuid(msg.getUuid(), ShareSolutionVO.class);
        if( !vo.getOwnerAccountUuid().equals(msg.getSession().getAccountUuid())){
            throw  new RuntimeException(String.format("The solution[name: %s] is not yours", dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class).getName()));
        }
        dbf.remove(vo);

        SimpleQuery<ShareSolutionVO> simpleQuery = dbf.createQuery(ShareSolutionVO.class);
        simpleQuery.add(ShareSolutionVO_.solutionUuid, SimpleQuery.Op.EQ, vo.getSolutionUuid());

        if(simpleQuery.count().longValue() == 0){
            SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);
            solutionVO.setShare(false);
            dbf.updateAndRefresh(solutionVO);
        }

        APIDeleteShareSolutionEvent event = new APIDeleteShareSolutionEvent(msg.getId());
        bus.publish(event);
    }


    private void handle(APICreateShareSolutionMsg msg) {
        List<ShareSolutionVO> shareSolutionVOS = new ArrayList<>();
        List<String> accountUuids = msg.getAccountUuids();

        for(String accountUuid : accountUuids){

            SimpleQuery<ShareSolutionVO> simpleQuery = dbf.createQuery(ShareSolutionVO.class);
            simpleQuery.add(ShareSolutionVO_.solutionUuid, SimpleQuery.Op.EQ, msg.getSolutionUuid());
            simpleQuery.add(ShareSolutionVO_.accountUuid, SimpleQuery.Op.EQ, accountUuid);
            simpleQuery.add(ShareSolutionVO_.ownerAccountUuid, SimpleQuery.Op.EQ, msg.getSession().getAccountUuid());
            if(simpleQuery.count().longValue() != 0){
                throw new RuntimeException(String.format("The slotion[name: %s] was Shared by the user[uuid: %s]",
                        dbf.findByUuid(msg.getSolutionUuid(), SolutionVO.class).getName(), accountUuid));
            }

            ShareSolutionVO vo = new ShareSolutionVO();
            vo.setUuid(Platform.getUuid());
            vo.setAccountUuid(accountUuid);
            vo.setOwnerAccountUuid(msg.getSession().getAccountUuid());
            vo.setSolutionUuid(msg.getSolutionUuid());

            shareSolutionVOS.add(vo);
        }

        dbf.persistCollection(shareSolutionVOS);

        SolutionVO solutionVO = dbf.findByUuid(msg.getSolutionUuid(), SolutionVO.class);
        solutionVO.setShare(true);
        dbf.updateAndRefresh(solutionVO);

        APICreateShareSolutionEvent event = new APICreateShareSolutionEvent(msg.getId());
        bus.publish(event);
    }

    /**
     * 判断云专线的物理接口是否为共享端口
     */
    public boolean isShareForInterface(String interfaceUuid){
        boolean isShare = false;
        if(interfaceUuid == null){
            isShare =  true;
        }else{
            SolutionInterfaceVO solutionInterfaceVO = dbf.findByUuid(interfaceUuid,SolutionInterfaceVO.class);
            if(solutionInterfaceVO.getPortOfferingUuid().equals("SHARE")){
                isShare =  true;
            }
        }

        return isShare;
    }

    private void handle(APIGetTunnelPriceMsg msg) {
        APIGetProductPriceReply priceReply = getTunnelPrice(msg.getProductChargeModel(), msg.getDuration(),
                msg.getSession().getAccountUuid(),
                msg.getEndpointAUuid(),
                msg.getEndpointZUuid(),
                msg.getInnerConnectedEndpointUuid(),
                msg.getBandwidthOfferingUuid(),
                isShareForInterface(msg.getInterfaceAUuid()),
                isShareForInterface(msg.getInterfaceZUuid()));

        APIGetTunnelPriceReply reply = new APIGetTunnelPriceReply();
        reply.setPrice(priceReply.getOriginalPrice());
        bus.reply(msg, reply);
    }

    private void handle(APIGetVPNPriceMsg msg) {
        APIGetProductPriceReply reply = getVPNPrice(msg);

        APIGetVPNPriceReply priceReply = new APIGetVPNPriceReply();
        priceReply.setPrice(reply.getOriginalPrice());
        bus.reply(msg, priceReply);
    }

    @Transactional
    private void handle(APIRecountVPNPriceMsg msg) {
        SolutionVpnVO vo = dbf.findByUuid(msg.getUuid(), SolutionVpnVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);

        APIGetProductPriceReply reply = getVPNPrice(vo, solutionVO.getAccountUuid());
        if(reply.getOriginalPrice().compareTo(vo.getCost()) != 0){
            solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()).add(reply.getOriginalPrice()));
            solutionVO = dbf.getEntityManager().merge(solutionVO);

            vo.setCost(reply.getOriginalPrice());
            dbf.getEntityManager().merge(vo);
        }

        APIRecountVPNPriceReply priceReply = new APIRecountVPNPriceReply();
        priceReply.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        priceReply.setVpnInventory(SolutionVpnInventory.valueOf(vo));
        bus.reply(msg, priceReply);

    }

    private void handle(APIModifyBandwidthTunnelPriceMsg msg) {
        SolutionTunnelVO vo = dbf.findByUuid(msg.getUuid(), SolutionTunnelVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());

        APIGetProductPriceReply reply = getTunnelPrice(vo, solutionVO.getAccountUuid());

        APIModifyBandwidthTunnelPriceReply priceReply = new APIModifyBandwidthTunnelPriceReply();
        priceReply.setPrice(reply.getOriginalPrice());
        bus.reply(msg, priceReply);
    }


    @Transactional
    private void handle(APIRecountTunnelPriceMsg msg) {
        SolutionTunnelVO vo = dbf.findByUuid(msg.getUuid(), SolutionTunnelVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);

        APIGetProductPriceReply reply = getTunnelPrice(vo, solutionVO.getAccountUuid());
        if(reply.getOriginalPrice().compareTo(vo.getCost()) != 0){
            solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()).add(reply.getOriginalPrice()));
            solutionVO = dbf.getEntityManager().merge(solutionVO);

            vo.setCost(reply.getOriginalPrice());
            dbf.getEntityManager().merge(vo);
        }

        APIRecountTunnelPriceReply priceReply = new APIRecountTunnelPriceReply();
        priceReply.setTunnelInventory(SolutionTunnelInventory.valueOf(vo));
        priceReply.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.reply(msg,priceReply);
    }

    @Transactional
    private void handle(APIRecountInterfacePriceMsg msg) {
        SolutionInterfaceVO vo = dbf.findByUuid(msg.getUuid(), SolutionInterfaceVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);

        APIGetProductPriceReply reply = getInterfacePrice(vo,solutionVO.getAccountUuid());
        if(reply.getOriginalPrice().compareTo(vo.getCost()) != 0){
            solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()).add(reply.getOriginalPrice()));
            vo.setCost(reply.getOriginalPrice());
            dbf.getEntityManager().merge(vo);
            solutionVO = dbf.getEntityManager().merge(solutionVO);
        }

        APIRecountInterfacePriceReply priceReply = new APIRecountInterfacePriceReply();
        priceReply.setInterfaceInventory(SolutionInterfaceInventory.valueOf(vo));
        priceReply.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.reply(msg, priceReply);

    }



    @Transactional
    private void handle(APIUpdateSolutionVpnMsg msg) {

        SolutionVpnVO vo = dbf.findByUuid(msg.getUuid(), SolutionVpnVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);

        solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()).add(msg.getCost()));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setCost(msg.getCost());
        dbf.getEntityManager().merge(vo);

        APIUpdateSolutionVpnEvent event = new APIUpdateSolutionVpnEvent(msg.getId());
        event.setVpnInventory(SolutionVpnInventory.valueOf(vo));
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);

    }

    @Transactional
    private void handle(APIUpdateSolutionTunnelMsg msg) {

        SolutionTunnelVO vo = dbf.findByUuid(msg.getUuid(), SolutionTunnelVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);

        solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()).add(msg.getCost()));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setCost(msg.getCost());
        dbf.getEntityManager().merge(vo);

        APIUpdateSolutionTunnelEvent event = new APIUpdateSolutionTunnelEvent(msg.getId());
        event.setTunnelInventory(SolutionTunnelInventory.valueOf(vo));
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);
    }

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

    @Transactional
    private void handle(APIDeleteSolutionVpnMsg msg) {
        SolutionVpnVO vo = dbf.findByUuid(msg.getUuid(), SolutionVpnVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);
        solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()));
        dbf.getEntityManager().remove(dbf.getEntityManager().merge(vo));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        APIDeleteSolutionVpnEvent event = new APIDeleteSolutionVpnEvent(msg.getId());
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);
    }

    @Transactional
    private void handle(APIDeleteSolutionTunnelMsg msg) {
        SolutionTunnelVO vo = dbf.findByUuid(msg.getUuid(), SolutionTunnelVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);

        SimpleQuery<SolutionVpnVO> query = dbf.createQuery(SolutionVpnVO.class);
        query.add(SolutionVpnVO_.solutionTunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<SolutionVpnVO> solutionVpnVOS = query.list();

        BigDecimal vpnPrice = new BigDecimal(0);
        for(SolutionVpnVO solutionVpnVO :solutionVpnVOS){
            vpnPrice = vpnPrice.add(solutionVpnVO.getCost());
        }

        solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()).subtract(vpnPrice));

        dbf.removeCollection(solutionVpnVOS,SolutionVpnVO.class);
        dbf.getEntityManager().remove(dbf.getEntityManager().merge(vo));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        APIDeleteSolutionTunnelEvent event = new APIDeleteSolutionTunnelEvent(msg.getId());
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);
    }
    @Transactional
    private void handle(APIDeleteSolutionInterfaceMsg msg) {
        SolutionInterfaceVO vo = dbf.findByUuid(msg.getUuid(), SolutionInterfaceVO.class);
        SolutionVO solutionVO = dbf.findByUuid(vo.getSolutionUuid(), SolutionVO.class);
        solutionVO.setTotalCost(solutionVO.getTotalCost().subtract(vo.getCost()));
        dbf.getEntityManager().remove(dbf.getEntityManager().merge(vo));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        APIDeleteSolutionInterfaceEvent event = new APIDeleteSolutionInterfaceEvent(msg.getId());
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);
    }

    @Transactional
    private void handle(APIDeleteSolutionMsg msg) {

        UpdateQuery.New(SolutionInterfaceVO.class).condAnd(SolutionInterfaceVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();
        UpdateQuery.New(SolutionVpnVO.class).condAnd(SolutionVpnVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();
        UpdateQuery.New(SolutionTunnelVO.class).condAnd(SolutionTunnelVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        UpdateQuery.New(ShareSolutionVO.class).condAnd(ShareSolutionVO_.solutionUuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();

        UpdateQuery.New(SolutionVO.class).condAnd(SolutionVO_.uuid,
                SimpleQuery.Op.EQ, msg.getUuid()).delete();


        APIDeleteSolutionEvent event = new APIDeleteSolutionEvent(msg.getId());
        bus.publish(event);
    }

    @Transactional
    private void handle(APICreateSolutionVpnMsg msg) {

        SolutionVpnVO vo = new SolutionVpnVO();
        vo.setUuid(Platform.getUuid());
        vo.setCost(msg.getCost());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setSolutionTunnelUuid(msg.getSolutionTunnelUuid());
        vo.setName(msg.getName());

        dbf.getEntityManager().persist(vo);

        SolutionVO solutionVO = dbf.findByUuid(msg.getSolutionUuid(), SolutionVO.class);
        solutionVO.setTotalCost(solutionVO.getTotalCost().add(vo.getCost()));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        APICreateSolutionVpnEvent event = new APICreateSolutionVpnEvent(msg.getId());
        event.setVpnInventory(SolutionVpnInventory.valueOf(vo));
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);

    }

    @Transactional
    private void handle(APICreateSolutionTunnelMsg msg) {
        SolutionTunnelVO vo = new SolutionTunnelVO();
        vo.setUuid(Platform.getUuid());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setBandwidthOfferingUuid(msg.getBandwidthOfferingUuid());
        vo.setEndpointUuidA(msg.getEndpointUuidA());
        vo.setEndpointUuidZ(msg.getEndpointUuidZ());
        vo.setName(msg.getName());

        if(msg.getInnerConnectedEndpointUuid() != null){
            vo.setInnerConnectedEndpointUuid(msg.getInnerConnectedEndpointUuid());
        }

        BigDecimal tunnelCost = msg.getCost();
        //创建物理接口
        if(msg.getPortOfferingUuidA() != null){
            SolutionInterfaceVO faceA = createSolutionInterface(msg, msg.getEndpointUuidA(), msg.getPortOfferingUuidA());
            tunnelCost = tunnelCost.subtract(faceA.getCost());
        }
        if(msg.getPortOfferingUuidZ() != null){
            SolutionInterfaceVO faceb = createSolutionInterface(msg, msg.getEndpointUuidZ(), msg.getPortOfferingUuidZ());
            tunnelCost = tunnelCost.subtract(faceb.getCost());
        }

        vo.setShareA(msg.isShareA());
        vo.setShareZ(msg.isShareZ());

        vo.setCost(tunnelCost);
        dbf.getEntityManager().persist(vo);

        SolutionVO solutionVO = dbf.findByUuid(msg.getSolutionUuid(), SolutionVO.class);
        solutionVO.setTotalCost(solutionVO.getTotalCost().add(msg.getCost()));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        APICreateSolutionTunnelEvent event = new APICreateSolutionTunnelEvent(msg.getId());
        event.setTunnelInventory(SolutionTunnelInventory.valueOf(vo));
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);

    }

    @Transactional
    private void handle(APICreateSolutionInterfaceMsg msg) {
        SolutionInterfaceVO vo = new SolutionInterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setCost(msg.getCost());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setPortOfferingUuid(msg.getPortOfferingUuid());
        vo.setName(msg.getName());

        dbf.getEntityManager().persist(vo);

        SolutionVO solutionVO = dbf.findByUuid(msg.getSolutionUuid(), SolutionVO.class);
        System.out.println(vo.getCost());
        solutionVO.setTotalCost(solutionVO.getTotalCost().add(vo.getCost()));
        solutionVO = dbf.getEntityManager().merge(solutionVO);

        APICreateSolutionInterfaceEvent event = new APICreateSolutionInterfaceEvent(msg.getId());
        event.setInterfaceInventory(SolutionInterfaceInventory.valueOf(vo));
        event.setSolutionInventory(SolutionInventory.valueOf(solutionVO));
        bus.publish(event);

    }

    private void handle(APICreateSolutionMsg msg) {
        SolutionVO vo = new SolutionVO();
        vo.setUuid(Platform.getUuid());
        vo.setName(msg.getName());
        vo.setDescription(msg.getDescription());
        vo.setAccountUuid(msg.getAccountUuid());
        vo.setTotalCost(BigDecimal.ZERO);

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
        return msg;
    }

    /*创建物理接口*/
    private SolutionInterfaceVO createSolutionInterface(APICreateSolutionTunnelMsg msg, String endpointUuid, String portOfferingUuid) {
        SolutionInterfaceVO vo = new SolutionInterfaceVO();
        vo.setUuid(Platform.getUuid());
        vo.setDuration(msg.getDuration());
        vo.setProductChargeModel(msg.getProductChargeModel());
        vo.setSolutionUuid(msg.getSolutionUuid());
        vo.setEndpointUuid(endpointUuid);
        vo.setPortOfferingUuid(portOfferingUuid);
        APIGetProductPriceReply reply = getInterfacePrice(vo, msg.getSession().getAccountUuid());
        vo.setCost(reply.getOriginalPrice());


        dbf.getEntityManager().persist(vo);

        return vo;
    }

    /* 获取接口的价格*/
    private APIGetProductPriceReply getInterfacePrice(SolutionInterfaceVO vo, String accountUuid) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(vo.getProductChargeModel());
        pmsg.setDuration(vo.getDuration());
        pmsg.setAccountUuid(accountUuid);
        pmsg.setUnits(new TunnelBillingBase().getInterfacePriceUnit(vo.getPortOfferingUuid()));
        APIGetProductPriceReply reply = new BillingRESTCaller().syncJsonPost(pmsg);
        return reply;
    }
    /*获取VPN的价格*/
    private APIGetProductPriceReply getVPNPrice(APIGetVPNPriceMsg msg) {
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(msg.getProductChargeModel());
        priceMsg.setDuration(msg.getDuration());
        priceMsg.setAccountUuid(msg.getSession().getAccountUuid());
        priceMsg.setUnits(generateUnits(msg.getBandwidthOfferingUuid(),msg.getEndpointUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        return reply;
    }

    private APIGetProductPriceReply getVPNPrice(SolutionVpnVO vo, String accountUuid) {
        APIGetProductPriceMsg priceMsg = new APIGetProductPriceMsg();
        priceMsg.setProductChargeModel(vo.getProductChargeModel());
        priceMsg.setDuration(vo.getDuration());
        priceMsg.setAccountUuid(accountUuid);
        priceMsg.setUnits(generateUnits(vo.getBandwidthOfferingUuid(), vo.getEndpointUuid()));

        APIGetProductPriceReply reply = createOrder(priceMsg);
        return reply;
    }


    private List<ProductPriceUnit> generateUnits(String bandwidth, String areaCode) {

        return CollectionDSL.list(ProductPriceUnitFactory.createVpnPriceUnit(bandwidth, areaCode));
    }
    /*获取云专线的价格*/
    private APIGetProductPriceReply getTunnelPrice(SolutionTunnelVO vo, String accountUuid) {

               return getTunnelPrice(vo.getProductChargeModel(),
                    vo.getDuration(),
                    accountUuid,
                    vo.getEndpointVOA().getUuid(),
                    vo.getEndpointVOZ().getUuid(),
                    vo.getInnerConnectedEndpointUuid(),
                    vo.getBandwidthOfferingUuid(),
                    vo.isShareA(), vo.isShareZ());
    }


    private APIGetProductPriceReply getTunnelPrice(ProductChargeModel productChargeModel,
                                                   int duration,
                                                   String accountUuid,
                                                   String endpointAUuid,
                                                   String endpointZUuid,
                                                   String innerConnectedEndpointUuid,
                                                   String bandwidthOfferingUuid,
                                                   boolean isShareA, boolean isShareB) {

        APIGetProductPriceMsg pmsg = new APIGetProductPriceMsg();
        pmsg.setProductChargeModel(productChargeModel);
        pmsg.setDuration(duration);
        pmsg.setAccountUuid(accountUuid);
        EndpointVO endpointVOA = dbf.findByUuid(endpointAUuid, EndpointVO.class);
        EndpointVO endpointVOZ = dbf.findByUuid(endpointZUuid,EndpointVO.class);
        InnerConnectedEndpointVO innerConnectedEndpointVO = null;
        if(innerConnectedEndpointUuid != null){
            innerConnectedEndpointVO = dbf.findByUuid(innerConnectedEndpointUuid,InnerConnectedEndpointVO.class);
        }

        if(endpointVOA != null && endpointVOZ !=null && innerConnectedEndpointVO != null){
            pmsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(bandwidthOfferingUuid, isShareA, isShareB, endpointVOA,
                    endpointVOZ, innerConnectedEndpointVO.getEndpointUuid()));
        }else if(endpointVOA != null && endpointVOZ !=null && innerConnectedEndpointVO == null){
            pmsg.setUnits(new TunnelBillingBase().getTunnelPriceUnit(bandwidthOfferingUuid, isShareA, isShareB, endpointVOA,
                    endpointVOZ, null));
        }

        APIGetProductPriceReply reply = new BillingRESTCaller(CoreGlobalProperty.BILLING_SERVER_URL).syncJsonPost(pmsg);

        return reply;
    }


    //计算方案总价
    private BigDecimal totalCost(String solutionUuid){
        BigDecimal totalCost = new BigDecimal(0);

        totalCost = totalCost.add( SQL.New("select ifnull(sum(cost), 0) from SolutionInterfaceVO where solutionUuid = :solutionUuid", BigDecimal.class)
                .param("solutionUuid", solutionUuid)
                .find());

        totalCost = totalCost.add( SQL.New("select ifnull(sum(cost), 0) from SolutionTunnelVO where solutionUuid = :solutionUuid", BigDecimal.class)
                .param("solutionUuid", solutionUuid)
                .find());

        totalCost = totalCost.add( SQL.New("select ifnull(sum(cost), 0) from SolutionVpnVO where solutionUuid = :solutionUuid", BigDecimal.class)
                .param("solutionUuid", solutionUuid)
                .find());

        return totalCost;
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
        p.setName(TunnelConstant.QUOTA_SOLUTION_NUM);
        p.setValue(50);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(TunnelConstant.QUOTA_SOLUTION_INTERFACE_NUM);
        p.setValue(100);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(TunnelConstant.QUOTA_SOLUTION_TUNNEL_NUM);
        p.setValue(50);
        quota.addPair(p);

        p = new Quota.QuotaPair();
        p.setName(TunnelConstant.QUOTA_SOLUTION_VPN_NUM);
        p.setValue(100);
        quota.addPair(p);

        return list(quota);
    }

    private <T extends APIReply> T createOrder(APIMessage orderMsg) {
        String url = URLBuilder.buildUrlFromBase(CoreGlobalProperty.BILLING_SERVER_URL, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(orderMsg);
        APIReply reply;
        try {
            RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(orderMsg), RestAPIResponse.class);
            reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (!reply.isSuccess()) {
                throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, "failed to operate order."));
            }
        } catch (Exception e) {
            throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, String.format("call billing[url: %s] failed.", url)));
        }
        return reply.castReply();
    }
}
