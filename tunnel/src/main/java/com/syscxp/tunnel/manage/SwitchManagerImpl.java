package com.syscxp.tunnel.manage;

import com.syscxp.core.db.*;
import com.syscxp.tunnel.header.switchs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.EventFacade;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.componentloader.PluginRegistry;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.tunnel.header.monitor.HostSwitchMonitorVO;
import com.syscxp.tunnel.header.monitor.HostSwitchMonitorVO_;
import com.syscxp.tunnel.header.node.NodeVO;
import com.syscxp.tunnel.header.tunnel.InterfaceVO;
import com.syscxp.tunnel.header.tunnel.InterfaceVO_;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import static com.syscxp.core.Platform.argerr;

/**
 * Created by DCY on 2017-09-07
 */
public class SwitchManagerImpl extends AbstractService implements SwitchManager, ApiMessageInterceptor {

    private static final CLogger logger = Utils.getLogger(SwitchManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;


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
        if (msg instanceof APICreateSwitchModelMsg) {
            handle((APICreateSwitchModelMsg) msg);
        } else if (msg instanceof APIDeleteSwitchModelMsg) {
            handle((APIDeleteSwitchModelMsg) msg);
        } else if (msg instanceof APICreatePhysicalSwitchMsg) {
            handle((APICreatePhysicalSwitchMsg) msg);
        } else if (msg instanceof APIUpdatePhysicalSwitchMsg) {
            handle((APIUpdatePhysicalSwitchMsg) msg);
        } else if (msg instanceof APIDeletePhysicalSwitchMsg) {
            handle((APIDeletePhysicalSwitchMsg) msg);
        } else if (msg instanceof APICreateSwitchMsg) {
            handle((APICreateSwitchMsg) msg);
        } else if (msg instanceof APIUpdateSwitchMsg) {
            handle((APIUpdateSwitchMsg) msg);
        } else if (msg instanceof APIDeleteSwitchMsg) {
            handle((APIDeleteSwitchMsg) msg);
        } else if (msg instanceof APICreateSwitchPortMsg) {
            handle((APICreateSwitchPortMsg) msg);
        } else if (msg instanceof APIUpdateSwitchPortMsg) {
            handle((APIUpdateSwitchPortMsg) msg);
        } else if (msg instanceof APIDeleteSwitchPortMsg) {
            handle((APIDeleteSwitchPortMsg) msg);
        } else if (msg instanceof APICreateSwitchVlanMsg) {
            handle((APICreateSwitchVlanMsg) msg);
        } else if (msg instanceof APIDeleteSwitchVlanMsg) {
            handle((APIDeleteSwitchVlanMsg) msg);
        } else if (msg instanceof APIQuerySwitchPortAvailableMsg) {
            handle((APIQuerySwitchPortAvailableMsg) msg);
        } else if (msg instanceof APIQueryVlanUsedMsg) {
            handle((APIQueryVlanUsedMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIQueryVlanUsedMsg msg) {
        List<VlanUsedInventory> switchPortUsedInventoryList = new ArrayList<VlanUsedInventory>();
        APIQueryVlanUsedReply reply = new APIQueryVlanUsedReply();
        String sql = "select s.code, sp.portName, ts.vlan  " +
                "from SwitchVO s, SwitchPortVO sp, TunnelSwitchPortVO ts  " +
                "where  s.uuid = sp.switchUuid and sp.uuid = ts.switchPortUuid " +
                " and s.uuid = :uuid ORDER BY ts.createDate";

        TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tfq.setFirstResult(msg.getStart());
        tfq.setMaxResults(msg.getStart() + msg.getLimit());
        tfq.setParameter("uuid", msg.getUuid());

        List<Tuple> ts = tfq.getResultList();
        reply.setCount(ts.size());
        for (Tuple t : ts) {
            VlanUsedInventory inventory = new VlanUsedInventory();
            inventory.setCode(t.get(0, String.class));
            inventory.setPortName(t.get(1, String.class));
            switchPortUsedInventoryList.add(inventory);
        }

        reply.setInventories(switchPortUsedInventoryList);
        bus.reply(msg, reply);
    }

    @Transactional
    private void handle(APIQuerySwitchPortAvailableMsg msg) {
//        List<SwitchPortAvailableInventory> switchPortAvailableInventoryList = new ArrayList<SwitchPortAvailableInventory>();
        APIQuerySwitchPortAvailableReply reply = new APIQuerySwitchPortAvailableReply();

        String sql = "select sp from SwitchPortVO sp where sp.switchUuid = :switchUuid and sp.state = :state and " +
                "((select count(1) as num from InterfaceVO i where i.switchPortUuid = sp.uuid) = 0 or sp.portType = 'SHARE') ";

        boolean isPortName = false;
        if (msg.getPortName() != null) {
            sql = sql + "and sp.portName like :portName ";
            isPortName = true;
        }

        SQL q = SQL.New(sql, SwitchPortVO.class)
                .param("switchUuid", msg.getUuid())
                .param("state", SwitchPortState.Enabled)
                .offset(msg.getStart() != null ? msg.getStart() : 0)
                .limit(msg.getLimit() != null ? msg.getLimit() : 10);
        if (isPortName)
            q.param("portName", "%" + msg.getPortName() + "%");

        List<SwitchPortVO> switchPorts = q.list();
       /* List<Tuple> ts;
        if (msg.getPortName() != null) {
            String sql = "select s.code, sp.portName, sp.uuid from SwitchVO s, SwitchPortVO sp " +
                    "where s.uuid = sp.switchUuid and sp.state = :spstate " +
                    "and sp.uuid not in ( select switchPortUuid from InterfaceVO i ) " +
                    "and sp.portType <> 'SHARE' " +
                    "and s.uuid = :uuid and sp.portName like :portName " +
                    "UNION all " +
                    "select s.code, sp.portName, sp.uuid from SwitchVO s, SwitchPortVO sp " +
                    "where s.uuid = sp.switchUuid and sp.state = :spstate " +
                    "and sp.portType = 'SHARE' " +
                    "and s.uuid = :uuid and sp.portName like :portName ";

            TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);

            if (msg.getStart() != null && msg.getLimit() != null) {
                tfq.setFirstResult(msg.getStart());
                tfq.setMaxResults(msg.getStart() + msg.getLimit());
            }
            tfq.setParameter("uuid", msg.getUuid());
            tfq.setParameter("spstate", SwitchPortState.Enabled);
            tfq.setParameter("portName", "%" + msg.getPortName() + "%");

            ts = tfq.getResultList();
        } else {
            String sql = "select s.code, sp.portName, sp.uuid from SwitchVO s, SwitchPortVO sp " +
                    "where s.uuid = sp.switchUuid and sp.state = :spstate " +
                    "and sp.uuid not in ( select switchPortUuid from InterfaceVO i ) " +
                    "and sp.portType <> 'SHARE' " +
                    "and s.uuid = :uuid " +
                    "UNION all " +
                    "select s.code, sp.portName, sp.uuid from SwitchVO s, SwitchPortVO sp " +
                    "where s.uuid = sp.switchUuid and sp.state = :spstate " +
                    "and sp.portType = 'SHARE' " +
                    "and s.uuid = :uuid ";

            TypedQuery<Tuple> tfq = dbf.getEntityManager().createQuery(sql, Tuple.class);
            if (msg.getStart() != null && msg.getLimit() != null) {
                tfq.setFirstResult(msg.getStart());
                tfq.setMaxResults(msg.getStart() + msg.getLimit());
            }
            tfq.setParameter("uuid", msg.getUuid());
            tfq.setParameter("spstate", SwitchPortState.Enabled);
            ts = tfq.getResultList();


        }
        reply.setCount(ts.size());
        for (Tuple t : ts) {
            SwitchPortAvailableInventory inventory = new SwitchPortAvailableInventory();
            inventory.setCode(t.get(0, String.class));
            inventory.setPortName(t.get(1, String.class));
            inventory.setSwitchPortUuid(t.get(2, String.class));
            switchPortAvailableInventoryList.add(inventory);
        }*/
        reply.setCount(switchPorts.size());

        reply.setInventories(SwitchPortInventory.valueOf(switchPorts));
        bus.reply(msg, reply);
    }

    private void handle(APICreateSwitchModelMsg msg) {
        SwitchModelVO vo = new SwitchModelVO();

        vo.setUuid(Platform.getUuid());
        vo.setBrand(msg.getBrand());
        vo.setModel(msg.getModel());
        vo.setSubModel(msg.getSubModel());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchModelEvent evt = new APICreateSwitchModelEvent(msg.getId());
        evt.setInventory(SwitchModelInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchModelMsg msg) {
        String uuid = msg.getUuid();
        SwitchModelVO vo = dbf.findByUuid(uuid, SwitchModelVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteSwitchModelEvent evt = new APIDeleteSwitchModelEvent(msg.getId());
        evt.setInventory(SwitchModelInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APICreatePhysicalSwitchMsg msg) {

        PhysicalSwitchVO vo = new PhysicalSwitchVO();
        vo.setUuid(Platform.getUuid());
        vo.setNodeUuid(msg.getNodeUuid());
        vo.setSwitchModelUuid(msg.getSwitchModelUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setOwner(msg.getOwner());
        vo.setType(msg.getType());
        vo.setAccessType(msg.getAccessType());
        vo.setRack(msg.getRack());
        vo.setmIP(msg.getmIP());
        vo.setLocalIP(msg.getLocalIP());
        vo.setUsername(msg.getUsername());
        vo.setPassword(msg.getPassword());
        vo.setDescription(msg.getDescription());
        vo.setNode(dbf.findByUuid(msg.getNodeUuid(), NodeVO.class));
        vo.setSwitchModel(dbf.findByUuid(msg.getSwitchModelUuid(), SwitchModelVO.class));

        //如果为SDN接入交换机，则存入上联表
        if (msg.getAccessType() != null) {
            if (msg.getAccessType() != PhysicalSwitchAccessType.TRANSPORT && msg.getType() == PhysicalSwitchType.SDN) {
                PhysicalSwitchUpLinkRefVO voref = new PhysicalSwitchUpLinkRefVO();
                voref.setUuid(Platform.getUuid());
                voref.setPhysicalSwitchUuid(vo.getUuid());
                voref.setPortName(msg.getPortName());
                voref.setUplinkPhysicalSwitchUuid(msg.getUplinkPhysicalSwitchUuid());
                voref.setUplinkPhysicalSwitchPortName(msg.getUplinkPhysicalSwitchPortName());
                dbf.getEntityManager().persist(voref);
            }
        }

        dbf.getEntityManager().persist(vo);

        APICreatePhysicalSwitchEvent evt = new APICreatePhysicalSwitchEvent(msg.getId());
        evt.setInventory(PhysicalSwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdatePhysicalSwitchMsg msg) {
        PhysicalSwitchVO vo = dbf.findByUuid(msg.getUuid(), PhysicalSwitchVO.class);
        boolean update = false;

        if (msg.getCode() != null) {
            vo.setCode(msg.getCode());
            update = true;
        }
        if (msg.getName() != null) {
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getOwner() != null) {
            vo.setOwner(msg.getOwner());
            update = true;
        }
        if (msg.getRack() != null) {
            vo.setRack(msg.getRack());
            update = true;
        }
        if (msg.getmIP() != null) {
            vo.setmIP(msg.getmIP());
            update = true;
        }
        if (msg.getLocalIP() != null) {
            vo.setLocalIP(msg.getLocalIP());
            update = true;
        }
        if (msg.getUsername() != null) {
            vo.setUsername(msg.getUsername());
            update = true;
        }
        if (msg.getPassword() != null) {
            vo.setPassword(msg.getPassword());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdatePhysicalSwitchEvent evt = new APIUpdatePhysicalSwitchEvent(msg.getId());
        evt.setInventory(PhysicalSwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    @Transactional
    private void handle(APIDeletePhysicalSwitchMsg msg) {

        PhysicalSwitchVO vo = dbf.findByUuid(msg.getUuid(), PhysicalSwitchVO.class);
        dbf.remove(vo);

        //删除物理交换机对应的上联表
        SimpleQuery<PhysicalSwitchUpLinkRefVO> q = dbf.createQuery(PhysicalSwitchUpLinkRefVO.class);
        q.add(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        List<PhysicalSwitchUpLinkRefVO> psuList = q.list();
        if (psuList.size() > 0) {
            for (PhysicalSwitchUpLinkRefVO psu : psuList) {
                dbf.remove(psu);
            }
        }

        APIDeletePhysicalSwitchEvent evt = new APIDeletePhysicalSwitchEvent(msg.getId());
        evt.setInventory(PhysicalSwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchMsg msg) {
        SwitchVO vo = new SwitchVO();

        vo.setUuid(Platform.getUuid());
        vo.setEndpointUuid(msg.getEndpointUuid());
        vo.setCode(msg.getCode());
        vo.setName(msg.getName());
        vo.setType(msg.getType());
        vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
        vo.setState(SwitchState.Enabled);
        vo.setStatus(SwitchStatus.Connected);
        vo.setDescription(msg.getDescription());


        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchEvent evt = new APICreateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateSwitchMsg msg) {
        SwitchVO vo = dbf.findByUuid(msg.getUuid(), SwitchVO.class);
        boolean update = false;
        if (msg.getName() != null) {
            vo.setName(msg.getName());
            update = true;
        }
        if (msg.getCode() != null) {
            vo.setCode(msg.getCode());
            update = true;
        }
        if (msg.getType() != null) {
            vo.setType(msg.getType());
            update = true;
        }
        if (msg.getPhysicalSwitchUuid() != null) {
            vo.setPhysicalSwitchUuid(msg.getPhysicalSwitchUuid());
            update = true;
        }
        if (msg.getState() != null) {
            vo.setState(msg.getState());
            update = true;
        }
        if (msg.getStatus() != null) {
            vo.setStatus(msg.getStatus());
            update = true;
        }
        if (msg.getDescription() != null) {
            vo.setDescription(msg.getDescription());
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateSwitchEvent evt = new APIUpdateSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchMsg msg) {

        SwitchVO vo = dbf.findByUuid(msg.getUuid(), SwitchVO.class);
        dbf.remove(vo);

        APIDeleteSwitchEvent evt = new APIDeleteSwitchEvent(msg.getId());
        evt.setInventory(SwitchInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchPortMsg msg) {
        SwitchPortVO vo = new SwitchPortVO();

        vo.setUuid(Platform.getUuid());
        vo.setSwitchUuid(msg.getSwitchUuid());
        vo.setPortNum(null);
        vo.setPortName(msg.getPortName());
        vo.setPortType(msg.getPortType());
        vo.setPortAttribute(msg.getPortAttribute());
        vo.setAutoAllot(msg.getAutoAllot());
        vo.setState(SwitchPortState.Enabled);

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchPortEvent evt = new APICreateSwitchPortEvent(msg.getId());
        evt.setInventory(SwitchPortInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIUpdateSwitchPortMsg msg) {
        SwitchPortVO vo = dbf.findByUuid(msg.getUuid(), SwitchPortVO.class);
        boolean update = false;
        if (msg.getState() != null) {
            vo.setState(msg.getState());
            update = true;
        }

        if (update)
            vo = dbf.updateAndRefresh(vo);

        APIUpdateSwitchPortEvent evt = new APIUpdateSwitchPortEvent(msg.getId());
        evt.setInventory(SwitchPortInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchPortMsg msg) {
        String uuid = msg.getUuid();
        SwitchPortVO vo = dbf.findByUuid(uuid, SwitchPortVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteSwitchPortEvent evt = new APIDeleteSwitchPortEvent(msg.getId());
        evt.setInventory(SwitchPortInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APICreateSwitchVlanMsg msg) {
        SwitchVlanVO vo = new SwitchVlanVO();

        vo.setUuid(Platform.getUuid());
        vo.setSwitchUuid(msg.getSwitchUuid());
        vo.setStartVlan(msg.getStartVlan());
        vo.setEndVlan(msg.getEndVlan());

        vo = dbf.persistAndRefresh(vo);

        APICreateSwitchVlanEvent evt = new APICreateSwitchVlanEvent(msg.getId());
        evt.setInventory(SwitchVlanInventory.valueOf(vo));
        bus.publish(evt);
    }

    private void handle(APIDeleteSwitchVlanMsg msg) {
        String uuid = msg.getUuid();
        SwitchVlanVO vo = dbf.findByUuid(uuid, SwitchVlanVO.class);

        if (vo != null) {
            dbf.remove(vo);
        }

        APIDeleteSwitchVlanEvent evt = new APIDeleteSwitchVlanEvent(msg.getId());
        evt.setInventory(SwitchVlanInventory.valueOf(vo));
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
        return bus.makeLocalServiceId(SwitchConstant.SERVICE_ID);
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APICreateSwitchModelMsg) {
            validate((APICreateSwitchModelMsg) msg);
        } else if (msg instanceof APIDeleteSwitchModelMsg) {
            validate((APIDeleteSwitchModelMsg) msg);
        } else if (msg instanceof APICreatePhysicalSwitchMsg) {
            validate((APICreatePhysicalSwitchMsg) msg);
        } else if (msg instanceof APIUpdatePhysicalSwitchMsg) {
            validate((APIUpdatePhysicalSwitchMsg) msg);
        } else if (msg instanceof APIDeletePhysicalSwitchMsg) {
            validate((APIDeletePhysicalSwitchMsg) msg);
        } else if (msg instanceof APICreateSwitchMsg) {
            validate((APICreateSwitchMsg) msg);
        } else if (msg instanceof APIUpdateSwitchMsg) {
            validate((APIUpdateSwitchMsg) msg);
        } else if (msg instanceof APIDeleteSwitchMsg) {
            validate((APIDeleteSwitchMsg) msg);
        } else if (msg instanceof APICreateSwitchPortMsg) {
            validate((APICreateSwitchPortMsg) msg);
        } else if (msg instanceof APIUpdateSwitchPortMsg) {
            validate((APIUpdateSwitchPortMsg) msg);
        } else if (msg instanceof APIDeleteSwitchPortMsg) {
            validate((APIDeleteSwitchPortMsg) msg);
        } else if (msg instanceof APICreateSwitchVlanMsg) {
            validate((APICreateSwitchVlanMsg) msg);
        } else if (msg instanceof APIDeleteSwitchVlanMsg) {
            validate((APIDeleteSwitchVlanMsg) msg);
        }
        return msg;
    }

    private void validate(APICreateSwitchModelMsg msg) {
        //判断model是否已经存在
        if (msg.getSubModel() == null) {
            SimpleQuery<SwitchModelVO> q = dbf.createQuery(SwitchModelVO.class);
            q.add(SwitchModelVO_.model, SimpleQuery.Op.EQ, msg.getModel());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("switchModel %s is already exist ", msg.getModel()));
            }
        } else {
            SimpleQuery<SwitchModelVO> q = dbf.createQuery(SwitchModelVO.class);
            q.add(SwitchModelVO_.model, SimpleQuery.Op.EQ, msg.getModel());
            q.add(SwitchModelVO_.subModel, SimpleQuery.Op.EQ, msg.getSubModel());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("switchModel %s is already exist ", msg.getModel()));
            }
        }

    }

    private void validate(APIDeleteSwitchModelMsg msg) {
        //判断该型号是否被物理交换机使用
        SimpleQuery<PhysicalSwitchVO> q = dbf.createQuery(PhysicalSwitchVO.class);
        q.add(PhysicalSwitchVO_.switchModelUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,switchModel is being used!"));
        }

    }

    private void validate(APICreatePhysicalSwitchMsg msg) {
        //判断code是否已经存在
        SimpleQuery<PhysicalSwitchVO> q = dbf.createQuery(PhysicalSwitchVO.class);
        q.add(PhysicalSwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's code %s is already exist ", msg.getCode()));
        }
        //判断mIP和LocalIp唯一性
        SimpleQuery<PhysicalSwitchVO> q2 = dbf.createQuery(PhysicalSwitchVO.class);
        q2.add(PhysicalSwitchVO_.mIP, SimpleQuery.Op.EQ, msg.getmIP());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's mip %s is already exist ", msg.getmIP()));
        }
        SimpleQuery<PhysicalSwitchVO> q3 = dbf.createQuery(PhysicalSwitchVO.class);
        q3.add(PhysicalSwitchVO_.localIP, SimpleQuery.Op.EQ, msg.getLocalIP());
        if (q3.isExists()) {
            throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's localIp %s is already exist ", msg.getLocalIP()));
        }
        //如果是SDN接入
        if (msg.getAccessType() != null) {
            if (msg.getAccessType() != PhysicalSwitchAccessType.TRANSPORT && msg.getType() == PhysicalSwitchType.SDN) {
                //判断传输端口名称在一个物理交换机的业务端口下是否存在
                String sql = "select count(b.uuid) from SwitchVO a,SwitchPortVO b " +
                        "where a.uuid = b.switchUuid " +
                        "and b.portName = :portName " +
                        "and a.physicalSwitchUuid = :physicalSwitchUuid ";
                TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
                vq.setParameter("portName", msg.getUplinkPhysicalSwitchPortName());
                vq.setParameter("physicalSwitchUuid", msg.getUplinkPhysicalSwitchUuid());
                Long count = vq.getSingleResult();
                if (count > 0) {
                    throw new ApiMessageInterceptionException(argerr("portName %s is already exist ", msg.getUplinkPhysicalSwitchPortName()));
                }
                //判断传输端口名称在一个物理交换机的传输端口下是否存在
                SimpleQuery<PhysicalSwitchUpLinkRefVO> q4 = dbf.createQuery(PhysicalSwitchUpLinkRefVO.class);
                q4.add(PhysicalSwitchUpLinkRefVO_.uplinkPhysicalSwitchUuid, SimpleQuery.Op.EQ, msg.getUplinkPhysicalSwitchUuid());
                q4.add(PhysicalSwitchUpLinkRefVO_.uplinkPhysicalSwitchPortName, SimpleQuery.Op.EQ, msg.getUplinkPhysicalSwitchPortName());
                if (q4.isExists()) {
                    throw new ApiMessageInterceptionException(argerr("portName %s is already exist ", msg.getUplinkPhysicalSwitchPortName()));
                }
                //判断传输端口名称在一个物理交换机的监控端口下是否存在
                SimpleQuery<HostSwitchMonitorVO> q5 = dbf.createQuery(HostSwitchMonitorVO.class);
                q5.add(HostSwitchMonitorVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getUplinkPhysicalSwitchUuid());
                q5.add(HostSwitchMonitorVO_.physicalSwitchPortName, SimpleQuery.Op.EQ, msg.getUplinkPhysicalSwitchPortName());
                if (q5.isExists()) {
                    throw new ApiMessageInterceptionException(argerr("portName %s is already exist ", msg.getUplinkPhysicalSwitchPortName()));
                }
            }

        }


    }

    private void validate(APIUpdatePhysicalSwitchMsg msg) {

        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<PhysicalSwitchVO> q = dbf.createQuery(PhysicalSwitchVO.class);
            q.add(PhysicalSwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q.add(PhysicalSwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's code %s is already exist ", msg.getCode()));
            }
        }

        //判断mIP和LocalIp唯一性
        if (msg.getmIP() != null) {
            SimpleQuery<PhysicalSwitchVO> q2 = dbf.createQuery(PhysicalSwitchVO.class);
            q2.add(PhysicalSwitchVO_.mIP, SimpleQuery.Op.EQ, msg.getmIP());
            q2.add(PhysicalSwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q2.isExists()) {
                throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's mip %s is already exist ", msg.getmIP()));
            }
        }
        if (msg.getLocalIP() != null) {
            SimpleQuery<PhysicalSwitchVO> q3 = dbf.createQuery(PhysicalSwitchVO.class);
            q3.add(PhysicalSwitchVO_.localIP, SimpleQuery.Op.EQ, msg.getLocalIP());
            q3.add(PhysicalSwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q3.isExists()) {
                throw new ApiMessageInterceptionException(argerr("PhysicalSwitch's localIp %s is already exist ", msg.getLocalIP()));
            }
        }

    }

    private void validate(APIDeletePhysicalSwitchMsg msg) {

        //判断该物理交换机下是否有虚拟交换机
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,PhysicalSwitch is being used!"));
        }
        //判断该物理交换机下是否有监控
        SimpleQuery<HostSwitchMonitorVO> q2 = dbf.createQuery(HostSwitchMonitorVO.class);
        q2.add(HostSwitchMonitorVO_.physicalSwitchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,PhysicalSwitch is being used!"));
        }

    }

    private void validate(APICreateSwitchMsg msg) {
        //判断code是否已经存在
        SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
        q.add(SwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ", msg.getCode()));
        }
        //验证互联连接点下仅有2个逻辑交换机，且所属物理交换机不能是同一个
        if(msg.getType() != SwitchType.ACCESS){  //内联或者外联交换机
            SwitchVO switchInner = Q.New(SwitchVO.class)
                    .eq(SwitchVO_.type,SwitchType.INNER)
                    .eq(SwitchVO_.endpointUuid,msg.getEndpointUuid())
                    .find();
            SwitchVO switchOuter = Q.New(SwitchVO.class)
                    .eq(SwitchVO_.type,SwitchType.OUTER)
                    .eq(SwitchVO_.endpointUuid,msg.getEndpointUuid())
                    .find();
            if(msg.getType() == SwitchType.INNER){  //创建内联交换机
                if(switchInner != null){
                    throw new ApiMessageInterceptionException(argerr("该互联连接点下已经存在内联交换机!"));
                }else{
                    if(switchOuter != null && msg.getPhysicalSwitchUuid().equals(switchOuter.getPhysicalSwitchUuid())){
                        throw new ApiMessageInterceptionException(argerr("该互联连接点下内联/外联交换机所属的物理交换机不能是同一个!"));
                    }
                }
            }else{                                  //创建外链交换机
                if(switchOuter != null){
                    throw new ApiMessageInterceptionException(argerr("该互联连接点下已经存在外联交换机!"));
                }else{
                    if(switchInner != null && msg.getPhysicalSwitchUuid().equals(switchInner.getPhysicalSwitchUuid())){
                        throw new ApiMessageInterceptionException(argerr("该互联连接点下内联/外联交换机所属的物理交换机不能是同一个!"));
                    }
                }
            }
        }

    }

    private void validate(APIUpdateSwitchMsg msg) {
        //判断code是否已经存在
        if (msg.getCode() != null) {
            SimpleQuery<SwitchVO> q = dbf.createQuery(SwitchVO.class);
            q.add(SwitchVO_.code, SimpleQuery.Op.EQ, msg.getCode());
            q.add(SwitchVO_.uuid, SimpleQuery.Op.NOT_EQ, msg.getUuid());
            if (q.isExists()) {
                throw new ApiMessageInterceptionException(argerr("switch's code %s is already exist ", msg.getCode()));
            }
        }

    }

    private void validate(APIDeleteSwitchMsg msg) {

        //判断该交换机下是否有端口
        SimpleQuery<SwitchPortVO> q = dbf.createQuery(SwitchPortVO.class);
        q.add(SwitchPortVO_.switchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,Switch is being used by switchPort!"));
        }
        //判断该交换机下是否有Vlan段
        SimpleQuery<SwitchVlanVO> q2 = dbf.createQuery(SwitchVlanVO.class);
        q2.add(SwitchVlanVO_.switchUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q2.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,Switch is being used by switchVlan!"));
        }
    }


    private void validate(APICreateSwitchPortMsg msg) {

        //端口名称在一个物理交换机下是否存在
        String sql = "select count(a.uuid) from PhysicalSwitchVO a,SwitchVO b,SwitchPortVO c " +
                "where a.uuid = b.physicalSwitchUuid " +
                "and b.uuid = c.switchUuid " +
                "and c.portName = :portName " +
                "and a.uuid = (select physicalSwitchUuid from SwitchVO where uuid = :switchUuid)";
        TypedQuery<Long> vq = dbf.getEntityManager().createQuery(sql, Long.class);
        vq.setParameter("portName", msg.getPortName());
        vq.setParameter("switchUuid", msg.getSwitchUuid());
        Long count = vq.getSingleResult();
        if (count > 0) {
            throw new ApiMessageInterceptionException(argerr("portName %s is already exist ", msg.getPortName()));
        }
    }

    private void validate(APIUpdateSwitchPortMsg msg) {
    }

    private void validate(APIDeleteSwitchPortMsg msg) {

        //判断该端口是否被买了
        SimpleQuery<InterfaceVO> q = dbf.createQuery(InterfaceVO.class);
        q.add(InterfaceVO_.switchPortUuid, SimpleQuery.Op.EQ, msg.getUuid());
        if (q.isExists()) {
            throw new ApiMessageInterceptionException(argerr("cannot delete,switchPort is being used!"));
        }

    }

    private void validate(APICreateSwitchVlanMsg msg){

        if(msg.getStartVlan() > msg.getEndVlan()){
            throw new ApiMessageInterceptionException(argerr("endvlan must more than startvlan"));
        }

        TunnelStrategy ts = new TunnelStrategy();
        SwitchVO switchVO = dbf.findByUuid(msg.getSwitchUuid(),SwitchVO.class);
        PhysicalSwitchVO physicalSwitch = dbf.findByUuid(switchVO.getPhysicalSwitchUuid(),PhysicalSwitchVO.class);
        PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRef = Q.New(PhysicalSwitchUpLinkRefVO.class)
                .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitch.getUuid())
                .find();
        List<Integer> addIntegerList = ts.getVlanIntegerList(msg.getStartVlan(),msg.getEndVlan());
        if(physicalSwitch.getType() == PhysicalSwitchType.SDN){        //SDN接入交换机
            //找到上联MPLS交换机
            PhysicalSwitchVO uplinkPhysicalSwitch = dbf.findByUuid(physicalSwitchUpLinkRef.getUplinkPhysicalSwitchUuid(),PhysicalSwitchVO.class);
            if(uplinkPhysicalSwitch.getAccessType() == PhysicalSwitchAccessType.TRANSPORT){   //该上联MPLS只做传输
                List<SwitchVlanVO> switchVlans = ts.getSwitchVlansFromUplink(uplinkPhysicalSwitch.getUuid());
                if(!switchVlans.isEmpty()){
                    List<Integer> vlanIntegerList = ts.getVlanIntegerList(switchVlans);
                    if(ts.isMixed(vlanIntegerList,addIntegerList)){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }else{                                                                              //该上联MPLS 接入传输
                List<SwitchVlanVO> switchVlansT = ts.getSwitchVlansFromUplink(uplinkPhysicalSwitch.getUuid());
                List<SwitchVlanVO> switchVlansA = ts.getSwitchVlans(uplinkPhysicalSwitch.getUuid());
                if(!switchVlansT.isEmpty() || !switchVlansA.isEmpty()){
                    if(!switchVlansT.isEmpty()){
                        switchVlansA.addAll(switchVlansT);
                    }
                    List<Integer> vlanIntegerList = ts.getVlanIntegerList(switchVlansA);
                    if(ts.isMixed(vlanIntegerList,addIntegerList)){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }else{      //MPLS交换机
            if(physicalSwitch.getAccessType() == PhysicalSwitchAccessType.ACCESS){  //接入
                List<SwitchVlanVO> switchVlans = ts.getSwitchVlans(physicalSwitch.getUuid());
                if(!switchVlans.isEmpty()){
                    List<Integer> vlanIntegerList = ts.getVlanIntegerList(switchVlans);
                    if(ts.isMixed(vlanIntegerList,addIntegerList)){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }else{                                                                  //接入传输
                List<SwitchVlanVO> switchVlansT = ts.getSwitchVlansFromUplink(physicalSwitch.getUuid());
                List<SwitchVlanVO> switchVlansA = ts.getSwitchVlans(physicalSwitch.getUuid());
                if(!switchVlansT.isEmpty() || !switchVlansA.isEmpty()){
                    if(!switchVlansT.isEmpty()){
                        switchVlansA.addAll(switchVlansT);
                    }
                    List<Integer> vlanIntegerList = ts.getVlanIntegerList(switchVlansA);
                    if(ts.isMixed(vlanIntegerList,addIntegerList)){
                        throw new ApiMessageInterceptionException(argerr("vlan has overlapping"));
                    }
                }
            }
        }

    }

    private void validate(APIDeleteSwitchVlanMsg msg) {
        //判断该Vlan段有没有被使用
        SwitchVlanVO vo = dbf.findByUuid(msg.getUuid(), SwitchVlanVO.class);
        List<Integer> vlanList = fingAllocateVlanBySwitch(vo.getSwitchUuid());
        if (vlanList.size() > 0) {
            for (Integer vlan : vlanList) {
                if (vlan >= vo.getStartVlan() && vlan <= vo.getEndVlan()) {
                    throw new ApiMessageInterceptionException(argerr("cannot delete,switchVlan is being used!"));
                }
            }
        }
    }

    //查询该虚拟交换机下Tunnel已经分配的Vlan
    public List<Integer> fingAllocateVlanBySwitch(String switchUuid) {

        String sql = "select distinct a.vlan from TunnelSwitchPortVO a,SwitchPortVO b " +
                "where a.switchPortUuid = b.uuid " +
                "and b.switchUuid = :switchUuid ";
        TypedQuery<Integer> avq = dbf.getEntityManager().createQuery(sql, Integer.class);
        avq.setParameter("switchUuid", switchUuid);
        return avq.getResultList();
    }
}
