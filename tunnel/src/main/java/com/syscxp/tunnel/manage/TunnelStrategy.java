package com.syscxp.tunnel.manage;

import com.syscxp.tunnel.header.switchs.*;
import com.syscxp.tunnel.header.tunnel.TunnelInterfaceVO;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.tunnel.header.switchs.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Random;

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
    public String getSwitchPortByStrategy(String endpointUuid , SwitchPortAttribute portAttribute , SwitchPortType portType){
        String switchPortUuid = null;
        if(portAttribute == SwitchPortAttribute.Shared){        //共享端口
            String sql = "select c.uuid from EndpointVO a,SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = :endpointUuid " +
                    "and b.state = :switchState and b.status = :switchStatus " +
                    "and c.portAttribute = :portAttribute and c.state = :portState and c.autoAllot = :autoAllot ";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",endpointUuid);
            vq.setParameter("switchState", SwitchState.Enabled);
            vq.setParameter("switchStatus", SwitchStatus.Connected);
            vq.setParameter("portAttribute",portAttribute);
            vq.setParameter("portState", SwitchPortState.Enabled);
            vq.setParameter("autoAllot", 1);
            List<String> portList = vq.getResultList();
            if(portList.size() > 0){
                Random r = new Random();
                switchPortUuid = portList.get(r.nextInt(portList.size()));
            }
        }else if(portAttribute == SwitchPortAttribute.Exclusive){    //独享端口
            String sql = "select c.uuid from EndpointVO a, SwitchVO b,SwitchPortVO c " +
                    "where a.uuid = b.endpointUuid and b.uuid = c.switchUuid " +
                    "and a.uuid = :endpointUuid " +
                    "and b.state = :switchState and b.status = :switchStatus " +
                    "and c.portAttribute = :portAttribute and c.state = :portState and c.portType = :portType and c.autoAllot = :autoAllot " +
                    "and c.uuid not in (select switchPortUuid from InterfaceVO)";
            TypedQuery<String> vq = dbf.getEntityManager().createQuery(sql, String.class);
            vq.setParameter("endpointUuid",endpointUuid);
            vq.setParameter("switchState", SwitchState.Enabled);
            vq.setParameter("switchStatus", SwitchStatus.Connected);
            vq.setParameter("portAttribute",portAttribute);
            vq.setParameter("portState", SwitchPortState.Enabled);
            vq.setParameter("portType",portType);
            vq.setParameter("autoAllot", 1);
            List<String> portList = vq.getResultList();
            if(portList.size() > 0){
                Random r = new Random();
                switchPortUuid = portList.get(r.nextInt(portList.size()));
            }

        }

        return switchPortUuid;

    }

    //策略分配外部VLAN
    public Integer getInnerVlanByStrategy(String networkUuid ,String interfaceUuid){
        Integer vlan = null;

        //查询该TUNNEL的物理接口所属的虚拟交换机
        String switchUuid = findSwitchByInterface(interfaceUuid);
        //查询该虚拟交换机下所有的Vlan段
        List<SwitchVlanVO> vlanList = findSwitchVlanBySwitch(switchUuid);
        //查询该虚拟交换机下已经分配的Vlan
        List<Integer> allocatedVlans = fingAllocateVlanBySwitch(switchUuid);


        //同一个VSI下同一个物理接口不用分配vlan，他们vlan一样
        vlan = findVlanForSameVsiAndInterface(networkUuid, interfaceUuid);
        if(vlan != -1){
            return vlan;
        }else{
            if(allocatedVlans.isEmpty()){
                return vlanList.get(0).getStartVlan();
            }else{
                vlan = allocateVlan(vlanList, allocatedVlans);
                return vlan;
            }

        }
    }

    //查询物理接口所属的虚拟交换机
    public String findSwitchByInterface (String interfaceUuid){
        String sql = "select a.switchUuid from SwitchPortVO a,InterfaceVO b " +
                "where a.uuid = b.switchPortUuid " +
                "and b.uuid = :interfaceUuid";
        TypedQuery<String> sq = dbf.getEntityManager().createQuery(sql,String.class);
        sq.setParameter("interfaceUuid",interfaceUuid);
        String switchUuid = sq.getSingleResult();
        return switchUuid;
    }

    //查询该虚拟交换机下所有的Vlan段
    public List<SwitchVlanVO> findSwitchVlanBySwitch (String switchUuid){
        String sql = "select a from SwitchVlanVO a where a.switchUuid = :switchUuid";
        TypedQuery<SwitchVlanVO> svq = dbf.getEntityManager().createQuery(sql, SwitchVlanVO.class);
        svq.setParameter("switchUuid",switchUuid);
        List<SwitchVlanVO> vlanList = svq.getResultList();
        return vlanList;
    }

    //查询该虚拟交换机下Tunnel已经分配的Vlan
    public List<Integer> fingAllocateVlanBySwitch(String switchUuid){

        String sql = "select distinct a.vlan from TunnelInterfaceVO a,InterfaceVO b,SwitchPortVO c " +
                "where a.interfaceUuid = b.uuid " +
                "and b.switchPortUuid = c.uuid " +
                "and c.switchUuid = :switchUuid ";
        TypedQuery<Integer> avq = dbf.getEntityManager().createQuery(sql,Integer.class);
        avq.setParameter("switchUuid",switchUuid);
        List<Integer> allocatedVlans = avq.getResultList();
        return allocatedVlans;
    }

    //查询该端口在同一个VSI下有否存在，如果存在，直接使用该端口的vlan即可
    public Integer findVlanForSameVsiAndInterface(String networkUuid, String interfaceUuid){
        String sql = "select b from TunnelVO a,TunnelInterfaceVO b where a.uuid = b.tunnelUuid " +
                "and a.networkUuid = :networkUuid and b.interfaceUuid = :interfaceUuid ";
        TypedQuery<TunnelInterfaceVO> vlanq = dbf.getEntityManager().createQuery(sql,TunnelInterfaceVO.class);
        vlanq.setParameter("networkUuid",networkUuid);
        vlanq.setParameter("interfaceUuid",interfaceUuid);

        List<TunnelInterfaceVO> list = vlanq.getResultList();
        return list.isEmpty() ? -1 : list.get(0).getVlan();
    }

    //分配可用VLAN
    public int allocateVlan(List<SwitchVlanVO> vlanList, List<Integer> allocatedVlans){
        int vlan = 0;
        for (SwitchVlanVO vlanVO : vlanList) {
            Integer startVlan = vlanVO.getStartVlan();
            Integer endVlan = vlanVO.getEndVlan();
            List<Integer> allocatedVlan = null;
            for(Integer alloc : allocatedVlans){
                if(alloc >= startVlan && alloc <= endVlan){
                    allocatedVlan.add(alloc);
                }
            }
            vlan = NetworkUtils.randomAllocateVlan(startVlan,endVlan,allocatedVlan);
            if(vlan == 0){
                continue;
            }else{
                break;
            }
        }
        return vlan;
    }
}