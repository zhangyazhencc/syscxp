package com.syscxp.tunnel.network;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.configuration.BandwidthOfferingVO;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.switchs.PhysicalSwitchVO;
import com.syscxp.header.tunnel.switchs.SwitchModelVO;
import com.syscxp.header.tunnel.switchs.SwitchPortVO;
import com.syscxp.header.tunnel.tunnel.TaskResourceVO;
import com.syscxp.header.tunnel.tunnel.TaskStatus;
import com.syscxp.tunnel.sdnController.ControllerCommands;
import com.syscxp.tunnel.sdnController.ControllerRestConstant;
import com.syscxp.tunnel.sdnController.ControllerRestFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by DCY on 2018/3/7
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkControllerBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkControllerBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private RESTFacade restf;

    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg){
        if(msg instanceof CreateL3EndpointMsg){
            handle((CreateL3EndpointMsg) msg);
        }else if(msg instanceof DeleteL3EndPointMsg){
            handle((DeleteL3EndPointMsg) msg);
        }else if(msg instanceof UpdateL3EndpointBandwidthMsg){
            handle((UpdateL3EndpointBandwidthMsg) msg);
        }else if(msg instanceof CreateL3RouteMsg){
            handle((CreateL3RouteMsg) msg);
        }else if(msg instanceof DeleteL3RouteMsg){
            handle((DeleteL3RouteMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg){}

    private void handle(CreateL3RouteMsg msg){
        CreateL3RouteReply reply = new CreateL3RouteReply();

        L3RouteVO l3RouteVO = dbf.findByUuid(msg.getL3RouteUuid(),L3RouteVO.class);
        L3EndPointVO l3EndPointVO = dbf.findByUuid(l3RouteVO.getL3EndPointUuid(),L3EndPointVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.L3NetworkConfig l3NetworkConfig = getL3NetworkConfigInfo(l3RouteVO);
        String command = JSONObjectUtil.toJsonString(l3NetworkConfig);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.ADD_ROUTES, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("添加路由条目成功！");

                //更新状态
                l3EndPointVO.setState(L3EndpointState.Enabled);
                l3EndPointVO.setStatus(L3EndpointStatus.Connected);
                dbf.updateAndRefresh(l3EndPointVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("添加路由条目失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(DeleteL3RouteMsg msg){
        DeleteL3RouteReply reply = new DeleteL3RouteReply();

        L3RouteVO l3RouteVO = dbf.findByUuid(msg.getL3RouteUuid(),L3RouteVO.class);
        L3EndPointVO l3EndPointVO = dbf.findByUuid(l3RouteVO.getL3EndPointUuid(),L3EndPointVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.L3NetworkConfig l3NetworkConfig = getL3NetworkConfigInfo(l3RouteVO);
        String command = JSONObjectUtil.toJsonString(l3NetworkConfig);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.DELETE_ROUTES, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("删除路由条目成功！");

                //更新状态
                l3EndPointVO.setState(L3EndpointState.Enabled);
                l3EndPointVO.setStatus(L3EndpointStatus.Connected);
                dbf.updateAndRefresh(l3EndPointVO);

                dbf.remove(l3RouteVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("删除路由条目失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(UpdateL3EndpointBandwidthMsg msg){
        UpdateL3EndpointBandwidthReply reply = new UpdateL3EndpointBandwidthReply();

        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndpointUuid(),L3EndPointVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.L3NetworkConfig l3NetworkConfig = getL3NetworkConfigInfo(l3EndPointVO);
        String command = JSONObjectUtil.toJsonString(l3NetworkConfig);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.MODIFY_L3BANDWIDTH, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发修改L3带宽成功！");

                //更新状态
                l3EndPointVO.setState(L3EndpointState.Enabled);
                l3EndPointVO.setStatus(L3EndpointStatus.Connected);
                dbf.updateAndRefresh(l3EndPointVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发修改L3带宽失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(CreateL3EndpointMsg msg){
        CreateL3EndpointReply reply = new CreateL3EndpointReply();

        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndpointUuid(),L3EndPointVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.L3NetworkConfig l3NetworkConfig = getL3NetworkConfigInfo(l3EndPointVO);
        String command = JSONObjectUtil.toJsonString(l3NetworkConfig);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.CREATE_L3ENDPOINT, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发创建云网络连接点成功！");

                //更新状态
                l3EndPointVO.setState(L3EndpointState.Enabled);
                l3EndPointVO.setStatus(L3EndpointStatus.Connected);
                dbf.updateAndRefresh(l3EndPointVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发创建云网络连接点失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(DeleteL3EndPointMsg msg){
        DeleteL3EndPointReply reply = new DeleteL3EndPointReply();

        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndpointUuid(),L3EndPointVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.L3NetworkConfig l3NetworkConfig = getL3NetworkConfigInfo(l3EndPointVO);
        String command = JSONObjectUtil.toJsonString(l3NetworkConfig);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.DELETE_L3ENDPOINT, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发删除L3连接点成功！");

                //更新状态
                l3EndPointVO.setState(L3EndpointState.Disabled);
                l3EndPointVO.setStatus(L3EndpointStatus.Disconnected);
                dbf.updateAndRefresh(l3EndPointVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发删除L3连接点失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }



    /**
     * 控制器下发配置--下发单位为连接点
     * */
    public ControllerCommands.L3NetworkConfig getL3NetworkConfigInfo(L3EndPointVO vo){
        ControllerCommands.L3NetworkConfig l3NetworkConfig = new ControllerCommands.L3NetworkConfig();

        List<ControllerCommands.L3RoutesConfig> routes = new ArrayList<>();

        if(Q.New(L3RouteVO.class).eq(L3RouteVO_.l3EndPointUuid, vo.getUuid()).isExists()){
            List<L3RouteVO> l3RouteVOS = Q.New(L3RouteVO.class).eq(L3RouteVO_.l3EndPointUuid, vo.getUuid()).list();
            for (L3RouteVO l3RouteVO : l3RouteVOS) {
                String[] cidr = l3RouteVO.getCidr().split("/");
                ControllerCommands.L3RoutesConfig l3RoutesConfig = new ControllerCommands.L3RoutesConfig();
                l3RoutesConfig.setBusiness_ip(cidr[0]);
                l3RoutesConfig.setNetmask(cidr[1]);
                l3RoutesConfig.setRoute_ip(l3RouteVO.getNextIp());
                l3RoutesConfig.setIndex(l3RouteVO.getIndexNum());
                routes.add(l3RoutesConfig);
            }
        }


        List<ControllerCommands.L3MplsConfig> mpls_switches = new ArrayList<>();
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(vo.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        SwitchModelVO switchModelVO = dbf.findByUuid(physicalSwitchVO.getSwitchModelUuid(), SwitchModelVO.class);
        SwitchPortVO switchPortVO = dbf.findByUuid(vo.getSwitchPortUuid(), SwitchPortVO.class);

        ControllerCommands.L3MplsConfig l3MplsConfig = new ControllerCommands.L3MplsConfig();
        l3MplsConfig.setUuid(vo.getPhysicalSwitchUuid());
        l3MplsConfig.setSwitch_type(switchModelVO.getModel());
        l3MplsConfig.setSub_type(switchModelVO.getSubModel());
        l3MplsConfig.setPort_name(switchPortVO.getPortName());
        l3MplsConfig.setVlan_id(vo.getVlan());
        l3MplsConfig.setM_ip(physicalSwitchVO.getmIP());
        l3MplsConfig.setProtocol(physicalSwitchVO.getProtocol().toString());
        l3MplsConfig.setPort(physicalSwitchVO.getPort());
        l3MplsConfig.setConnect_ip_local(vo.getLocalIP());
        l3MplsConfig.setConnect_ip_remote(vo.getRemoteIp());
        l3MplsConfig.setNetmask(vo.getNetmask());
        l3MplsConfig.setUsername(physicalSwitchVO.getUsername());
        l3MplsConfig.setPassword(physicalSwitchVO.getPassword());
        l3MplsConfig.setBandwidth(vo.getBandwidth()/1024);
        l3MplsConfig.setRoutes(routes);
        mpls_switches.add(l3MplsConfig);

        L3NetworkVO l3NetworkVO = dbf.findByUuid(vo.getL3NetworkUuid(), L3NetworkVO.class);
        l3NetworkConfig.setNet_id(l3NetworkVO.getUuid());
        l3NetworkConfig.setVrf_id(l3NetworkVO.getVid());
        l3NetworkConfig.setUsername(l3NetworkVO.getCode());
        l3NetworkConfig.setMpls_switches(mpls_switches);
        return l3NetworkConfig;
    }

    /**
     * 控制器下发配置--下发单位为路由
     * */
    public ControllerCommands.L3NetworkConfig getL3NetworkConfigInfo(L3RouteVO vo){
        ControllerCommands.L3NetworkConfig l3NetworkConfig = new ControllerCommands.L3NetworkConfig();

        List<ControllerCommands.L3RoutesConfig> routes = new ArrayList<>();

        ControllerCommands.L3RoutesConfig l3RoutesConfig = new ControllerCommands.L3RoutesConfig();
        String[] cidr = vo.getCidr().split("/");
        l3RoutesConfig.setBusiness_ip(cidr[0]);
        l3RoutesConfig.setNetmask(cidr[1]);
        l3RoutesConfig.setRoute_ip(vo.getNextIp());
        l3RoutesConfig.setIndex(vo.getIndexNum());
        routes.add(l3RoutesConfig);

        L3EndPointVO l3EndPointVO = dbf.findByUuid(vo.getL3EndPointUuid(), L3EndPointVO.class);
        List<ControllerCommands.L3MplsConfig> mpls_switches = new ArrayList<>();
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(l3EndPointVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        SwitchModelVO switchModelVO = dbf.findByUuid(physicalSwitchVO.getSwitchModelUuid(), SwitchModelVO.class);
        SwitchPortVO switchPortVO = dbf.findByUuid(l3EndPointVO.getSwitchPortUuid(), SwitchPortVO.class);

        ControllerCommands.L3MplsConfig l3MplsConfig = new ControllerCommands.L3MplsConfig();
        l3MplsConfig.setUuid(l3EndPointVO.getPhysicalSwitchUuid());
        l3MplsConfig.setSwitch_type(switchModelVO.getModel());
        l3MplsConfig.setSub_type(switchModelVO.getSubModel());
        l3MplsConfig.setPort_name(switchPortVO.getPortName());
        l3MplsConfig.setVlan_id(l3EndPointVO.getVlan());
        l3MplsConfig.setM_ip(physicalSwitchVO.getmIP());
        l3MplsConfig.setProtocol(physicalSwitchVO.getProtocol().toString());
        l3MplsConfig.setPort(physicalSwitchVO.getPort());
        l3MplsConfig.setConnect_ip_local(l3EndPointVO.getLocalIP());
        l3MplsConfig.setConnect_ip_remote(l3EndPointVO.getRemoteIp());
        l3MplsConfig.setNetmask(l3EndPointVO.getNetmask());
        l3MplsConfig.setUsername(physicalSwitchVO.getUsername());
        l3MplsConfig.setPassword(physicalSwitchVO.getPassword());
        l3MplsConfig.setBandwidth(l3EndPointVO.getBandwidth()/1024);
        l3MplsConfig.setRoutes(routes);
        mpls_switches.add(l3MplsConfig);

        L3NetworkVO l3NetworkVO = dbf.findByUuid(l3EndPointVO.getL3NetworkUuid(), L3NetworkVO.class);
        l3NetworkConfig.setNet_id(l3NetworkVO.getUuid());
        l3NetworkConfig.setVrf_id(l3NetworkVO.getVid());
        l3NetworkConfig.setUsername(l3NetworkVO.getCode());
        l3NetworkConfig.setMpls_switches(mpls_switches);
        return l3NetworkConfig;
    }
}
