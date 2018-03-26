package com.syscxp.tunnel.network;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.*;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.tunnel.network.*;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.TaskResourceVO;
import com.syscxp.header.tunnel.tunnel.TaskStatus;
import com.syscxp.header.tunnel.tunnel.TaskType;
import com.syscxp.tunnel.tunnel.TunnelStrategy;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.TypedQuery;
import java.util.List;

import static com.syscxp.core.Platform.argerr;

/**
 * Create by DCY on 2018/3/8
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class L3NetworkBase {
    private static final CLogger logger = Utils.getLogger(L3NetworkBase.class);

    @Autowired
    private DatabaseFacade dbf;

    /**
     * 自动获取 Index
     */
    public Integer getIndexForRoute(String l3EndpointUuid){

        Integer index = 10;

        if(Q.New(L3RouteVO.class)
                .eq(L3RouteVO_.l3EndpointUuid, l3EndpointUuid)
                .isExists()){

            List<Integer> allocationIndexs = Q.New(L3RouteVO.class)
                    .eq(L3RouteVO_.l3EndpointUuid, l3EndpointUuid)
                    .select(L3RouteVO_.indexNum)
                    .listValues();

            for(int i = 1; i <= 1000; i++){
                int x = i*10;
                if(!allocationIndexs.contains(x)){
                    index = x;
                    break;
                }
            }

            return index;
        }else{
            return index;
        }
    }

    /**
     * 自动获取 VID
     */
    public Integer getVidAuto() {

        GLock glock = new GLock("maxvid", 120);
        glock.lock();

        Integer vid;
        String sql = "select max(vo.vid) from L3NetworkVO vo";
        try {
            TypedQuery<Integer> vq = dbf.getEntityManager().createQuery(sql, Integer.class);
            vid = vq.getSingleResult();
            if (vid == null) {
                vid = CoreGlobalProperty.START_L3_VID;
            } else {
                vid = vid + 1;
            }

        } finally {
            glock.unlock();
        }
        return vid;
    }

    /**
     * 判断是否满足下发条件
     */
    public boolean isControllerReady(L3EndpointVO vo){
        boolean isReady = true;
        if(vo.getRemoteIp() == null){
            isReady = false;
        }

        if(vo.getLocalIP() == null){
            isReady = false;
        }

        if(vo.getNetmask() == null){
            isReady = false;
        }

        return isReady;

    }


    /**
     * 分配VLAN
     * */
    public Integer getVlanForL3(String switchPortUuid, String physicalSwitchUuid){
        TunnelStrategy ts = new TunnelStrategy();
        Integer vlan;

        //查询该虚拟交换机下所有的Vlan段
        SwitchPortVO switchPortVO = dbf.findByUuid(switchPortUuid, SwitchPortVO.class);
        List<SwitchVlanVO> vlanList = Q.New(SwitchVlanVO.class)
                .eq(SwitchVlanVO_.switchUuid, switchPortVO.getSwitchUuid())
                .eq(SwitchVlanVO_.type, SwitchVlanType.L3)
                .list();

        //查询该虚拟交换机所属物理交换机下已经分配的Vlan
        List<Integer> allocatedVlans = findAllocateVlanByPhysicalSwitch(physicalSwitchUuid);

        if(vlanList.isEmpty()){
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下未配置3层Vlan段，请联系系统管理员 "));
        }
        if(allocatedVlans.isEmpty()){
            return vlanList.get(0).getStartVlan();
        }else{
            vlan = ts.allocateVlan(vlanList, allocatedVlans);
            return vlan;
        }
    }

    /**
     * 查询该虚拟交换机所属物理交换机已经分配的Vlan
     * */
    public List<Integer> findAllocateVlanByPhysicalSwitch(String physicalSwitchUuid){

        String sql1 = "select distinct a.vlan from TunnelSwitchPortVO a " +
                "where a.ownerMplsSwitchUuid = :physicalSwitchUuid";
        TypedQuery<Integer> avq1 = dbf.getEntityManager().createQuery(sql1,Integer.class);
        avq1.setParameter("physicalSwitchUuid", physicalSwitchUuid);
        List<Integer> list1 = avq1.getResultList();

        String sql2 = "select distinct a.vlan from L3EndpointVO a " +
                "where a.physicalSwitchUuid = :physicalSwitchUuid";
        TypedQuery<Integer> avq2 = dbf.getEntityManager().createQuery(sql2,Integer.class);
        avq2.setParameter("physicalSwitchUuid", physicalSwitchUuid);
        List<Integer> list2 = avq2.getResultList();

        list1.addAll(list2);

        return list1;
    }

    /**
     * 删除连接点及其关联
     * */
    public void deleteL3EndpointDB(String l3EndpointUuid){
        UpdateQuery.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.uuid, l3EndpointUuid)
                .delete();
        UpdateQuery.New(L3RtVO.class)
                .eq(L3RtVO_.l3EndpointUuid, l3EndpointUuid)
                .delete();
        UpdateQuery.New(L3RouteVO.class)
                .eq(L3RouteVO_.l3EndpointUuid, l3EndpointUuid)
                .delete();

    }

    /**
     * 更新云网络连接点数量
     * */
    public void updateEndpointNum(String l3Networkuuid){

        Long num = Q.New(L3EndpointVO.class)
                .eq(L3EndpointVO_.l3NetworkUuid, l3Networkuuid)
                .count();

        UpdateQuery.New(L3NetworkVO.class)
                .set(L3NetworkVO_.endpointNum, num.intValue())
                .eq(L3NetworkVO_.uuid, l3Networkuuid)
                .update();

    }

    /**
     * 创建L3连接点下发任务
     */
    public TaskResourceVO newTaskResourceVO(L3EndpointVO vo, TaskType taskType) {
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        L3NetworkVO l3NetworkVO = dbf.findByUuid(vo.getL3NetworkUuid(), L3NetworkVO.class);

        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setAccountUuid(l3NetworkVO.getOwnerAccountUuid());
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
     * 创建L3连接点路由下发任务
     */
    public TaskResourceVO newTaskResourceVO(L3RouteVO vo, TaskType taskType) {
        TaskResourceVO taskResourceVO = new TaskResourceVO();
        L3EndpointVO l3EndpointVO = dbf.findByUuid(vo.getL3EndpointUuid(), L3EndpointVO.class);
        L3NetworkVO l3NetworkVO = dbf.findByUuid(l3EndpointVO.getL3NetworkUuid(), L3NetworkVO.class);

        taskResourceVO.setUuid(Platform.getUuid());
        taskResourceVO.setAccountUuid(l3NetworkVO.getOwnerAccountUuid());
        taskResourceVO.setResourceUuid(vo.getUuid());
        taskResourceVO.setResourceType(vo.getClass().getSimpleName());
        taskResourceVO.setTaskType(taskType);
        taskResourceVO.setBody(null);
        taskResourceVO.setResult(null);
        taskResourceVO.setStatus(TaskStatus.Preexecute);
        taskResourceVO = dbf.persistAndRefresh(taskResourceVO);
        return taskResourceVO;
    }
}
