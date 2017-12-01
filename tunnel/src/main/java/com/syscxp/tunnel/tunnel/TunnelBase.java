package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.*;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO_;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by DCY on 2017/12/1
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelBase {
    private static final CLogger logger = Utils.getLogger(TunnelBase.class);

    @Autowired
    private DatabaseFacade dbf;

    /**
     * 自动获取 VSI
     */
    public Integer getVsiAuto() {

        GLock glock = new GLock("maxvsi", 120);
        glock.lock();

        Integer vsi;
        String sql = "select max(vo.vsi) from TunnelVO vo";
        try {
            TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
            vsi = vq.getSingleResult();
            if (vsi == null) {
                vsi = CoreGlobalProperty.START_VSI;
            } else {
                vsi = vsi + 1;
            }

        } finally {
            glock.unlock();
        }
        return vsi;
    }

    /**
     * 创建云专线 支付成功创建下发任务
     */
    public TaskResourceVO newTaskResourceVO(TunnelVO vo, TaskType taskType) {
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setAccountUuid(vo.getOwnerAccountUuid());
        taskResourceVO.setResourceUuid(vo.getUuid());
        taskResourceVO.setResourceType(vo.getClass().getSimpleName());
        taskResourceVO.setTaskType(taskType);
        taskResourceVO.setBody(null);
        taskResourceVO.setResult(null);
        taskResourceVO.setStatus(TaskStatus.Preexecute);
        taskResourceVO = dbf.persistAndRefresh(taskResourceVO);
        return taskResourceVO;
    }

    /**
     * 创建云专线 如果跨国,将出海口设备添加至TunnelSwitchPort
     */
    public void createTunnelSwitchPortForAbroad(String innerConnectedEndpointUuid, TunnelVO vo, boolean isBInner) {
        TunnelStrategy ts = new TunnelStrategy();

        //通过互联连接点找到内联交换机和内联端口
        SwitchVO innerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.INNER)
                .find();
        SwitchPortVO innerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, innerSwitch.getUuid())
                .find();
        //通过互联连接点找到外联交换机和外联端口
        SwitchVO outerSwitch = Q.New(SwitchVO.class)
                .eq(SwitchVO_.endpointUuid, innerConnectedEndpointUuid)
                .eq(SwitchVO_.type, SwitchType.OUTER)
                .find();
        SwitchPortVO outerSwitchPort = Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.switchUuid, outerSwitch.getUuid())
                .find();
        //获取互联设备的VLAN
        Integer innerVlan = ts.getVlanBySwitch(innerSwitch.getUuid());

        TunnelSwitchPortVO tsvoB = new TunnelSwitchPortVO();
        TunnelSwitchPortVO tsvoC = new TunnelSwitchPortVO();

        if (isBInner) {
            tsvoB.setUuid(Platform.getUuid());
            tsvoB.setTunnelUuid(vo.getUuid());
            tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoB.setInterfaceUuid(null);
            tsvoB.setSwitchPortUuid(innerSwitchPort.getUuid());
            tsvoB.setType(NetworkType.TRUNK);
            tsvoB.setVlan(innerVlan);
            tsvoB.setSortTag("B");


            tsvoC.setUuid(Platform.getUuid());
            tsvoC.setTunnelUuid(vo.getUuid());
            tsvoC.setInterfaceUuid(null);
            tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoC.setSwitchPortUuid(outerSwitchPort.getUuid());
            tsvoC.setType(NetworkType.TRUNK);
            tsvoC.setVlan(innerVlan);
            tsvoC.setSortTag("C");

        } else {
            tsvoB.setUuid(Platform.getUuid());
            tsvoB.setTunnelUuid(vo.getUuid());
            tsvoB.setInterfaceUuid(null);
            tsvoB.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoB.setSwitchPortUuid(outerSwitchPort.getUuid());
            tsvoB.setType(NetworkType.TRUNK);
            tsvoB.setVlan(innerVlan);
            tsvoB.setSortTag("B");

            tsvoC.setUuid(Platform.getUuid());
            tsvoC.setTunnelUuid(vo.getUuid());
            tsvoC.setInterfaceUuid(null);
            tsvoC.setEndpointUuid(innerConnectedEndpointUuid);
            tsvoC.setSwitchPortUuid(innerSwitchPort.getUuid());
            tsvoC.setType(NetworkType.TRUNK);
            tsvoC.setVlan(innerVlan);
            tsvoC.setSortTag("C");

        }
        dbf.persistAndRefresh(tsvoB);
        dbf.persistAndRefresh(tsvoC);

    }

    /**
     *  修改接口类型
     */
    public void updateNetworkType(InterfaceVO iface, String tunnelUuid, NetworkType newType, List<InnerVlanSegment> segments) {
        UpdateQuery.New(InterfaceVO.class)
                .set(InterfaceVO_.type, newType)
                .eq(InterfaceVO_.uuid, iface.getUuid())
                .update();

        if (tunnelUuid == null)
            return;

        if (iface.getType() == NetworkType.QINQ)
            UpdateQuery.New(QinqVO.class).eq(QinqVO_.tunnelUuid, tunnelUuid).delete();

        if (newType == NetworkType.QINQ) {
            List<QinqVO> vos = new ArrayList<>();
            for (InnerVlanSegment segment : segments) {
                QinqVO qinq = new QinqVO();
                qinq.setUuid(Platform.getUuid());
                qinq.setTunnelUuid(tunnelUuid);
                qinq.setStartVlan(segment.getStartVlan());
                qinq.setEndVlan(segment.getEndVlan());
                vos.add(qinq);
            }
            dbf.persistCollection(vos);
        }
    }

    /**
     *  删除Tunnel数据库及其关联
     */
    @Transactional
    public void deleteTunnel(TunnelVO vo){
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

    /**根据 switchPort 找出所属的 PhysicalSwitch */
    public PhysicalSwitchVO getPhysicalSwitch(SwitchPortVO switchPortVO){
        SwitchVO switchVO = dbf.findByUuid(switchPortVO.getSwitchUuid(),SwitchVO.class);
        return dbf.findByUuid(switchVO.getPhysicalSwitchUuid(),PhysicalSwitchVO.class);
    }

    /**根据 tunnelSwitchPortVO 获取对端MPLS交换机 */
    public PhysicalSwitchVO getRemotePhysicalSwitch(TunnelSwitchPortVO tunnelSwitchPortVO){
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(),SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitch(switchPortVO);
        if(physicalSwitchVO.getType() == PhysicalSwitchType.SDN) {   //SDN接入
            //找到SDN交换机的上联传输交换机
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchVO.getUuid())
                    .find();
            physicalSwitchVO = dbf.findByUuid(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid(),PhysicalSwitchVO.class);
        }

        return physicalSwitchVO;
    }

    /**
     * 根据TunnelSwicth获取两端节点
     */
    public String getNodeUuid(TunnelVO vo, String sortTag) {
        TunnelSwitchPortVO tunnelSwitch = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, sortTag)
                .find();
        return dbf.findByUuid(tunnelSwitch.getEndpointUuid(), EndpointVO.class).getNodeUuid();
    }


    /**
     * 通过连接点获取可用的端口规格
     */
    public List<PortOfferingVO> getPortTypeByEndpoint(String endpointUuid) {

        String sql ="SELECT t FROM PortOfferingVO t WHERE t.uuid IN (" +
                "select DISTINCT sp.portType from SwitchPortVO sp, SwitchVO s where sp.switchUuid=s.uuid " +
                "and s.endpointUuid=:endpointUuid and s.state=:switchState and sp.state=:state " +
                "and sp.uuid not in (select switchPortUuid from InterfaceVO i where i.endpointUuid=:endpointUuid)) ";
        return SQL.New(sql)
                .param("state", SwitchPortState.Enabled)
                .param("switchState", SwitchState.Enabled)
                .param("endpointUuid", endpointUuid)
                .list();
    }

    /**
     * 通过连接点和端口规格获取可用的端口
     */
    public List<SwitchPortVO> getSwitchPortByType(String endpointUuid, String type) {
        String sql = "SELECT sp FROM SwitchPortVO sp, SwitchVO s WHERE sp.switchUuid=s.uuid " +
                "AND s.endpointUuid = :endpointUuid AND s.state=:switchState AND sp.state=:state AND sp.portType = :portType " +
                "AND sp.uuid not in (select switchPortUuid from InterfaceVO i where i.endpointUuid=:endpointUuid) ";
        return SQL.New(sql)
                .param("state", SwitchPortState.Enabled)
                .param("switchState", SwitchState.Enabled)
                .param("portType", type)
                .param("endpointUuid", endpointUuid)
                .list();
    }

    /**
     * 通过端口获取物理交换机的管理IP
     */
    public String getPhysicalSwitch(String switchPortUuid) {
        String switcUuid = Q.New(SwitchPortVO.class).
                eq(SwitchPortVO_.uuid, switchPortUuid)
                .select(SwitchPortVO_.switchUuid)
                .findValue();

        String physicalSwitchUuid = Q.New(SwitchVO.class).
                eq(SwitchVO_.uuid, switcUuid)
                .select(SwitchVO_.physicalSwitchUuid).findValue();

        String switchIp = Q.New(PhysicalSwitchVO.class).
                eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid).
                select(PhysicalSwitchVO_.mIP).findValue();

        if (switchIp == null)
            throw new IllegalArgumentException("获取物理交换机IP失败");

        return switchIp;
    }

    /**
     * 根据节点找到所属区域
     */
    public String getZoneUuid(String nodeUuid) {
        String zoneUuid = null;
        ZoneNodeRefVO zoneNodeRefVO = Q.New(ZoneNodeRefVO.class)
                .eq(ZoneNodeRefVO_.nodeUuid, nodeUuid)
                .find();
        if (zoneNodeRefVO != null) {
            zoneUuid = zoneNodeRefVO.getZoneUuid();
        }
        return zoneUuid;
    }
}
