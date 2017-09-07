package org.zstack.tunnel.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.Platform;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.EventFacade;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.cloudbus.ResourceDestinationMaker;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.DbEntityLister;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.tunnel.header.host.*;
import org.zstack.tunnel.header.switchs.SwitchPortVO;
import org.zstack.tunnel.header.switchs.SwitchPortVO_;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import static org.zstack.core.Platform.argerr;

/**
 * Created by DCY on 2017-09-07
 */
public class MonitorManagerImpl  extends AbstractService implements MonitorManager,ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(MonitorManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private PluginRegistry pluginRgty;
    @Autowired
    private DbEntityLister dl;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private EventFacade evtf;


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
        if(msg instanceof APICreateHostMsg){
            handle((APICreateHostMsg) msg);
        }else if(msg instanceof APIUpdateHostMsg){
            handle((APIUpdateHostMsg) msg);
        }else if(msg instanceof APICreateHostMonitorMsg){
            handle((APICreateHostMonitorMsg) msg);
        }else if(msg instanceof APIUpdateHostMonitorMsg){
            handle((APIUpdateHostMonitorMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateHostMsg msg){
        HostVO vo = new HostVO();

        vo.setUuid(Platform.getUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setIp(msg.getIp());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        vo.setState(HostState.UNDEPLOYED);

        vo = dbf.persistAndRefresh(vo);

        APICreateHostEvent evt = new APICreateHostEvent(msg.getId());
        evt.setInventory(HostInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateHostMsg msg){
        HostVO vo = dbf.findByUuid(msg.getUuid(),HostVO.class);
        boolean update = false;
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getIp() != null){
            vo.setIp(msg.getIp());
            update = true;
        }
        if(msg.getUsername() != null){
            vo.setUsername(msg.getUsername());
            update = true;
        }
        if(msg.getPassword() != null){
            vo.setPassword(msg.getPassword());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateHostEvent evt = new APIUpdateHostEvent(msg.getId());
        evt.setInventory(HostInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateHostMonitorMsg msg){
        HostMonitorVO vo = new HostMonitorVO();

        vo.setUuid(Platform.getUuid());
        vo.setHostUuid(msg.getHostUuid());
        vo.setSwitchPortUuid(msg.getSwitchPortUuid());
        vo.setInterfaceName(msg.getInterfaceName());

        vo = dbf.persistAndRefresh(vo);

        APICreateHostMonitorEvent evt = new APICreateHostMonitorEvent(msg.getId());
        evt.setInventory(HostMonitorInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateHostMonitorMsg msg){
        HostMonitorVO vo = dbf.findByUuid(msg.getUuid(),HostMonitorVO.class);
        boolean update = false;
        if(msg.getInterfaceName() != null){
            vo.setInterfaceName(msg.getInterfaceName());
            update = true;
        }
        if(msg.getSwitchPortUuid() != null){
            vo.setSwitchPortUuid(msg.getSwitchPortUuid());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateHostMonitorEvent evt = new APIUpdateHostMonitorEvent(msg.getId());
        evt.setInventory(HostMonitorInventory.valueOf(vo));
        bus.publish(evt);
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
    public String getId() {
        return bus.makeLocalServiceId(MonitorConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if(msg instanceof APICreateHostMsg){    //---------intercept-HOST-----------------------------------
            validate((APICreateHostMsg) msg);
        }else if(msg instanceof APIUpdateHostMsg){
            validate((APIUpdateHostMsg) msg);
        }else if(msg instanceof APICreateHostMonitorMsg){
            validate((APICreateHostMonitorMsg) msg);
        }else if(msg instanceof APIUpdateHostMonitorMsg){
            validate((APIUpdateHostMonitorMsg) msg);
        }
        return msg;
    }

    //-----------------------------------------------------VALIDATE-HOST------------------------------------------------
    private void validate(APICreateHostMsg msg){
        //判断code是否已经存在
        SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
        q.add(HostVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if(q.isExists()){
            throw new ApiMessageInterceptionException(argerr("host's code %s is already exist ",msg.getCode()));
        }
    }

    private void validate(APIUpdateHostMsg msg){
        //判断所修改的监控机是否存在
        SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
        q.add(HostVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("host %s is not exist ",msg.getUuid()));
        }
        //判断code是否已经存在
        if(msg.getCode() != null){
            SimpleQuery<HostVO> q2 = dbf.createQuery(HostVO.class);
            q2.add(HostVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q2.add(HostVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if(q2.isExists()){
                throw new ApiMessageInterceptionException(argerr("host's code %s is already exist ",msg.getCode()));
            }
        }

    }

    private void validate(APICreateHostMonitorMsg msg){

        //判断所属监控机是否存在
        SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
        q.add(HostVO_.uuid, SimpleQuery.Op.EQ, msg.getHostUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("host %s is not exist ",msg.getHostUuid()));
        }
        //判断所选择的交换机端口是否存在
        SimpleQuery<SwitchPortVO> q2 = dbf.createQuery(SwitchPortVO.class);
        q2.add(SwitchPortVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchPortUuid());
        if (!q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switchPort %s is not exist ",msg.getSwitchPortUuid()));
        }
    }

    private void validate(APIUpdateHostMonitorMsg msg){
        //判断所修改的监控机交换机是否存在
        SimpleQuery<HostMonitorVO> q = dbf.createQuery(HostMonitorVO.class);
        q.add(HostMonitorVO_.uuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("HostSwitchMonitor %s is not exist ",msg.getUuid()));
        }
        //判断所属监控机是否存在
        SimpleQuery<HostVO> q2 = dbf.createQuery(HostVO.class);
        q2.add(HostVO_.uuid, SimpleQuery.Op.EQ, msg.getHostUuid());
        if (!q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("host %s is not exist ",msg.getHostUuid()));
        }
        //判断所选择的交换机端口是否存在
        if(msg.getSwitchPortUuid() != null){
            SimpleQuery<SwitchPortVO> q3 = dbf.createQuery(SwitchPortVO.class);
            q3.add(SwitchPortVO_.uuid, SimpleQuery.Op.EQ, msg.getSwitchPortUuid());
            if (!q3.isExists()) {
                throw new ApiMessageInterceptionException(argerr("switchPort %s is not exist ",msg.getSwitchPortUuid()));
            }
        }

    }
}
