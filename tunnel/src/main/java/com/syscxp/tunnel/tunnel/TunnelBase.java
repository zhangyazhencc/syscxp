package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.*;
import com.syscxp.core.job.JobQueueFacade;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.tunnel.endpoint.EndpointType;
import com.syscxp.header.tunnel.endpoint.EndpointVO;
import com.syscxp.header.tunnel.monitor.SpeedTestTunnelVO;
import com.syscxp.header.tunnel.monitor.SpeedTestTunnelVO_;
import com.syscxp.header.tunnel.monitor.TunnelMonitorVO;
import com.syscxp.header.tunnel.monitor.TunnelMonitorVO_;
import com.syscxp.header.tunnel.node.NodeVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO;
import com.syscxp.header.tunnel.node.ZoneNodeRefVO_;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.tunnel.job.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.syscxp.core.Platform.argerr;

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
     * 获取云专线类型
     */
    public TunnelType getTunnelType(NodeVO nodeA, NodeVO nodeZ, String innerEndpointUuid){

        String zoneUuidA = getZoneUuid(nodeA.getUuid());
        String zoneUuidZ = getZoneUuid(nodeZ.getUuid());

        boolean isTransnational = false;
        if (nodeA.getCountry().equals("CHINA") && !nodeZ.getCountry().equals("CHINA")) {
            isTransnational = true;
        }
        if (!nodeA.getCountry().equals("CHINA") && nodeZ.getCountry().equals("CHINA")) {
            isTransnational = true;
        }

        if(isTransnational){    //跨国
            if(innerEndpointUuid == null){
                throw new IllegalArgumentException("该跨国专线互联连接点不能为空！");
            }
            EndpointVO endpointVO = dbf.findByUuid(innerEndpointUuid, EndpointVO.class);
            if(endpointVO.getEndpointType() == EndpointType.VIRTUAL){   //直通
                return TunnelType.CHINA1ABROAD;
            }else{                                                      //互联
                return TunnelType.CHINA2ABROAD;
            }
        }else{                  //国内互传 或 国外到国外
            if (nodeA.getCountry().equals("CHINA") && nodeZ.getCountry().equals("CHINA")) {  //国内互传
                if (nodeA.getCity().equals(nodeZ.getCity())) {  //同城
                    return TunnelType.CITY;
                } else if (zoneUuidA != null && zoneUuidZ != null && zoneUuidA.equals(zoneUuidZ)) { //同区域
                    return TunnelType.REGION;
                } else {                      //长传
                    return TunnelType.LONG;
                }
            } else {                                                                        //国外到国外
                return TunnelType.ABROAD;
            }
        }
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
     * 创建跨国的TunnelSwitchPort
     */
    public void createTunnelSwitchPortForAbroad(TunnelVO vo,String endpointUuid,String switchPortUuid,String remoteSwitchPortUuid,Integer vlan,String sortTag){
        PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPortUuid(switchPortUuid);
        PhysicalSwitchVO remotePhysicalSwitch = getPhysicalSwitchBySwitchPortUuid(remoteSwitchPortUuid);

        TunnelSwitchPortVO tsvo = new TunnelSwitchPortVO();

        tsvo.setUuid(Platform.getUuid());
        tsvo.setTunnelUuid(vo.getUuid());
        tsvo.setEndpointUuid(endpointUuid);
        tsvo.setInterfaceUuid(null);
        tsvo.setSwitchPortUuid(switchPortUuid);
        tsvo.setType(NetworkType.TRUNK);
        tsvo.setVlan(vlan);
        tsvo.setSortTag(sortTag);
        tsvo.setPhysicalSwitchUuid(physicalSwitch.getUuid());
        tsvo.setOwnerMplsSwitchUuid(getUplinkMplsSwitchByPhysicalSwitch(physicalSwitch).getUuid());
        tsvo.setPeerMplsSwitchUuid(getUplinkMplsSwitchByPhysicalSwitch(remotePhysicalSwitch).getUuid());

        dbf.getEntityManager().persist(tsvo);
    }

    /**
     * 创建TunnelSwitchPort
     */
    public void createTunnelSwitchPort(TunnelVO tunnelVO,InterfaceVO interfaceVO,String remoteSwitchPortUuid,Integer vlan,String sortTag,boolean isQinq){
        PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPortUuid(interfaceVO.getSwitchPortUuid());
        PhysicalSwitchVO remotePhysicalSwitch = getPhysicalSwitchBySwitchPortUuid(remoteSwitchPortUuid);

        TunnelSwitchPortVO tsvo = new TunnelSwitchPortVO();

        tsvo.setUuid(Platform.getUuid());
        tsvo.setTunnelUuid(tunnelVO.getUuid());
        tsvo.setInterfaceUuid(interfaceVO.getUuid());
        tsvo.setEndpointUuid(interfaceVO.getEndpointUuid());
        tsvo.setSwitchPortUuid(interfaceVO.getSwitchPortUuid());
        if(isQinq){
            tsvo.setType(NetworkType.QINQ);
        }else{
            tsvo.setType(interfaceVO.getType());
        }
        tsvo.setVlan(vlan);
        tsvo.setSortTag(sortTag);
        tsvo.setPhysicalSwitchUuid(physicalSwitch.getUuid());
        tsvo.setOwnerMplsSwitchUuid(getUplinkMplsSwitchByPhysicalSwitch(physicalSwitch).getUuid());
        tsvo.setPeerMplsSwitchUuid(getUplinkMplsSwitchByPhysicalSwitch(remotePhysicalSwitch).getUuid());

        dbf.getEntityManager().persist(tsvo);
    }

    /**
     * 查询物理接口所属的虚拟交换机
     * */
    public String findSwitchByInterface (String interfaceUuid){
        String sql = "select a.switchUuid from SwitchPortVO a,InterfaceVO b " +
                "where a.uuid = b.switchPortUuid " +
                "and b.uuid = :interfaceUuid";
        TypedQuery<String> sq = dbf.getEntityManager().createQuery(sql,String.class);
        sq.setParameter("interfaceUuid",interfaceUuid);
        return sq.getSingleResult();
    }

    /**
     * 找出SDN物理交换机的上联MPLS交换机，如果是MPLS直接返回
     * */
    public PhysicalSwitchVO getUplinkMplsSwitchByPhysicalSwitch(PhysicalSwitchVO physicalSwitch){
        if(physicalSwitch.getType() == PhysicalSwitchType.SDN){

            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitch.getUuid())
                    .find();
            return dbf.findByUuid(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid(),PhysicalSwitchVO.class);
        }else{
            return physicalSwitch;
        }
    }

    /**
     * 创建云专线时判断两端端口是否属于同一个物理交换机
     */
    public boolean isSamePhysicalSwitchForTunnel(SwitchPortVO switchPortA,SwitchPortVO switchPortZ){

        PhysicalSwitchVO physicalSwitchA = getPhysicalSwitch(switchPortA);
        PhysicalSwitchVO physicalSwitchZ = getPhysicalSwitch(switchPortZ);

        if(physicalSwitchA.getType() == PhysicalSwitchType.SDN && physicalSwitchZ.getType() == PhysicalSwitchType.SDN){
            //两端都是SDN接入
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVOA= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchA.getUuid())
                    .find();
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVOZ= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchZ.getUuid())
                    .find();
            if(physicalSwitchUpLinkRefVOA.getUplinkPhysicalSwitchUuid().equals(physicalSwitchUpLinkRefVOZ.getUplinkPhysicalSwitchUuid())){
                return true;
            }else{
                return false;
            }

        }else if(physicalSwitchA.getType() == PhysicalSwitchType.MPLS && physicalSwitchZ.getType() == PhysicalSwitchType.MPLS){
            //两端都是MPLS接入
            if(physicalSwitchA.getUuid().equals(physicalSwitchZ.getUuid())){
                return true;
            }else{
                return false;
            }
        }else{
            //一端SDN,一端MPLS，则应该拿SDN上联的MPLS去和另一端MPLS判断
            if(physicalSwitchA.getType() == PhysicalSwitchType.SDN){
                //A是SDN，Z是MPLS
                PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchA.getUuid())
                        .find();
                if(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid().equals(physicalSwitchZ.getUuid())){
                    return true;
                }else{
                    return false;
                }
            }else{
                //A是MPLS,Z是SDN
                PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchZ.getUuid())
                        .find();
                if(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid().equals(physicalSwitchA.getUuid())){
                    return true;
                }else{
                    return false;
                }
            }

        }
    }

    public String[] getSamePhysicalSwitchUuidForControl(String switchPortUuidA,String switchPortUuidZ){
        PhysicalSwitchVO physicalSwitchA = getPhysicalSwitch(dbf.findByUuid(switchPortUuidA,SwitchPortVO.class));
        PhysicalSwitchVO physicalSwitchZ = getPhysicalSwitch(dbf.findByUuid(switchPortUuidZ,SwitchPortVO.class));

        if(physicalSwitchA.getType() == PhysicalSwitchType.SDN && physicalSwitchZ.getType() == PhysicalSwitchType.SDN){
            //两端都是SDN接入
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVOA= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchA.getUuid())
                    .find();
            PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVOZ= Q.New(PhysicalSwitchUpLinkRefVO.class)
                    .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchZ.getUuid())
                    .find();
            if(physicalSwitchUpLinkRefVOA.getUplinkPhysicalSwitchUuid().equals(physicalSwitchUpLinkRefVOZ.getUplinkPhysicalSwitchUuid())){
                return new String[]{physicalSwitchUpLinkRefVOA.getUplinkPhysicalSwitchUuid()};
            }else{
                return new String[]{""};
            }

        }else if(physicalSwitchA.getType() == PhysicalSwitchType.MPLS && physicalSwitchZ.getType() == PhysicalSwitchType.MPLS){
            //两端都是MPLS接入
            if(physicalSwitchA.getUuid().equals(physicalSwitchZ.getUuid())){
                return new String[]{physicalSwitchA.getUuid()};
            }else{
                return new String[]{""};
            }
        }else{
            //一端SDN,一端MPLS，则应该拿SDN上联的MPLS去和另一端MPLS判断
            if(physicalSwitchA.getType() == PhysicalSwitchType.SDN){
                //A是SDN，Z是MPLS
                PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchA.getUuid())
                        .find();
                if(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid().equals(physicalSwitchZ.getUuid())){
                    return new String[]{physicalSwitchZ.getUuid()};
                }else{
                    return new String[]{""};
                }
            }else{
                //A是MPLS,Z是SDN
                PhysicalSwitchUpLinkRefVO physicalSwitchUpLinkRefVO= Q.New(PhysicalSwitchUpLinkRefVO.class)
                        .eq(PhysicalSwitchUpLinkRefVO_.physicalSwitchUuid,physicalSwitchZ.getUuid())
                        .find();
                if(physicalSwitchUpLinkRefVO.getUplinkPhysicalSwitchUuid().equals(physicalSwitchA.getUuid())){
                    return new String[]{physicalSwitchA.getUuid()};
                }else{
                    return new String[]{""};
                }
            }

        }
    }

    /**
     * 创建云专线的新购物理接口-共享端口
     */
    public InterfaceVO createInterfaceByTunnel(String endpointUuid,APICreateTunnelMsg msg){
        TunnelStrategy ts = new TunnelStrategy();
        EndpointVO endpointVO = dbf.findByUuid(endpointUuid, EndpointVO.class);
        InterfaceVO interfaceVO = new InterfaceVO();

        String switchPortUuid = ts.getSwitchPortByStrategy(msg.getAccountUuid(), endpointUuid, "SHARE");
        if (switchPortUuid == null) {
            throw new ApiMessageInterceptionException(argerr("该连接点[%s]下无可用的共享端口",endpointUuid));
        }
        interfaceVO.setUuid(Platform.getUuid());
        interfaceVO.setAccountUuid(msg.getAccountUuid());
        interfaceVO.setOwnerAccountUuid(msg.getAccountUuid());
        interfaceVO.setName(endpointVO.getName() + "_共享接口_" + Platform.getUuid().substring(0, 6));
        interfaceVO.setEndpointUuid(endpointUuid);
        interfaceVO.setSwitchPortUuid(switchPortUuid);
        interfaceVO.setType(NetworkType.TRUNK);
        interfaceVO.setDuration(msg.getDuration());
        interfaceVO.setProductChargeModel(msg.getProductChargeModel());
        interfaceVO.setDescription(null);
        interfaceVO.setState(InterfaceState.Up);
        interfaceVO.setMaxModifies(CoreGlobalProperty.INTERFACE_MAX_MOTIFIES);
        interfaceVO.setExpireDate(null);

        dbf.getEntityManager().persist(interfaceVO);
        return interfaceVO;
    }

    /**
     * 判断云专线的物理接口是否为共享端口
     */
    public boolean isShareForInterface(String interfaceUuid){
        boolean isShare = false;
        if(interfaceUuid == null){
            isShare =  true;
        }else{
            String portType = getPortTypeByInterface(interfaceUuid);
            if(portType.equals("SHARE")){
                isShare =  true;
            }
        }

        return isShare;
    }

    /**
     * 根据接口获得端口类型
     * */
    public String getPortTypeByInterface(String interfaceUuid){
        InterfaceVO interfaceVO = dbf.findByUuid(interfaceUuid,InterfaceVO.class);
        return  Q.New(SwitchPortVO.class)
                .eq(SwitchPortVO_.uuid, interfaceVO.getSwitchPortUuid())
                .select(SwitchPortVO_.portType).findValue();
    }

    /**
     * 判断该物理接口是否收费
     * */
    public boolean isFreeForInterface(String interfaceUuid){
        boolean isFree = false;

        String portType = getPortTypeByInterface(interfaceUuid);
        if(portType.equals("SHARE") || portType.equals("EXTENDPORT")){
            isFree =  true;
        }

        return isFree;
    }

    /**
     * 删除Tunnel数据库及其关联
     */
    public void deleteTunnelDB(TunnelVO vo) {
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
        //删除外采资源表
        if(Q.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, vo.getUuid()).isExists()){
            UpdateQuery.New(OutsideResourceVO.class).eq(OutsideResourceVO_.resourceUuid, vo.getUuid()).delete();
        }
        //删除Monitor和速度测试
        UpdateQuery.New(TunnelMonitorVO.class).eq(TunnelMonitorVO_.tunnelUuid, vo.getUuid()).delete();
        UpdateQuery.New(SpeedTestTunnelVO.class).eq(SpeedTestTunnelVO_.tunnelUuid, vo.getUuid()).delete();
        //删除续费表
        logger.info("删除通道成功，并创建任务：DeleteRenewVOAfterDeleteResourceJob");
        DeleteRenewVOAfterDeleteResourceJob job = new DeleteRenewVOAfterDeleteResourceJob();
        job.setAccountUuid(vo.getOwnerAccountUuid());
        job.setResourceType(vo.getClass().getSimpleName());
        job.setResourceUuid(vo.getUuid());
        jobf.execute("删除专线-删除续费表", Platform.getManagementServerId(), job);
    }

    /**
     * 根据 switchPort 找出所属的 PhysicalSwitch
     */
    public PhysicalSwitchVO getPhysicalSwitch(SwitchPortVO switchPortVO) {
        SwitchVO switchVO = dbf.findByUuid(switchPortVO.getSwitchUuid(), SwitchVO.class);
        return dbf.findByUuid(switchVO.getPhysicalSwitchUuid(), PhysicalSwitchVO.class);
    }

    public PhysicalSwitchVO getPhysicalSwitchBySwitchPortUuid(String switchPortUuid){
        return getPhysicalSwitch(dbf.findByUuid(switchPortUuid,SwitchPortVO.class));
    }

    /**
     * 通过连接点获取可用的端口规格
     */
    public List<PortOfferingVO> getPortTypeByEndpoint(String endpointUuid) {

        String sql = "SELECT t FROM PortOfferingVO t WHERE t.uuid IN (" +
                "select DISTINCT sp.portType from SwitchPortVO sp, SwitchVO s where sp.switchUuid=s.uuid " +
                "and s.endpointUuid=:endpointUuid and s.state=:switchState and sp.state=:state and (sp.portType=:portType1 or sp.portType=:portType2 " +
                "or sp.uuid not in (select switchPortUuid from InterfaceVO i where i.endpointUuid=:endpointUuid))) ";
        return SQL.New(sql)
                .param("state", SwitchPortState.Enabled)
                .param("switchState", SwitchState.Enabled)
                .param("endpointUuid", endpointUuid)
                .param("portType1", "SHARE")
                .param("portType2", "EXTENDPORT")
                .list();
    }

    /**
     * 通过连接点和端口规格获取可用的端口
     */
    public List<SwitchPortVO> getSwitchPortByType(String accountUuid, String endpointUuid, String type, Integer start, Integer limit) {
        String sql = "SELECT sp FROM SwitchPortVO sp, SwitchVO s WHERE sp.switchUuid=s.uuid " +
                "AND s.endpointUuid = :endpointUuid AND s.state=:switchState AND sp.state=:state AND sp.portType = :portType " +
                "AND sp.uuid not in (select switchPortUuid from InterfaceVO i where i.endpointUuid=:endpointUuid ";
        if ("SHARE".equals(type) || "EXTENDPORT".equals(type)){
            sql = sql + "AND i.accountUuid = :accountUuid ";
        }
        sql = sql + ") ";

        SQL q = SQL.New(sql)
                .param("state", SwitchPortState.Enabled)
                .param("switchState", SwitchState.Enabled)
                .param("portType", type)
                .param("endpointUuid", endpointUuid)
                .offset(start != null ? start : 0)
                .limit(limit != null ? limit : 20);

        if ("SHARE".equals(type) || "EXTENDPORT".equals(type)){
            q.param("accountUuid", accountUuid);
        }
        return  q.list();
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
     * 判断是否需要调用控制器下发删除
     */
    public boolean isNeedControlDelete(TunnelState tunnelState){
        if(tunnelState == TunnelState.Unpaid || tunnelState ==TunnelState.Disabled){
            return false;
        }else{
            return true;
        }
    }

    /**
     * 退订成功后专线控制器相关操作
     */
    public TunnelVO doControlDeleteTunnelAfterUnsubcribeSuccess(TunnelVO vo){
        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();
        if(vo.getExpireDate() == null || vo.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now()))){
            vo.setExpireDate(dbf.getCurrentSqlTime());
        }
        vo.setAccountUuid(null);
        vo = dbf.updateAndRefresh(vo);

        if(isNeedControlDelete(vo.getState())){
            taskBase.taskDeleteTunnel(vo);
        }else{
            deleteTunnelDB(vo);

            taskBase.deleteTunnelForRelationJob(vo, "删除专线");
        }


        return vo;
    }

    /**
     * 退订成功后专线数据库相关操作
     */
    public TunnelVO doDeleteTunnelDBAfterUnsubcribeSuccess(TunnelVO vo){
        String commands = JSONObjectUtil.toJsonString(new TunnelControllerBase().getTunnelConfigInfo(vo, false));
        TunnelJobAndTaskBase taskBase = new TunnelJobAndTaskBase();

        if(vo.getExpireDate() == null || vo.getExpireDate().after(Timestamp.valueOf(LocalDateTime.now()))){
            vo.setExpireDate(dbf.getCurrentSqlTime());
        }
        vo.setAccountUuid(null);
        vo = dbf.updateAndRefresh(vo);

        deleteTunnelDB(vo);

        new TunnelJobAndTaskBase().taskDeleteTunnelZK(vo.getUuid(), vo.getOwnerAccountUuid(), commands);

        taskBase.deleteTunnelForRelationJob(vo, "删除专线");

        return vo;
    }

    /**
     * judge share point tunnel
     * @param tunnelVOA
     * @param tunnelVOB
     * @return
     */
    public boolean isSharePoint(TunnelVO tunnelVOA, TunnelVO tunnelVOB) {
        boolean isSharePoint = false;
        if (!tunnelVOA.getVsi().equals(tunnelVOB.getVsi()))
            return false;

        for (TunnelSwitchPortVO portA : tunnelVOA.getTunnelSwitchPortVOS()) {
            for (TunnelSwitchPortVO portB : tunnelVOB.getTunnelSwitchPortVOS()) {
                if (portA.getVlan().equals(portB.getVlan())) {
                    if (StringUtils.equals(portA.getSwitchPortUuid(), portB.getSwitchPortUuid()))
                        return true;
                }
            }
        }

        return isSharePoint;
    }
}
