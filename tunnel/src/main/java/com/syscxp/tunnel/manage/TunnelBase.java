package com.syscxp.tunnel.manage;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.TunnelState;
import com.syscxp.header.tunnel.TunnelStatus;
import com.syscxp.tunnel.header.switchs.*;
import com.syscxp.tunnel.header.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by DCY on 2017/10/26
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelBase extends AbstractTunnel{
    private static final CLogger logger = Utils.getLogger(TunnelBase.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ErrorFacade errf;

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
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg){}

    private void handle(CreateTunnelMsg msg){
        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        //System.out.println("！！！！！！！！！！！下发参数"+command);
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
            }
        });
    }

    private void handle(DeleteTunnelMsg msg){
        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.STOP_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发删除成功！");

                deleteTunnel(tunnelVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发删除失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);
            }
        });
    }

    private void handle(EnabledTunnelMsg msg){
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

                dbf.updateAndRefresh(tunnelVO);

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发启用失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);
            }
        });
    }

    private void handle(DisabledTunnelMsg msg){
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
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发禁用失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);
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
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发更改端口失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(command);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);
            }
        });
    }

    @Transactional
    private void deleteTunnel(TunnelVO vo){
        dbf.remove(vo);

        //删除对应的 TunnelSwitchPortVO 和 QingqVO
        SimpleQuery<TunnelSwitchPortVO> q = dbf.createQuery(TunnelSwitchPortVO.class);
        q.add(TunnelSwitchPortVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<TunnelSwitchPortVO> tivList = q.list();
        if (tivList.size() > 0) {
            for(TunnelSwitchPortVO tiv : tivList){
                dbf.remove(tiv);
            }
        }
        SimpleQuery<QinqVO> q2 = dbf.createQuery(QinqVO.class);
        q2.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<QinqVO> qinqList = q2.list();
        if (qinqList.size() > 0) {
            for(QinqVO qv : qinqList){
                dbf.remove(qv);
            }
        }
    }

    /**
     *  获取控制器下发配置
     */
    private ControllerCommands.IssuedTunnelCommand getTunnelConfigInfo(TunnelVO tunnelVO){

        List<ControllerCommands.TunnelConfig> tunnelList = new ArrayList<>();
        List<ControllerCommands.ConnectionsConfig> connectionsList = new ArrayList<>();

        List<QinqVO> qinqVOs = Q.New(QinqVO.class)
                .eq(QinqVO_.tunnelUuid,tunnelVO.getUuid())
                .list();

        boolean abroad = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"B")
                .isExists();
        if(abroad){     //跨国发两端
            List<ControllerCommands.TunnelMplsConfig> mplsListInner = new ArrayList<>();
            List<ControllerCommands.TunnelSdnConfig> sdnListInner = new ArrayList<>();
            List<ControllerCommands.TunnelMplsConfig> mplsListOuter = new ArrayList<>();
            List<ControllerCommands.TunnelSdnConfig> sdnListOuter = new ArrayList<>();
            ControllerCommands.TunnelConfig tunnelConfigInner = new ControllerCommands.TunnelConfig();
            ControllerCommands.ConnectionsConfig connectionsConfigInner = new ControllerCommands.ConnectionsConfig();
            ControllerCommands.TunnelConfig tunnelConfigOuter = new ControllerCommands.TunnelConfig();
            ControllerCommands.ConnectionsConfig connectionsConfigOuter = new ControllerCommands.ConnectionsConfig();

            TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"A")
                    .find();
            TunnelSwitchPortVO tunnelSwitchPortB = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"B")
                    .find();
            TunnelSwitchPortVO tunnelSwitchPortC = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"C")
                    .find();
            TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"Z")
                    .find();

            ControllerCommands.TunnelMplsConfig tmcA = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortA,tunnelSwitchPortB,qinqVOs);
            ControllerCommands.TunnelMplsConfig tmcB = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortB,tunnelSwitchPortA,qinqVOs);
            ControllerCommands.TunnelMplsConfig tmcC = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortC,tunnelSwitchPortZ,qinqVOs);
            ControllerCommands.TunnelMplsConfig tmcZ = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortZ,tunnelSwitchPortC,qinqVOs);
            ControllerCommands.TunnelSdnConfig tscA = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortA,qinqVOs);
            ControllerCommands.TunnelSdnConfig tscB = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortB,qinqVOs);
            ControllerCommands.TunnelSdnConfig tscC = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortC,qinqVOs);
            ControllerCommands.TunnelSdnConfig tscZ = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortZ,qinqVOs);

            mplsListInner.add(tmcA);
            mplsListInner.add(tmcB);
            mplsListOuter.add(tmcC);
            mplsListOuter.add(tmcZ);
            if(tscA != null){
                sdnListInner.add(tscA);
                connectionsConfigInner.setSdn_interface_A(tscA.getUuid());
            }
            if(tscB != null){
                sdnListInner.add(tscB);
                connectionsConfigInner.setSdn_interface_Z(tscB.getUuid());
            }
            if(tscC != null){
                sdnListOuter.add(tscC);
                connectionsConfigOuter.setSdn_interface_A(tscC.getUuid());
            }
            if(tscZ != null){
                sdnListOuter.add(tscZ);
                connectionsConfigOuter.setSdn_interface_Z(tscZ.getUuid());
            }

            tunnelConfigInner.setTunnel_id(tunnelVO.getUuid());
            tunnelConfigInner.setIs_on_local(isOnLock(tunnelSwitchPortA,tunnelSwitchPortB));
            tunnelConfigInner.setMpls_switches(mplsListInner);
            if(sdnListInner.size()>0){
                tunnelConfigInner.setSdn_switches(sdnListInner);
            }

            connectionsConfigInner.setTunnel_id(tunnelVO.getUuid());
            connectionsConfigInner.setMpls_interface_A(tmcA.getUuid());
            connectionsConfigInner.setMpls_interface_Z(tmcB.getUuid());

            tunnelConfigOuter.setTunnel_id(tunnelVO.getUuid());
            tunnelConfigOuter.setIs_on_local(isOnLock(tunnelSwitchPortC,tunnelSwitchPortZ));
            tunnelConfigOuter.setMpls_switches(mplsListOuter);
            if(sdnListOuter.size()>0){
                tunnelConfigOuter.setSdn_switches(sdnListOuter);
            }

            connectionsConfigOuter.setTunnel_id(tunnelVO.getUuid());
            connectionsConfigOuter.setMpls_interface_A(tmcC.getUuid());
            connectionsConfigOuter.setMpls_interface_Z(tmcZ.getUuid());

            tunnelList.add(tunnelConfigInner);
            connectionsList.add(connectionsConfigInner);
            tunnelList.add(tunnelConfigOuter);
            connectionsList.add(connectionsConfigOuter);


        }else{
            List<ControllerCommands.TunnelMplsConfig> mplsList = new ArrayList<>();
            List<ControllerCommands.TunnelSdnConfig> sdnList = new ArrayList<>();
            ControllerCommands.TunnelConfig tunnelConfig = new ControllerCommands.TunnelConfig();
            ControllerCommands.ConnectionsConfig connectionsConfig = new ControllerCommands.ConnectionsConfig();

            TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"A")
                    .find();

            TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,tunnelVO.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"Z")
                    .find();

            ControllerCommands.TunnelMplsConfig tmcA = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortA,tunnelSwitchPortZ,qinqVOs);
            ControllerCommands.TunnelMplsConfig tmcZ = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortZ,tunnelSwitchPortA,qinqVOs);
            ControllerCommands.TunnelSdnConfig tscA = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortA,qinqVOs);
            ControllerCommands.TunnelSdnConfig tscZ = getTunnelSdnConfig(tunnelVO.getBandwidth(),tunnelSwitchPortZ,qinqVOs);

            mplsList.add(tmcA);
            mplsList.add(tmcZ);
            if(tscA != null){
                sdnList.add(tscA);
                connectionsConfig.setSdn_interface_A(tscA.getUuid());
            }
            if(tscZ != null){
                sdnList.add(tscZ);
                connectionsConfig.setSdn_interface_Z(tscZ.getUuid());
            }


            tunnelConfig.setTunnel_id(tunnelVO.getUuid());
            tunnelConfig.setIs_on_local(isOnLock(tunnelSwitchPortA,tunnelSwitchPortZ));
            tunnelConfig.setMpls_switches(mplsList);
            if(sdnList.size()>0){
                tunnelConfig.setSdn_switches(sdnList);
            }

            connectionsConfig.setTunnel_id(tunnelVO.getUuid());
            connectionsConfig.setMpls_interface_A(tmcA.getUuid());
            connectionsConfig.setMpls_interface_Z(tmcZ.getUuid());

            tunnelList.add(tunnelConfig);
            connectionsList.add(connectionsConfig);
        }

        return ControllerCommands.IssuedTunnelCommand.valueOf(tunnelList,connectionsList);
    }

    /**
     *  MPLS 下发配置
     */
    private ControllerCommands.TunnelMplsConfig getTunnelMplsConfig(TunnelVO tunnelVO, TunnelSwitchPortVO tunnelSwitchPortVO, TunnelSwitchPortVO remoteSwitchPortVO, List<QinqVO> qinqVOs){
        ControllerCommands.TunnelMplsConfig tmc = new ControllerCommands.TunnelMplsConfig();
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitch(switchPortVO);

        if(physicalSwitchVO.getAccessType() == PhysicalSwitchAccessType.SDN){   //SDN接入

            //找到SDN交换机的上联传输交换机
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVO.getUuid())
                    .find();
            PhysicalSwitchVO mplsPhysicalSwitch = dbf.findByUuid(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid(),PhysicalSwitchVO.class);
            SwitchModelVO mplsSwitchModel = dbf.findByUuid(mplsPhysicalSwitch.getSwitchModelUuid(),SwitchModelVO.class);

            PhysicalSwitchVO remoteMplsPhysicalSwitch = getRemotePhysicalSwitch(remoteSwitchPortVO);

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

        }else if(physicalSwitchVO.getAccessType() == PhysicalSwitchAccessType.MPLS){  //Mpls接入
            SwitchModelVO switchModel = dbf.findByUuid(physicalSwitchVO.getSwitchModelUuid(),SwitchModelVO.class);

            PhysicalSwitchVO remoteMplsPhysicalSwitch = getRemotePhysicalSwitch(remoteSwitchPortVO);

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
        }

        return tmc;
    }

    /**
     *  SDN 下发配置
     */
    private ControllerCommands.TunnelSdnConfig getTunnelSdnConfig(Long bandwidth, TunnelSwitchPortVO tunnelSwitchPortVO, List<QinqVO> qinqVOs){
        ControllerCommands.TunnelSdnConfig tsc = new ControllerCommands.TunnelSdnConfig();
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitch(switchPortVO);
        if(physicalSwitchVO.getAccessType() == PhysicalSwitchAccessType.SDN){   //SDN接入
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

        }else if(physicalSwitchVO.getAccessType() == PhysicalSwitchAccessType.MPLS){  //Mpls接入
            tsc = null;
        }
        return tsc;
    }

    /**根据 tunnelSwitchPortVO 获取对端MPLS交换机 */
    private PhysicalSwitchVO getRemotePhysicalSwitch(TunnelSwitchPortVO tunnelSwitchPortVO){
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitch(switchPortVO);
        if(physicalSwitchVO.getAccessType() == PhysicalSwitchAccessType.SDN) {   //SDN接入
            //找到SDN交换机的上联传输交换机
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVO.getUuid())
                    .find();
            PhysicalSwitchVO mplsPhysicalSwitch = dbf.findByUuid(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid(),PhysicalSwitchVO.class);
            physicalSwitchVO = mplsPhysicalSwitch;
        }

        return physicalSwitchVO;
    }

    /**内部VLAN段拼接成字符串 */
    private String getInnerVlanToString(List<QinqVO> qinqVOs){
        StringBuffer buf=new StringBuffer();
        for(int i=0;i<qinqVOs.size();i++){
            QinqVO qinqVO = qinqVOs.get(i);
            if(i == (qinqVOs.size()-1)){
                if(qinqVO.getStartVlan() == qinqVO.getEndVlan()){
                    buf.append(qinqVO.getStartVlan().toString());
                }else{
                    buf.append(qinqVO.getStartVlan().toString());
                    buf.append(" to ");
                    buf.append(qinqVO.getEndVlan().toString());
                }
            }else{
                if(qinqVO.getStartVlan() == qinqVO.getEndVlan()){
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
        String d=buf.toString();
        return d;
    }

    /**根据 switchPort 找出所属的 PhysicalSwitch */
    private PhysicalSwitchVO getPhysicalSwitch(SwitchPortVO switchPortVO){
        SwitchVO switchVO = dbf.findByUuid(switchPortVO.getSwitchUuid(),SwitchVO.class);
        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(switchVO.getPhysicalSwitchUuid(),PhysicalSwitchVO.class);
        return physicalSwitchVO;
    }

    /**
     *  判断该通道是否onlock
     */
    private boolean isOnLock(TunnelSwitchPortVO tunnelSwitchPortA,TunnelSwitchPortVO tunnelSwitchPortZ){
        boolean isonlock = false;
        SwitchPortVO switchPortA = dbf.findByUuid(tunnelSwitchPortA.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchA = getPhysicalSwitch(switchPortA);

        SwitchPortVO switchPortZ = dbf.findByUuid(tunnelSwitchPortZ.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchZ = getPhysicalSwitch(switchPortZ);

        if(physicalSwitchA.getAccessType() == physicalSwitchZ.getAccessType()){
            if(physicalSwitchA.getUuid().equals(physicalSwitchZ.getUuid())){
                isonlock = true;
            }
        }

        return isonlock;
    }
}
