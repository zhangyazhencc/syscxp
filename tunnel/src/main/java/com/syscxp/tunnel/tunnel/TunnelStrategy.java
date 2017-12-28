package com.syscxp.tunnel.tunnel;

import com.syscxp.core.db.Q;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.InterfaceVO;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;

import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.syscxp.core.Platform.argerr;

/**
 * Created by DCY on 2017-09-18
 *
 * Tunnel策略生成类
 */

@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class TunnelStrategy  {

    private static final CLogger logger = Utils.getLogger(TunnelStrategy.class);

    @Autowired
    private DatabaseFacade dbf;

    //策略分配端口
    public String getSwitchPortByStrategy(String accountUuid, String endpointUuid , String portOfferingUuid){
        String switchPortUuid = null;
        if(portOfferingUuid.equals("SHARE")){        //共享端口
            //判断该用户在该连接点下是否已经购买共享口
            String sql = "select a from InterfaceVO a,SwitchPortVO b " +
                    "where a.switchPortUuid = b.uuid " +
                    "and a.accountUuid = :accountUuid " +
                    "and a.endpointUuid = :endpointUuid " +
                    "and b.portType = 'SHARE'";
            TypedQuery<InterfaceVO> itq = dbf.getEntityManager().createQuery(sql, InterfaceVO.class);
            itq.setParameter("accountUuid",accountUuid);
            itq.setParameter("endpointUuid",endpointUuid);
            if(!itq.getResultList().isEmpty()){
                throw new ApiMessageInterceptionException(argerr("一个用户在同一个连接点下只能购买一个共享口！ "));
            }

            String sql2 = "select c.uuid from SwitchVO b,SwitchPortVO c " +
                    "where b.uuid = c.switchUuid " +
                    "and b.endpointUuid = :endpointUuid " +
                    "and b.state = :switchState and b.status = :switchStatus " +
                    "and c.portType = :portType and c.state = :portState and c.autoAllot = :autoAllot ";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql2, String.class);
            vq.setParameter("endpointUuid",endpointUuid);
            vq.setParameter("switchState", SwitchState.Enabled);
            vq.setParameter("switchStatus", SwitchStatus.Connected);
            vq.setParameter("portType",portOfferingUuid);
            vq.setParameter("portState", SwitchPortState.Enabled);
            vq.setParameter("autoAllot", 1);
            List<String> portList = vq.getResultList();
            if(portList.size() > 0){
                Random r = new Random();
                switchPortUuid = portList.get(r.nextInt(portList.size()));
            }
        }else{    //独享端口
            String sql = "select c.uuid from SwitchVO b,SwitchPortVO c " +
                    "where b.uuid = c.switchUuid " +
                    "and b.endpointUuid = :endpointUuid " +
                    "and b.state = :switchState and b.status = :switchStatus " +
                    "and c.portType = :portType and c.state = :portState and c.autoAllot = :autoAllot " +
                    "and c.uuid not in (select switchPortUuid from InterfaceVO)";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",endpointUuid);
            vq.setParameter("switchState", SwitchState.Enabled);
            vq.setParameter("switchStatus", SwitchStatus.Connected);
            vq.setParameter("portType",portOfferingUuid);
            vq.setParameter("portState", SwitchPortState.Enabled);
            vq.setParameter("autoAllot", 1);
            List<String> portList = vq.getResultList();
            if(portList.size() > 0){
                Random r = new Random();
                switchPortUuid = portList.get(r.nextInt(portList.size()));
            }

        }

        return switchPortUuid;

    }

    //策略分配外部VLAN（通过物理接口）
    public Integer getVlanByStrategy(String interfaceUuid){
        Integer vlan = null;

        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuid = findSwitchByInterface(interfaceUuid);
        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanList = findSwitchVlanBySwitch(switchUuid);
        //查询该虚拟交换机所属物理交换机下已经分配的Vlan
        List<Integer> allocatedVlans = fingAllocateVlanBySwitch(switchUuid);

        if(vlanList.isEmpty()){
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下未配置VLAN，请联系系统管理员 "));
        }
        if(allocatedVlans.isEmpty()){
            return vlanList.get(0).getStartVlan();
        }else{
            vlan = allocateVlan(vlanList, allocatedVlans);
            return vlan;
        }
    }

    //策略分配外部VLAN（通过交换机）
    public Integer getVlanBySwitch(String switchUuid){
        Integer vlan = null;

        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanList = findSwitchVlanBySwitch(switchUuid);
        //查询该虚拟交换机所属物理交换机下已经分配的Vlan
        List<Integer> allocatedVlans = fingAllocateVlanBySwitch(switchUuid);

        if(vlanList.isEmpty()){
            throw new ApiMessageInterceptionException(argerr("该端口所属虚拟交换机下未配置VLAN，请联系系统管理员 "));
        }
        if(allocatedVlans.isEmpty()){
            return vlanList.get(0).getStartVlan();
        }else{
            vlan = allocateVlan(vlanList, allocatedVlans);
            return vlan;
        }
    }

    //查询物理接口所属的虚拟交换机
    public String findSwitchByInterface (String interfaceUuid){
        String sql = "select a.switchUuid from SwitchPortVO a,InterfaceVO b " +
                "where a.uuid = b.switchPortUuid " +
                "and b.uuid = :interfaceUuid";
        TypedQuery<String> sq = dbf.getEntityManager().createQuery(sql,String.class);
        sq.setParameter("interfaceUuid",interfaceUuid);
        return sq.getSingleResult();
    }

    //查询该虚拟交换机下所有的Vlan段
    public List<SwitchVlanVO> findSwitchVlanBySwitch (String switchUuid){
        String sql = "select a from SwitchVlanVO a where a.switchUuid = :switchUuid";
        TypedQuery<SwitchVlanVO> svq = dbf.getEntityManager().createQuery(sql, SwitchVlanVO.class);
        svq.setParameter("switchUuid",switchUuid);
        return svq.getResultList();
    }

    //查询该虚拟交换机所属物理交换机下Tunnel已经分配的Vlan
    public List<Integer> fingAllocateVlanBySwitch(String switchUuid){
        String physicalSwitchUuid = Q.New(SwitchVO.class)
                .eq(SwitchVO_.uuid,switchUuid)
                .select(SwitchVO_.physicalSwitchUuid)
                .findValue();

        String sql = "select distinct a.vlan from TunnelSwitchPortVO a,SwitchPortVO b,SwitchVO c " +
                "where a.switchPortUuid = b.uuid and b.switchUuid = c.uuid" +
                "and c.physicalSwitchUuid = :physicalSwitchUuid ";
        TypedQuery<Integer> avq = dbf.getEntityManager().createQuery(sql,Integer.class);
        avq.setParameter("physicalSwitchUuid",physicalSwitchUuid);
        return avq.getResultList();
    }

    //分配可用VLAN
    public int allocateVlan(List<SwitchVlanVO> vlanList, List<Integer> allocatedVlans){
        int vlan = 0;
        for (SwitchVlanVO vlanVO : vlanList) {
            Integer startVlan = vlanVO.getStartVlan();
            Integer endVlan = vlanVO.getEndVlan();
            List<Integer> allocatedVlan = new ArrayList<>();
            for(Integer alloc : allocatedVlans){
                if(alloc >= startVlan && alloc <= endVlan){
                    allocatedVlan.add(alloc);
                }
            }
            vlan = NetworkUtils.randomAllocateVlan(startVlan,endVlan,allocatedVlan);
            if(vlan != 0){
                break;
            }
        }
        return vlan;
    }

    //查询同一个物理交换机下的所有VLAN段
    public List<SwitchVlanVO> getSwitchVlans(String physicalSwitchUuid){
        List<SwitchVlanVO> switchVlans = new ArrayList<>();
        String sql = "select c from PhysicalSwitchVO a,SwitchVO b,SwitchVlanVO c " +
                "where a.uuid = b.physicalSwitchUuid and b.uuid = c.switchUuid " +
                "and a.uuid = :physicalSwitchUuid";
        TypedQuery<SwitchVlanVO> vq = dbf.getEntityManager().createQuery(sql, SwitchVlanVO.class);
        vq.setParameter("physicalSwitchUuid",physicalSwitchUuid);
        switchVlans = vq.getResultList();
        return switchVlans;
    }

    //查询同一个上联交换机下的所有VLAN段、
    public List<SwitchVlanVO> getSwitchVlansFromUplink(String uplinkPhysicalSwitchUuid){
        List<SwitchVlanVO> switchVlans = new ArrayList<>();
        String sql = "select d from PhysicalSwitchUpLinkRefVO a,PhysicalSwitchVO b,SwitchVO c,SwitchVlanVO d " +
                "where a.physicalSwitchUuid = b.uuid and b.uuid = c.physicalSwitchUuid and c.uuid = d.switchUuid " +
                "and a.uplinkPhysicalSwitchUuid = :uplinkPhysicalSwitchUuid";
        TypedQuery<SwitchVlanVO> vq = dbf.getEntityManager().createQuery(sql, SwitchVlanVO.class);
        vq.setParameter("uplinkPhysicalSwitchUuid",uplinkPhysicalSwitchUuid);
        switchVlans = vq.getResultList();
        return switchVlans;
    }

    //将VLAN段整合成一个List
    public List<Integer> getVlanIntegerList(List<SwitchVlanVO> switchVlans){
        List<Integer> vlanIntegerList = new ArrayList<>();
        for(SwitchVlanVO switchVlanVO: switchVlans){
            if(switchVlanVO.getStartVlan().equals(switchVlanVO.getEndVlan())){
                vlanIntegerList.add(switchVlanVO.getStartVlan());
            }else{
                for(Integer i=switchVlanVO.getStartVlan();i<=switchVlanVO.getEndVlan();i++){
                    vlanIntegerList.add(i);
                }
            }
        }
        return vlanIntegerList;
    }

    public List<Integer> getVlanIntegerList(Integer startVlan,Integer endVlan){
        List<Integer> vlanIntegerList = new ArrayList<>();

        if(startVlan.equals(endVlan)){
            vlanIntegerList.add(startVlan);
        }else{
            for(Integer i = startVlan;i <= endVlan;i++){
                vlanIntegerList.add(i);
            }
        }

        return vlanIntegerList;
    }

    //判断两个集合是否存在交集
    public boolean isMixed(List<Integer> aList,List<Integer> bList){
        for(Integer b: bList){
            if(aList.contains(b)){
                return true;
            }
        }
        return false;
    }
}
