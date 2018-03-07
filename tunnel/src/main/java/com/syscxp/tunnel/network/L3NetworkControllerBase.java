package com.syscxp.tunnel.network;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.billing.ProductChargeModel;
import com.syscxp.header.core.Completion;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.network.CreateL3EndpointMsg;
import com.syscxp.header.tunnel.network.CreateL3EndpointReply;
import com.syscxp.header.tunnel.network.L3EndPointVO;
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
        }else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleApiMessage(APIMessage msg){}

    private void handle(CreateL3EndpointMsg msg){
        CreateL3EndpointReply reply = new CreateL3EndpointReply();

        L3EndPointVO l3EndPointVO = dbf.findByUuid(msg.getL3EndpointUuid(),L3EndPointVO.class);
        TaskResourceVO taskResourceVO = dbf.findByUuid(msg.getTaskUuid(),TaskResourceVO.class);

        //ControllerCommands.IssuedTunnelCommand issuedTunnelCommand = getTunnelConfigInfo(tunnelVO, false);
        //String command = JSONObjectUtil.toJsonString(issuedTunnelCommand);
        String command = "";
        ControllerRestFacade crf = new ControllerRestFacade(CoreGlobalProperty.CONTROLLER_MANAGER_URL);

        crf.sendCommand(ControllerRestConstant.CREATE_L3ENDPOINT, command, new Completion(null) {
            @Override
            public void success() {
                logger.info("下发创建云网络连接点成功！");

                //修改连接点状态

                //更新任务状态
                taskResourceVO.setBody(command);
                taskResourceVO.setStatus(TaskStatus.Success);
                dbf.updateAndRefresh(taskResourceVO);

                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                logger.info("下发创建云网络连接点失败！");

                //修改连接点状态

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
}
