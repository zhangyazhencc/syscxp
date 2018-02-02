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

import javax.persistence.TypedQuery;
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
        }else if(msg instanceof DeleteTunnelZKMsg){
            handle((DeleteTunnelZKMsg) msg);
        }else if(msg instanceof EnabledTunnelMsg){
            handle((EnabledTunnelMsg) msg);
        }else if(msg instanceof CreateTunnelZKMsg){
            handle((CreateTunnelZKMsg) msg);
        }else if(msg instanceof DisabledTunnelMsg){
            handle((DisabledTunnelMsg) msg);
        }else if(msg instanceof ModifyTunnelBandwidthMsg){
            handle((ModifyTunnelBandwidthMsg) msg);
        }else if(msg instanceof ModifyTunnelPortsMsg){
            handle((ModifyTunnelPortsMsg) msg);
        }else if(msg instanceof ModifyTunnelPortsZKMsg){
            handle((ModifyTunnelPortsZKMsg) msg);
        }else if(msg instanceof ListTraceRouteMsg){
            handle((ListTraceRouteMsg) msg);
        }else if(msg instanceof RevertTunnelMsg){
            handle((RevertTunnelMsg) msg);
        }else if(msg instanceof RollBackCreateTunnelMsg){
            handle((RollBackCreateTunnelMsg) msg);
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg){}

    private void handle(ListTraceRouteMsg msg){
        ListTraceRouteReply reply = new ListTraceRouteReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);

        List<ControllerCommands.TunnelMplsConfig> tunnelMplsConfig = issuedTunnelCommand.getTunnel().get(0).getMpls_switches();
        for (int i = 0; i < tunnelMplsConfig.size(); i++){
            ControllerCommands.TunnelMplsConfig tc = tunnelMplsConfig.get(i);
            if(tc.getSortTag().equals("B") || tc.getSortTag().equals("Z")){
                tunnelMplsConfig.remove(tc);
            }
        }

        String command = JSONObjectUtil.toJsonString(tunnelMplsConfig);
        logger.info("----TraceRoute----:"+command);

        String url = CoreGlobalProperty.CONTROLLER_MANAGER_URL+ControllerRestConstant.TUNNEL_TRACE;
        try {

            ControllerCommands.ControllerTraceResponse traceResponse = restf.syncJsonPost(url, command, ControllerCommands.ControllerTraceResponse.class);
            reply.setMsg(traceResponse.getMsg());

            bus.reply(msg, reply);
        } catch (Exception e) {
            String errorMsg = String.format("Exception: TraceRoute %s. %s", url, e.getMessage());
            reply.setError(errf.stringToOperationError(errorMsg));

            bus.reply(msg, reply);
        }
    }

    private void handle(RevertTunnelMsg msg){
        RevertTunnelReply reply = new RevertTunnelReply();

        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.START_TUNNEL, msg.getCommands(), new Completion(null) {
            @Override
            public void success() {
                logger.info("下发恢复成功！");

                //更新任务状态
                taskResourceVO.setBody(msg.getCommands());
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发恢复失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(msg.getCommands());
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    private void handle(RollBackCreateTunnelMsg msg){
        RollBackCreateTunnelReply reply = new RollBackCreateTunnelReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, true);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.START_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发创建回滚成功！");

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发创建回滚失败！");

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

    private void handle(CreateTunnelMsg msg){
        CreateTunnelReply reply = new CreateTunnelReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
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

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.STOP_TUNNEL, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发删除成功！");

                new TunnelBase().deleteTunnelDB(tunnelVO);

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

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
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

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
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
        ModifyTunnelBandwidthReply reply = new ModifyTunnelBandwidthReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
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

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发调整带宽失败！");

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

    private void handle(ModifyTunnelPortsMsg msg){
        ModifyTunnelPortsReply reply = new ModifyTunnelPortsReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
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

    /***********************************************************************************************************************************************************************/

    private void handle(ModifyTunnelPortsZKMsg msg){
        ModifyTunnelPortsZKReply reply = new ModifyTunnelPortsZKReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.MODIFY_TUNNEL_PORTS_ZK, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发更改端口ZK数据修改成功！");

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发更改端口ZK数据修改失败！");

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

    private void handle(CreateTunnelZKMsg msg){
        CreateTunnelZKReply reply = new CreateTunnelZKReply();

        TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
        String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.START_TUNNEL_ZK, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发保存ZK数据成功！");

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发保存ZK数据失败！");

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

    private void handle(DeleteTunnelZKMsg msg){
        DeleteTunnelZKReply reply = new DeleteTunnelZKReply();
        String commands;
        if(msg.getTunnelUuid() == null){
            commands = msg.getCommands();
        }else{
            TunnelVO tunnelVO = dbf.findByUuid(msg.getTunnelUuid(),TunnelVO.class);
            ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
            commands = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        }

        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.STOP_TUNNEL_ZK, commands, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发删除ZK数据成功！");

                //更新任务状态
                taskResourceVO.setBody(commands);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发删除ZK数据失败！");

                //更新任务状态
                taskResourceVO.setStatus(TaskStatus.Fail);
                taskResourceVO.setBody(commands);
                taskResourceVO.setResult(JSONObjectUtil.toJsonString(errorCode));
                dbf.updateAndRefresh(taskResourceVO);

                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }

    /*************************************************************************************************************************************************************************************/

    /**
     *  获取控制器下发配置
     */
    public ControllerCommands.IssuedTunnelCommand getTunnelConfigInfo(TunnelVO tunnelVO, boolean isRollback){

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
            tmcB = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortB,qinqVOs,"B");
            tmcC = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortC,qinqVOs,"C");
            tmcA = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortA,qinqVOs,"A");
            tmcZ = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortZ,qinqVOs,"Z");
        }else{
            tmcA = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortA,qinqVOs,"A");
            tmcZ = getTunnelMplsConfig(tunnelVO,tunnelSwitchPortZ,qinqVOs,"Z");
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
        tunnelConfig.setRollback(isRollback);
        tunnelConfig.setCross_tunnel(getCrossPhysicalSwitchUuid(tunnelVO));
        tunnelConfig.setSame_switch(getSameSwitch(tunnelVO));
        tunnelConfig.setMpls_switches(mplsList);
        if(sdnList.size()>0){
            tunnelConfig.setSdn_switches(sdnList);
        }

        tunnelList.add(tunnelConfig);


        return ControllerCommands.IssuedTunnelCommand.valueOf(tunnelList);
    }

    /**
     * 通过专线找出same_switch
     * */
    public String[] getSameSwitch(TunnelVO vo){
        TunnelBase tunnelBase = new TunnelBase();
        String switchPortUuidA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"A")
                .select(TunnelSwitchPortVO_.switchPortUuid)
                .findValue();
        String switchPortUuidB = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"B")
                .select(TunnelSwitchPortVO_.switchPortUuid)
                .findValue();
        String switchPortUuidC = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"C")
                .select(TunnelSwitchPortVO_.switchPortUuid)
                .findValue();
        String switchPortUuidZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"Z")
                .select(TunnelSwitchPortVO_.switchPortUuid)
                .findValue();
        if(switchPortUuidB == null){
            return tunnelBase.getSamePhysicalSwitchUuidForControl(switchPortUuidA,switchPortUuidZ);
        }else{
            if(tunnelBase.isSamePhysicalSwitchForTunnel(dbf.findByUuid(switchPortUuidA,SwitchPortVO.class),dbf.findByUuid(switchPortUuidB,SwitchPortVO.class))){
                return tunnelBase.getSamePhysicalSwitchUuidForControl(switchPortUuidA,switchPortUuidB);
            }else{
                return tunnelBase.getSamePhysicalSwitchUuidForControl(switchPortUuidC,switchPortUuidZ);
            }
        }

    }

    /**
     * 通过专线找出共点的交换机
     * */
    public String[] getCrossPhysicalSwitchUuid(TunnelVO vo){
        TunnelBase tunnelBase = new TunnelBase();
        TunnelSwitchPortVO tunnelSwitchPortVOA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortVOZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag,"Z")
                .find();
        if(isCross(vo.getUuid(),tunnelSwitchPortVOA.getInterfaceUuid(),tunnelSwitchPortVOA.getVlan())){
            String switchPortUuidA = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"A")
                    .select(TunnelSwitchPortVO_.switchPortUuid)
                    .findValue();
            PhysicalSwitchVO physicalSwitchVOA = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuidA);
            if(physicalSwitchVOA.getType() == PhysicalSwitchType.SDN){
                PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVOA= Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVOA.getUuid())
                        .find();
                return new String[]{physicalSwitchVOA.getUuid(),physicalSwitchUpLinkRefVOA.getUplinkPhysicalSwitchUuid()};
            }else{
                return new String[]{physicalSwitchVOA.getUuid()};
            }
        }else if(isCross(vo.getUuid(),tunnelSwitchPortVOZ.getInterfaceUuid(),tunnelSwitchPortVOZ.getVlan())){
            String switchPortUuidZ = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid,vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag,"Z")
                    .select(TunnelSwitchPortVO_.switchPortUuid)
                    .findValue();
            PhysicalSwitchVO physicalSwitchVOZ = tunnelBase.getPhysicalSwitchBySwitchPortUuid(switchPortUuidZ);
            if(physicalSwitchVOZ.getType() == PhysicalSwitchType.SDN){
                PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVOZ= Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVOZ.getUuid())
                        .find();
                return new String[]{physicalSwitchVOZ.getUuid(),physicalSwitchUpLinkRefVOZ.getUplinkPhysicalSwitchUuid()};
            }else{
                return new String[]{physicalSwitchVOZ.getUuid()};
            }
        }else{
            return new String[]{""};
        }
    }

    /**
     * 控制器下发的共点判断
     * */
    public boolean isCross(String tunnelUuid, String interfaceUuid, Integer vlan) {
        TunnelVO vo = dbf.findByUuid(tunnelUuid, TunnelVO.class);
        Integer vsi = vo.getVsi();

        String sql = "select b from TunnelVO a, TunnelSwitchPortVO b " +
                "where a.uuid = b.tunnelUuid " +
                "and a.uuid != :tunnelUuid " +
                "and a.state = 'Enabled' " +
                "and a.vsi = :vsi " +
                "and b.interfaceUuid = :interfaceUuid " +
                "and b.vlan = :vlan";
        TypedQuery<TunnelSwitchPortVO> vq = dbf.getEntityManager().createQuery(sql, TunnelSwitchPortVO.class);
        vq.setParameter("tunnelUuid", tunnelUuid);
        vq.setParameter("vsi", vsi);
        vq.setParameter("interfaceUuid", interfaceUuid);
        vq.setParameter("vlan", vlan);
        if (vq.getResultList().size() < 1) {
            return false;
        } else {
            return true;
        }

    }


    /**
     *  MPLS 下发配置
     */
    public ControllerCommands.TunnelMplsConfig getTunnelMplsConfig(TunnelVO tunnelVO, TunnelSwitchPortVO tunnelSwitchPortVO, List<QinqVO> qinqVOs, String sortTag){

        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(tunnelSwitchPortVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO mplsPhysicalSwitchVO = dbf.findByUuid(tunnelSwitchPortVO.getOwnerMplsSwitchUuid(), PhysicalSwitchVO.class);
        SwitchModelVO mplsSwitchModel = dbf.findByUuid(mplsPhysicalSwitchVO.getSwitchModelUuid(),SwitchModelVO.class);
        PhysicalSwitchVO peerMplsPhysicalSwitchVO = dbf.findByUuid(tunnelSwitchPortVO.getPeerMplsSwitchUuid(), PhysicalSwitchVO.class);


        ControllerCommands.TunnelMplsConfig tmc = new ControllerCommands.TunnelMplsConfig();

        tmc.setUuid(mplsPhysicalSwitchVO.getUuid());
        tmc.setSwitch_type(mplsSwitchModel.getModel());
        tmc.setSub_type(mplsSwitchModel.getSubModel());
        tmc.setVni(tunnelVO.getVsi());
        tmc.setRemote_ip(peerMplsPhysicalSwitchVO.getLocalIP());
        tmc.setVlan_id(tunnelSwitchPortVO.getVlan());
        tmc.setM_ip(mplsPhysicalSwitchVO.getmIP());
        tmc.setUsername(mplsPhysicalSwitchVO.getUsername());
        tmc.setPassword(mplsPhysicalSwitchVO.getPassword());
        tmc.setSortTag(sortTag);

        if(physicalSwitchVO.getType() == PhysicalSwitchType.SDN){

            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVO.getUuid())
                    .find();
            tmc.setPort_name(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchPortName());
            tmc.setNetwork_type("TRUNK");
        }else{
            tmc.setPort_name(switchPortVO.getPortName());
            tmc.setNetwork_type(tunnelSwitchPortVO.getType().toString());

            tmc.setBandwidth(tunnelVO.getBandwidth()/1024);
            if(tunnelSwitchPortVO.getType() == NetworkType.QINQ){
                tmc.setInner_vlan_id(getInnerVlanToString(qinqVOs));
            }
        }

        return tmc;

    }

    /**
     *  SDN 下发配置
     */
    public ControllerCommands.TunnelSdnConfig getTunnelSdnConfig(Long bandwidth, TunnelSwitchPortVO tunnelSwitchPortVO, List<QinqVO> qinqVOs, String sortTag){
        ControllerCommands.TunnelSdnConfig tsc = new ControllerCommands.TunnelSdnConfig();

        PhysicalSwitchVO physicalSwitchVO = dbf.findByUuid(tunnelSwitchPortVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);

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
    public String getInnerVlanToString(List<QinqVO> qinqVOs){
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

}
