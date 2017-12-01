package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.tunnel.TunnelState;
import com.syscxp.header.tunnel.tunnel.TunnelStatus;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
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
import java.util.Objects;


/**
 * Create by DCY on 2017/10/26
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelControllerBase extends AbstractTunnel {
    private static final CLogger logger = Utils.getLogger(TunnelControllerBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
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

    private void handleLocalMessage(Message msg){
        if(msg instanceof CreateTunnelMsg){
            handle((CreateTunnelMsg) msg);
        }else if(msg instanceof DeleteTunnelMsg){
            handle((DeleteTunnelMsg) msg);
        }else if(msg instanceof EnabledTunnelMsg){
            handle((EnabledTunnelMsg) msg);
        }else if(msg instanceof DisabledTunnelMsg){
            handle((DisabledTunnelMsg) msg);
        }else if(msg instanceof ModifyTunnelBandwidthMsg){
            handle((ModifyTunnelBandwidthMsg) msg);
        }else if(msg instanceof ModifyTunnelPortsMsg){
            handle((ModifyTunnelPortsMsg) msg);
        }else if(msg instanceof ListTraceRouteMsg){
            handle((ListTraceRouteMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg){}

    private void handle(ListTraceRouteMsg msg){
        ListTraceRouteReply reply = new ListTraceRouteReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);

        List<ControllerCommands.TunnelMplsConfig> tunnelMplsConfig = issuedTunnelCommand.getTunnel().get(0).getMpls_switches();
        for (int i = 0; i < tunnelMplsConfig.size(); i++){
            ControllerCommands.TunnelMplsConfig tc = tunnelMplsConfig.get(i);
            if(tc.getSortTag().equals("B") || tc.getSortTag().equals("Z")){
                tunnelMplsConfig.remove(tc);
            }
        }

        String command = JSONObjectUtil.toJsonString(tunnelMplsConfig);
        System.out.println("----TraceRoute----:"+command);

        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL+ControllerRestConstant.TUNNEL_TRACE;
        try {

            ControllerCommands.ControllerTraceResponse traceResponse = restf.syncJsonPost(url, command, ControllerCommands.ControllerTraceResponse.class);
            reply.setResults(traceResponse.getResults());

            bus.reply(msg, reply);
        } catch (Exception e) {
            String errorMsg = String.format("unable to post TraceRoute %s. %s", url, e.getMessage());
            reply.setError(errf.stringToOperationError(errorMsg));

            bus.reply(msg, reply);
        }
    }

    private void handle(CreateTunnelMsg msg){
        CreateTunnelReply reply = new CreateTunnelReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.START_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发创建成功！");

                //修改产品状态,设置到期时间
                tunnelVO.setState(TunnelState.Enabled);
                tunnelVO.setStatus(TunnelStatus.Connected);
                if(tunnelVO.getProductChargeModel() == ProductChargeModel.BY_MONTH){
                    tunnelVO.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(tunnelVO.getDuration())));
                }else if(tunnelVO.getProductChargeModel() == ProductChargeModel.BY_YEAR){
                    tunnelVO.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(tunnelVO.getDuration())));
                }
                dbf.updateAndRefresh(tunnelVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发创建失败！");

                tunnelVO.setState(TunnelState.Deployfailure);
                tunnelVO.setStatus(TunnelStatus.Disconnected);
                dbf.updateAndRefresh(tunnelVO);

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

    private void handle(DeleteTunnelMsg msg){
        DeleteTunnelReply reply = new DeleteTunnelReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.STOP_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发删除成功！");

                new TunnelBase().deleteTunnel(tunnelVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发删除失败！");

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

    private void handle(EnabledTunnelMsg msg){
        EnabledTunnelReply reply = new EnabledTunnelReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.START_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发启用成功！");

                tunnelVO.setState(TunnelState.Enabled);
                tunnelVO.setStatus(TunnelStatus.Connected);

                if(tunnelVO.getExpireDate()==null){
                    if(tunnelVO.getProductChargeModel() == ProductChargeModel.BY_MONTH){
                        tunnelVO.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusMonths(tunnelVO.getDuration())));
                    }else if(tunnelVO.getProductChargeModel() == ProductChargeModel.BY_YEAR){
                        tunnelVO.setExpireDate(Timestamp.valueOf(LocalDateTime.now().plusYears(tunnelVO.getDuration())));
                    }
                }

                dbf.updateAndRefresh(tunnelVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发启用失败！");

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

    private void handle(DisabledTunnelMsg msg){
        DisabledTunnelReply reply = new DisabledTunnelReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.STOP_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发禁用成功！");

                tunnelVO.setState(TunnelState.Disabled);
                tunnelVO.setStatus(TunnelStatus.Disconnected);

                dbf.updateAndRefresh(tunnelVO);
                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发禁用失败！");

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

    private void handle(ModifyTunnelBandwidthMsg msg){
        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.MODIFY_TUNNEL_BANDWIDTH, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发调整带宽成功！");

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发调整带宽失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);
            }
        });
    }

    private void handle(ModifyTunnelPortsMsg msg){
        ModifyTunnelPortsReply reply = new ModifyTunnelPortsReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.MODIFY_TUNNEL_PORTS, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发更改端口成功！");

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发更改端口失败！");

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
     *  获取控制器下发配置
     */
    private ControllerCommands.IssuedTunnelCommand getTunnelConfigInfo(TunnelVO tunnelVO){

        List<ControllerCommands.TunnelConfig> tunnelList = new ArrayList<>();

        List<QinqVO> qinqVOs = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid,tunnelVO.getUuid())
                .list();

        boolean abroad = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"B")
                .isExists();

        List<ControllerCommands.TunnelMplsConfig> mplsList = new ArrayList<>();
        List<ControllerCommands.TunnelSdnConfig> sdnList = new ArrayList<>();
        ControllerCommands.TunnelConfig tunnelConfig = new ControllerCommands.TunnelConfig();

        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"Z")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortB = null;
        TunnelSwitchPortVO tunnelSwitchPortC = null;
        ControllerCommands.TunnelMplsConfig tmcA = null;
        ControllerCommands.TunnelMplsConfig tmcB = null;
        ControllerCommands.TunnelMplsConfig tmcC = null;
        ControllerCommands.TunnelMplsConfig tmcZ = null;

        if(abroad){
            tunnelSwitchPortB = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"B")
                    .find();
            tunnelSwitchPortC = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"C")
                    .find();
            tmcB = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortB,tunnelSwitchPortA,qinqVOs,"B");
            tmcC = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortC,tunnelSwitchPortZ,qinqVOs,"C");
            tmcA = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortA,tunnelSwitchPortB,qinqVOs,"A");
            tmcZ = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortZ,tunnelSwitchPortC,qinqVOs,"Z");
        }else{
            tmcA = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortA,tunnelSwitchPortZ,qinqVOs,"A");
            tmcZ = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortZ,tunnelSwitchPortA,qinqVOs,"Z");
        }
        ControllerCommands.TunnelSdnConfig tscA = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortA,qinqVOs,"A");
        ControllerCommands.TunnelSdnConfig tscZ = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortZ,qinqVOs,"Z");

        mplsList.add(tmcA);
        if(tmcB != null){
            mplsList.add(tmcB);
        }
        if(tmcC != null){
            mplsList.add(tmcC);
        }
        mplsList.add(tmcZ);

        if(tscA != null){
            sdnList.add(tscA);
        }
        if(tscZ != null){
            sdnList.add(tscZ);
        }

        tunnelConfig.setTunnel_id(tunnelVO.getUuid());
        tunnelConfig.setIs_on_local(isOnLock(tunnelSwitchPortA,tunnelSwitchPortZ));
        tunnelConfig.setMpls_switches(mplsList);
        if(sdnList.size()>0){
            tunnelConfig.setSdn_switches(sdnList);
        }

        tunnelList.add(tunnelConfig);


        return ControllerCommands.IssuedTunnelCommand.valueOf(tunnelList);
    }

    /**
     *  MPLS 下发配置
     */
    private ControllerCommands.TunnelMplsConfig getTunnelMplsConfig(TunnelVO tunnelVO, TunnelSwitchPortVO tunnelSwitchPortVO, TunnelSwitchPortVO remoteSwitchPortVO, List<QinqVO> qinqVOs, String sortTag){
        TunnelBase tunnelBase = new TunnelBase();

        ControllerCommands.TunnelMplsConfig tmc = new ControllerCommands.TunnelMplsConfig();
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = new TunnelBase().getPhysicalSwitch(switchPortVO);

        if(physicalSwitchVO.getType() == PhysicalSwitchType.SDN){   //SDN接入

            //找到SDN交换机的上联传输交换机
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVO.getUuid())
                    .find();
            PhysicalSwitchVO mplsPhysicalSwitch = dbf.findByUuid(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid(),PhysicalSwitchVO.class);
            SwitchModelVO mplsSwitchModel = dbf.findByUuid(mplsPhysicalSwitch.getSwitchModelUuid(),SwitchModelVO.class);

            PhysicalSwitchVO remoteMplsPhysicalSwitch = tunnelBase.getRemotePhysicalSwitch(remoteSwitchPortVO);

            tmc.setUuid(mplsPhysicalSwitch.getUuid());
            tmc.setSwitch_type(mplsSwitchModel.getModel());
            tmc.setSub_type(mplsSwitchModel.getSubModel());
            tmc.setVni(tunnelVO.getVsi());
            tmc.setRemote_ip(remoteMplsPhysicalSwitch.getLocalIP());
            tmc.setPort_name(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchPortName());
            tmc.setVlan_id(tunnelSwitchPortVO.getVlan());
            tmc.setM_ip(mplsPhysicalSwitch.getmIP());
            tmc.setUsername(mplsPhysicalSwitch.getUsername());
            tmc.setPassword(mplsPhysicalSwitch.getPassword());
            tmc.setNetwork_type("TRUNK");
            tmc.setSortTag(sortTag);

        }else if(physicalSwitchVO.getType() == PhysicalSwitchType.MPLS){  //Mpls接入
            SwitchModelVO switchModel = dbf.findByUuid(physicalSwitchVO.getSwitchModelUuid(),SwitchModelVO.class);

            PhysicalSwitchVO remoteMplsPhysicalSwitch = tunnelBase.getRemotePhysicalSwitch(remoteSwitchPortVO);

            tmc.setUuid(physicalSwitchVO.getUuid());
            tmc.setSwitch_type(switchModel.getModel());
            tmc.setSub_type(switchModel.getSubModel());
            tmc.setVni(tunnelVO.getVsi());
            tmc.setRemote_ip(remoteMplsPhysicalSwitch.getLocalIP());
            tmc.setPort_name(switchPortVO.getPortName());
            tmc.setVlan_id(tunnelSwitchPortVO.getVlan());
            tmc.setM_ip(physicalSwitchVO.getmIP());
            tmc.setUsername(physicalSwitchVO.getUsername());
            tmc.setPassword(physicalSwitchVO.getPassword());
            tmc.setNetwork_type(tunnelSwitchPortVO.getType().toString());
            if(tunnelSwitchPortVO.getType() == NetworkType.QINQ){
                tmc.setInner_vlan_id(getInnerVlanToString(qinqVOs));
            }
            tmc.setBandwidth(tunnelVO.getBandwidth()/1024);
            tmc.setSortTag(sortTag);
        }

        return tmc;
    }

    /**
     *  SDN 下发配置
     */
    private ControllerCommands.TunnelSdnConfig getTunnelSdnConfig(Long bandwidth, TunnelSwitchPortVO tunnelSwitchPortVO, List<QinqVO> qinqVOs, String sortTag){
        ControllerCommands.TunnelSdnConfig tsc = new ControllerCommands.TunnelSdnConfig();
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = new TunnelBase().getPhysicalSwitch(switchPortVO);
        if(physicalSwitchVO.getType() == PhysicalSwitchType.SDN){   //SDN接入
            //找到SDN交换机的上联传输交换机
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVO.getUuid())
                    .find();
            tsc.setUuid(physicalSwitchVO.getUuid());
            tsc.setM_ip(physicalSwitchVO.getmIP());
            tsc.setVlan_id(tunnelSwitchPortVO.getVlan());
            tsc.setIn_port(switchPortVO.getPortName());
            tsc.setUplink(physicalSwitchUpLinkRefVO.getPortName());
            tsc.setBandwidth(bandwidth/1024);
            tsc.setNetwork_type(tunnelSwitchPortVO.getType().toString());
            if(tunnelSwitchPortVO.getType() == NetworkType.QINQ){
                tsc.setInner_vlan_id(getInnerVlanToString(qinqVOs));
            }
            tsc.setSortTag(sortTag);

        }else if(physicalSwitchVO.getType() == PhysicalSwitchType.MPLS){  //Mpls接入
            tsc = null;
        }
        return tsc;
    }

    /**内部VLAN段拼接成字符串 */
    private String getInnerVlanToString(List<QinqVO> qinqVOs){
        StringBuffer buf=new StringBuffer();
        for(int i=0;i<qinqVOs.size();i++){
            QinqVO qinqVO = qinqVOs.get(i);
            if(i == (qinqVOs.size()-1)){
                if(Objects.equals(qinqVO.getStartVlan(), qinqVO.getEndVlan())){
                    buf.append(qinqVO.getStartVlan().toString());
                }else{
                    buf.append(qinqVO.getStartVlan().toString());
                    buf.append(" to ");
                    buf.append(qinqVO.getEndVlan().toString());
                }
            }else{
                if(Objects.equals(qinqVO.getStartVlan(), qinqVO.getEndVlan())){
                    buf.append(qinqVO.getStartVlan().toString());
                    buf.append(",");
                }else{
                    buf.append(qinqVO.getStartVlan().toString());
                    buf.append(" to ");
                    buf.append(qinqVO.getEndVlan().toString());
                    buf.append(",");
                }
            }
        }
        return buf.toString();
    }

    /**
     *  判断该通道是否onlock
     */
    private boolean isOnLock(TunnelSwitchPortVO tunnelSwitchPortA,TunnelSwitchPortVO tunnelSwitchPortZ){
        TunnelBase tunnelBase = new TunnelBase();
        boolean isonlock = false;
        SwitchPortVO switchPortA = dbf.findByUuid(tunnelSwitchPortA.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchA = tunnelBase.getPhysicalSwitch(switchPortA);

        SwitchPortVO switchPortZ = dbf.findByUuid(tunnelSwitchPortZ.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchZ = tunnelBase.getPhysicalSwitch(switchPortZ);

        if(physicalSwitchA.getAccessType() == physicalSwitchZ.getAccessType()){
            if(physicalSwitchA.getUuid().equals(physicalSwitchZ.getUuid())){
                isonlock = true;
            }
        }

        return isonlock;
    }
}
