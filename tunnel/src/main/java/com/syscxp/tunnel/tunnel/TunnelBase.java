package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.*;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.monitor.TunnelMonitorVO;
import com.syscxp.header.tunnel.monitor.TunnelMonitorVO_;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO_;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.tunnel.job.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create by DCY on 2017/12/1
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelBase {
    private static final CLogger logger = Utils.getLogger(TunnelBase.class);

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private JobQueueFacade jobf;

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
        dbf.getEntityManager().persist(tsvoB);
        dbf.getEntityManager().persist(tsvoC);
    }

    /**
     * 修改接口类型
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
     * 删除Tunnel数据库及其关联
     */
    @Transactional
    public void deleteTunnel(TunnelVO vo) {
        dbf.remove(vo);

        //删除对应的 TunnelSwitchPortVO 和 QingqVO
        SimpleQuery<TunnelSwitchPortVO> q = dbf.createQuery(TunnelSwitchPortVO.class);
        q.add(TunnelSwitchPortVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<TunnelSwitchPortVO> tivList = q.list();
        if (tivList.size() > 0) {
            for (TunnelSwitchPortVO tiv : tivList) {
                dbf.remove(tiv);
            }
        }
        SimpleQuery<QinqVO> q2 = dbf.createQuery(QinqVO.class);
        q2.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, vo.getUuid());
        List<QinqVO> qinqList = q2.list();
        if (qinqList.size() > 0) {
            for (QinqVO qv : qinqList) {
                dbf.remove(qv);
            }
        }
        UpdateQuery.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid,vo.getUuid()).delete();
    }

    /**
     * 根据 switchPort 找出所属的 PhysicalSwitch
     */
    public PhysicalSwitchVO getPhysicalSwitch(SwitchPortVO switchPortVO) {
        SwitchVO switchVO = dbf.findByUuid(switchPortVO.getSwitchUuid(), SwitchVO.class);
        return dbf.findByUuid(switchVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
    }

    /**
     * 根据 tunnelSwitchPortVO 获取对端MPLS交换机
     */
    public PhysicalSwitchVO getRemotePhysicalSwitch(TunnelSwitchPortVO tunnelSwitchPortVO) {
        SwitchPortVO switchPortVO = dbf.findByUuid(tunnelSwitchPortVO.getSwitchPortUuid(), SwitchPortVO.class);
        PhysicalSwitchVO physicalSwitchVO = getPhysicalSwitch(switchPortVO);
        if (physicalSwitchVO.getType() == PhysicalSwitchType.SDN) {   //SDN接入
            //找到SDN交换机的上联传输交换机
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO = Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid, physicalSwitchVO.getUuid())
                    .find();
            physicalSwitchVO = dbf.findByUuid(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid(), PhysicalSwitchVO.class);
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

        String sql = "SELECT t FROM PortOfferingVO t WHERE t.uuid IN (" +
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
    public List<SwitchPortVO> getSwitchPortByType(String endpointUuid, String type, Integer start, Integer limit) {
        String sql = "SELECT sp FROM SwitchPortVO sp, SwitchVO s WHERE sp.switchUuid=s.uuid " +
                "AND s.endpointUuid = :endpointUuid AND s.state=:switchState AND sp.state=:state AND sp.portType = :portType " +
                "AND sp.uuid not in (select switchPortUuid from InterfaceVO i where i.endpointUuid=:endpointUuid) ";
        return SQL.New(sql)
                .param("state", SwitchPortState.Enabled)
                .param("switchState", SwitchState.Enabled)
                .param("portType", type)
                .param("endpointUuid", endpointUuid)
                .offset(start != null ? start : 0)
                .limit(limit != null ? limit : 10)
                .list();
    }

    /**
     * 通过端口获取物理交换机的管理IP
     */
    public String getPhysicalSwitchMip(String switchPortUuid) {
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

    /**
     * 修改端口和物理接口是否跨了物理交换机
     */
    public boolean isChangeSwitchCauseUpdatePort(String oldSwitchPortUuid,String newSwitchPortUuid){
        SwitchPortVO oldPort = dbf.findByUuid(oldSwitchPortUuid,SwitchPortVO.class);
        SwitchVO oldSwitch = dbf.findByUuid(oldPort.getSwitchUuid(),SwitchVO.class);

        SwitchPortVO newPort = dbf.findByUuid(newSwitchPortUuid,SwitchPortVO.class);
        SwitchVO newSwitch = dbf.findByUuid(newPort.getSwitchUuid(),SwitchVO.class);

        if(oldSwitch.getPhysicalSwitchUuid().equals(newSwitch.getPhysicalSwitchUuid())){
            return false;
        }else{
            return true;
        }
    }

    /************************************************Tunnel Job*****************************************/

    public void deleteTunnelJob(TunnelVO vo,String queueName){
        if(vo.getMonitorState() == TunnelMonitorState.Enabled){
            if(vo.getState() == TunnelState.Enabled){
                logger.info("删除通道成功，并创建任务：StopMonitorJob");
                StopMonitorJob job = new StopMonitorJob();
                job.setTunnelUuid(vo.getUuid());
                jobf.execute(queueName+"-停止监控", Platform.getManagementServerId(), job);
            }

            logger.info("删除通道成功，并创建任务：DeleteIcmpJob");
            DeleteIcmpJob job3 = new DeleteIcmpJob();
            job3.setTunnelUuid(vo.getUuid());
            jobf.execute(queueName+"-删除ICMP", Platform.getManagementServerId(), job3);

        }
        logger.info("删除通道成功，并创建任务：DeleteResourcePolicyRefJob");
        DeleteResourcePolicyRefJob job2 = new DeleteResourcePolicyRefJob();
        job2.setTunnelUuid(vo.getUuid());
        jobf.execute(queueName+"-策略同步", Platform.getManagementServerId(), job2);
    }

    public void updateInterfacePortsJob(APIUpdateInterfacePortMsg msg,String queueName){
        //首先判断该物理接口有没有开通专线
        boolean isBeUsedForTunnel = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.interfaceUuid,msg.getUuid())
                .isExists();
        if(msg.isIssue() && isBeUsedForTunnel){
            String tunnelUuid = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.interfaceUuid,msg.getUuid())
                    .select(TunnelSwitchPortVO_.tunnelUuid)
                    .findValue();
            InterfaceVO interfaceVO = dbf.findByUuid(msg.getUuid(),InterfaceVO.class);
            TunnelVO vo = dbf.findByUuid(tunnelUuid,TunnelVO.class);
            boolean isChangeSwitch = isChangeSwitchCauseUpdatePort(interfaceVO.getSwitchPortUuid(),msg.getSwitchPortUuid());
            if(isChangeSwitch){
                if(vo.getMonitorState() == TunnelMonitorState.Enabled){
                    logger.info("修改端口成功，并创建任务：ModifyMonitorJob");
                    ModifyMonitorJob job = new ModifyMonitorJob();
                    job.setTunnelUuid(vo.getUuid());
                    jobf.execute(queueName+"-更新监控", Platform.getManagementServerId(), job);

                    logger.info("修改端口成功，并创建任务：UpdateIcmpJob");
                    UpdateIcmpJob job3 = new UpdateIcmpJob();
                    job3.setTunnelUuid(vo.getUuid());
                    jobf.execute(queueName+"-更新ICMP", Platform.getManagementServerId(), job3);
                }

                logger.info("修改端口成功，并创建任务：UpdateTunnelInfoForFalconJob");
                UpdateTunnelInfoForFalconJob job2 = new UpdateTunnelInfoForFalconJob();
                job2.setTunnelUuid(vo.getUuid());
                job2.setBandwidth(vo.getBandwidth());
                TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                        .eq(TunnelSwitchPortVO_.sortTag, "A")
                        .find();
                TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                        .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                        .eq(TunnelSwitchPortVO_.sortTag, "Z")
                        .find();
                job2.setSwitchAVlan(tunnelSwitchPortA.getVlan());
                job2.setSwitchBVlan(tunnelSwitchPortZ.getVlan());
                job2.setSwitchAIp(getPhysicalSwitchMip(tunnelSwitchPortA.getSwitchPortUuid()));
                job2.setSwitchBIp(getPhysicalSwitchMip(tunnelSwitchPortZ.getSwitchPortUuid()));
                job2.setAccountUuid(vo.getOwnerAccountUuid());
                jobf.execute(queueName+"-策略同步", Platform.getManagementServerId(), job2);
            }
        }
    }

    public void updateTunnelVlanOrInterfaceJob(TunnelVO vo,APIUpdateTunnelVlanMsg msg,String queueName){

        String oldSwitchPortUuidA = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid,msg.getOldInterfaceAUuid())
                .select(InterfaceVO_.switchPortUuid)
                .findValue();
        String newSwitchPortUuidA = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid,msg.getInterfaceAUuid())
                .select(InterfaceVO_.switchPortUuid)
                .findValue();
        String oldSwitchPortUuidZ = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid,msg.getOldInterfaceZUuid())
                .select(InterfaceVO_.switchPortUuid)
                .findValue();
        String newSwitchPortUuidZ = Q.New(InterfaceVO.class)
                .eq(InterfaceVO_.uuid,msg.getInterfaceZUuid())
                .select(InterfaceVO_.switchPortUuid)
                .findValue();
        boolean isChangeA = isChangeSwitchCauseUpdatePort(oldSwitchPortUuidA,newSwitchPortUuidA);
        boolean isChangeZ = isChangeSwitchCauseUpdatePort(oldSwitchPortUuidZ,newSwitchPortUuidZ);
        if((!Objects.equals(msg.getaVlan(), msg.getOldAVlan()) || !Objects.equals(msg.getzVlan(), msg.getOldZVlan()))
                || (isChangeA || isChangeZ)){

            if(vo.getMonitorState() == TunnelMonitorState.Enabled){
                logger.info("修改VLAN或物理接口成功，并创建任务：ModifyMonitorJob");
                ModifyMonitorJob job = new ModifyMonitorJob();
                job.setTunnelUuid(vo.getUuid());
                jobf.execute(queueName+"-更新监控", Platform.getManagementServerId(), job);

                logger.info("修改VLAN或物理接口成功，并创建任务：UpdateIcmpJob");
                UpdateIcmpJob job3 = new UpdateIcmpJob();
                job3.setTunnelUuid(vo.getUuid());
                jobf.execute(queueName+"-更新ICMP", Platform.getManagementServerId(), job3);
            }

            logger.info("修改VLAN或物理接口成功，并创建任务：UpdateTunnelInfoForFalconJob");
            UpdateTunnelInfoForFalconJob job2 = new UpdateTunnelInfoForFalconJob();
            job2.setTunnelUuid(vo.getUuid());
            job2.setBandwidth(vo.getBandwidth());
            TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "A")
                    .find();
            TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                    .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                    .eq(TunnelSwitchPortVO_.sortTag, "Z")
                    .find();
            job2.setSwitchAVlan(tunnelSwitchPortA.getVlan());
            job2.setSwitchBVlan(tunnelSwitchPortZ.getVlan());
            job2.setSwitchAIp(getPhysicalSwitchMip(tunnelSwitchPortA.getSwitchPortUuid()));
            job2.setSwitchBIp(getPhysicalSwitchMip(tunnelSwitchPortZ.getSwitchPortUuid()));
            job2.setAccountUuid(vo.getOwnerAccountUuid());
            jobf.execute(queueName+"-策略同步", Platform.getManagementServerId(), job2);
        }
    }

    public void updateTunnelBandwidthJob(TunnelVO vo,String queueName){
        if(vo.getMonitorState() == TunnelMonitorState.Enabled){
            logger.info("修改带宽成功，并创建任务：ModifyMonitorJob");
            ModifyMonitorJob job = new ModifyMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            jobf.execute(queueName+"-更新监控", Platform.getManagementServerId(), job);

            logger.info("修改带宽成功，并创建任务：UpdateIcmpJob");
            UpdateIcmpJob job3 = new UpdateIcmpJob();
            job3.setTunnelUuid(vo.getUuid());
            jobf.execute(queueName+"-更新ICMP", Platform.getManagementServerId(), job3);

        }
        logger.info("修改带宽成功，并创建任务：UpdateTunnelInfoForFalconJob");
        UpdateTunnelInfoForFalconJob job2 = new UpdateTunnelInfoForFalconJob();
        job2.setTunnelUuid(vo.getUuid());
        job2.setBandwidth(vo.getBandwidth());
        TunnelSwitchPortVO tunnelSwitchPortA = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "A")
                .find();
        TunnelSwitchPortVO tunnelSwitchPortZ = Q.New(TunnelSwitchPortVO.class)
                .eq(TunnelSwitchPortVO_.tunnelUuid, vo.getUuid())
                .eq(TunnelSwitchPortVO_.sortTag, "Z")
                .find();
        job2.setSwitchAVlan(tunnelSwitchPortA.getVlan());
        job2.setSwitchBVlan(tunnelSwitchPortZ.getVlan());
        job2.setSwitchAIp(getPhysicalSwitchMip(tunnelSwitchPortA.getSwitchPortUuid()));
        job2.setSwitchBIp(getPhysicalSwitchMip(tunnelSwitchPortZ.getSwitchPortUuid()));
        job2.setAccountUuid(vo.getOwnerAccountUuid());
        jobf.execute(queueName+"-策略同步", Platform.getManagementServerId(), job2);
    }

    public void enabledTunnelJob(TunnelVO vo,String queueName){
        if(vo.getMonitorState() == TunnelMonitorState.Enabled){
            logger.info("专线恢复连接成功，并创建任务：StartMonitorJob");
            StartMonitorJob job = new StartMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            jobf.execute(queueName+"-开启监控", Platform.getManagementServerId(), job);
        }
    }

    public void disabledTunnelJob(TunnelVO vo,String queueName){
        if(vo.getMonitorState() == TunnelMonitorState.Enabled) {
            logger.info("专线关闭连接成功，并创建任务：StopMonitorJob");
            StopMonitorJob job = new StopMonitorJob();
            job.setTunnelUuid(vo.getUuid());
            jobf.execute(queueName+"-停止监控", Platform.getManagementServerId(), job);
        }
    }
}
