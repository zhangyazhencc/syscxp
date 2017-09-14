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
import org.zstack.tunnel.header.endpoint.EndpointEO;
import org.zstack.tunnel.header.host.*;
import org.zstack.tunnel.header.monitor.*;
import org.zstack.tunnel.header.node.APIDeleteNodeEvent;
import org.zstack.tunnel.header.node.NodeVO;
import org.zstack.tunnel.header.node.NodeVO_;
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
        }else if(msg instanceof APIDeleteHostMsg){
            handle((APIDeleteHostMsg) msg);
        }else if(msg instanceof APICreateHostMonitorMsg){
            handle((APICreateHostMonitorMsg) msg);
        }else if(msg instanceof APIUpdateHostMonitorMsg){
            handle((APIUpdateHostMonitorMsg) msg);
        } else if(msg instanceof APICreateHostSwitchMonitorMsg){
            handle((APICreateHostSwitchMonitorMsg) msg);
        } else if(msg instanceof APIUpdateHostSwitchMonitorMsg){
            handle((APIUpdateHostSwitchMonitorMsg) msg);
        } else{
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APICreateHostMsg msg){
        HostVO vo = new HostVO();

        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setHostIp(msg.getHostIp());
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

        if(msg.getNodeUuid() != null){
            vo.setNodeUuid(msg.getNodeUuid());
            update = true;
        }
        if(msg.getName() != null){
            vo.setName(msg.getName());
            update = true;
        }
        if(msg.getCode() != null){
            vo.setCode(msg.getCode());
            update = true;
        }
        if(msg.getHostIp() != null){
            vo.setHostIp(msg.getHostIp());
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

    private void handle(APIDeleteHostMsg msg){
        String uuid = msg.getUuid();

        HostEO hostEO = dbf.findByUuid(uuid,HostEO.class);

        if (hostEO != null) {
            hostEO.setDeleted(1);
            dbf.update(hostEO);
        }

        APIDeleteHostEvent event = new APIDeleteHostEvent(msg.getId());
        bus.publish(event);
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

    private void handle(APICreateHostSwitchMonitorMsg msg){
        HostSwitchMonitorVO vo = new HostSwitchMonitorVO();

        vo.setUuid(Platform.getUuid());
        vo.setHostUuid(msg.getHostUuid());
        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
        vo.setInterfaceName(msg.getInterfaceName());

        vo = dbf.persistAndRefresh(vo);

        APICreateHostSwitchMonitorEvent event = new APICreateHostSwitchMonitorEvent(msg.getId());
        event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
        bus.publish(event);
    }

    private void handle(APIUpdateHostSwitchMonitorMsg msg) {
        HostSwitchMonitorVO vo = dbf.findByUuid(msg.getUuid(),HostSwitchMonitorVO.class);
        if(vo != null){
            boolean update = false;
            if(msg.getHostUuid() != null){
                vo.setHostUuid(msg.getHostUuid());
                update = true;
            }
            if(msg.getPhysicalSwitchUuid() != null){
                vo.setHostUuid(msg.getHostUuid());
                update = true;
            }
            if(msg.getPhysicalSwitchPortName() != null){
                vo.setPhysicalSwitchPortName(msg.getPhysicalSwitchPortName());
                update = true;
            }
            if(msg.getInterfaceName() != null){
                vo.setInterfaceName(msg.getInterfaceName());
                update = true;
            }

            if(update){
                vo = dbf.updateAndRefresh(vo);

                APIUpdateHostSwitchMonitorEvent event = new APIUpdateHostSwitchMonitorEvent(msg.getId());
                event.setInventory(HostSwitchMonitorInventory.valueOf(vo));
                bus.publish(event);
            }
        }else
            throw new IllegalArgumentException("UUID not exist!");
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
        }else if(msg instanceof APICreateHostSwitchMonitorMsg){
            validate((APICreateHostSwitchMonitorMsg) msg);
        }else if(msg instanceof APIUpdateHostSwitchMonitorMsg){
            validate((APICreateHostSwitchMonitorMsg) msg);
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
        //判断交换机所属节点是否存在
        if(msg.getNodeUuid() != null){
            SimpleQuery<NodeVO> q3 = dbf.createQuery(NodeVO.class);
            q3.add(NodeVO_.uuid, SimpleQuery.Op.EQ, msg.getNodeUuid());
            if (!q3.isExists()) {
                throw new ApiMessageInterceptionException(argerr("node %s is not exist ",msg.getNodeUuid()));
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

    private void validate(APICreateHostSwitchMonitorMsg msg){
        // check hostUuid
        checkHostExist(msg.getHostUuid());
        // check physicalSwitchUuid
        // TODO: sunxuelong 2017/9/11  check physicalSwitchUuid;
    }

    public void checkHostExist(String hostUuid){
        SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
        q.add(HostVO_.uuid, SimpleQuery.Op.EQ, hostUuid);
        if (!q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("host %s is not exist ",hostUuid));
        }
    }

}
