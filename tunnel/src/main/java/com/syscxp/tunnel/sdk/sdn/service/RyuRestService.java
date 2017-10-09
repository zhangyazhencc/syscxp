package com.syscxp.tunnel.sdk.sdn.service;

import com.syscxp.tunnel.header.switchs.PhysicalSwitchAccessType;
import com.syscxp.tunnel.sdk.sdn.vo.MplsConfigIssueVO;
import com.syscxp.tunnel.sdk.sdn.vo.SdnConfigIssueVO;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.AsyncRESTCallback;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.tunnel.header.monitor.InterfaceType;
import com.syscxp.utils.gson.JSONObjectUtil;

import javax.persistence.AccessType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: 控制器命令下发.
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class RyuRestService extends AbstractService {
    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade evtf;

    // 监控通道配置下发
    public void tunnelMonitorIssue(String tunnelUuid){
        List<MplsConfigIssueVO> mplsList = new ArrayList<>();
        List<SdnConfigIssueVO> sdnList = new ArrayList<>();

        Map<String,Object> result = new HashMap<String, Object>();
        result.put("tunnel_id",tunnelUuid);
        result.put("mpls_interfaces",mplsList);
        result.put("sdn_interfaces",sdnList);

        String monitorSql = "select b.interfaceType,b.hostUuid,b.monitorIp,b.interfaceUuid\n" +
                "from TunnelMonitorVO a,TunnelMonitorInterfaceVO b\n" +
                "where b.tunnelMonitorUuid = a.uuid\n" +
                "and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> monitorQ = dbf.getEntityManager().createQuery(monitorSql, Tuple.class);
        monitorQ.setParameter("tunnelUuid",tunnelUuid);

        Map<String,String> monitorIp = new HashMap<String, String>();
        for (Tuple monitor : monitorQ.getResultList()) {
            monitorIp.put(monitor.get(0,String.class),monitor.get(2,String.class));
        }

        for (Tuple monitor : monitorQ.getResultList()) {
            MplsConfigIssueVO mpls = new MplsConfigIssueVO();

            String hostSql = "select e.accessType,e.mIP,e.username,e.password,f.model,f.subModel,d.physicalSwitchPortName\n" +
                    "from HostVO c,HostSwitchMonitorVO d, PhysicalSwitchVO e,SwitchModelVO f\n" +
                    "where d.hostUuid = c.uuid\n" +
                    "and d.physicalSwitchUuid = e.uuid\n" +
                    "and f.uuid = e.switchModelUuid\n" +
                    "and c.uuid = :hostUuid";
            TypedQuery<Tuple> hostQ = dbf.getEntityManager().createQuery(hostSql, Tuple.class);
            hostQ.setParameter("hostUuid",monitor.get(1,String.class));
            Tuple host = hostQ.getSingleResult();

            mpls.setM_ip(host.get(1,String.class));
            mpls.setUsername(host.get(2,String.class));
            mpls.setPassword(host.get(3,String.class));
            mpls.setSwitch_type(host.get(4,String.class));
            mpls.setSub_type(host.get(5,String.class));

            String tunnelSql = "select g.bandwidth,h.vlan,j.portName \n" +
                    "from TunnelVO g, TunnelInterfaceVO h, InterfaceVO i, SwitchPortVO j\n" +
                    "where h.tunnelUuid = g.uuid\n" +
                    "and h.sortType = :sortType\n" +
                    "and i.uuid = h.interfaceUuid\n" +
                    "and j.uuid = i.switchPortUuid\n" +
                    "and g.uuid = :tunnelUuid";
            TypedQuery<Tuple> tunnelQ = dbf.getEntityManager().createQuery(tunnelSql, Tuple.class);
            tunnelQ.setParameter("tunnelUuid",tunnelUuid);
            tunnelQ.setParameter("sortType",monitor.get(0,String.class));
            Tuple tunnel = tunnelQ.getSingleResult();
            mpls.setBandwidth(tunnel.get(0,Integer.class));
            mpls.setVlan_id(tunnel.get(1,Integer.class)+1);
            mpls.setPort_name(tunnel.get(2,String.class));

            mplsList.add(mpls);

            if (PhysicalSwitchAccessType.SDN.toString().equals(host.get(0,String.class))){
                SdnConfigIssueVO sdn = new SdnConfigIssueVO();
                sdn.setM_ip(host.get(1,String.class));
                sdn.setIn_port(host.get(6,String.class));
                if(monitor.get(0,String.class).toString().equals(InterfaceType.A.toString())){
                    sdn.setNw_src(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.Z.toString()));
                }
                if(monitor.get(0,String.class).toString().equals(InterfaceType.Z.toString())){
                    sdn.setNw_src(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.A.toString()));
                }
                sdn.setBandwidth(tunnel.get(0,Integer.class));
                sdn.setVlan_id(tunnel.get(1,Integer.class)+1);
                sdnList.add(sdn);
            }
        }

        String jsonString = JSONObjectUtil.toJsonString(result);

        RestTemplate restTemplate = evtf.getRESTTemplate();
        String rspResult = restTemplate.postForEntity("http://localhost:8088/demo/call", jsonString, String.class).getBody();
        // String result = restTemplate.postForEntity("http://192.168.211.224:8080/tunnel/start_monitor", jsonString, String.class).getBody();

        System.out.println(result);
    }


    public void restTest(String tunnelUuid,Message msg){
        String json= "";

        // RyuRestCallback callback = new RyuRestCallback();

        String url = "http://localhost:8088/demo/test";

        evtf.asyncJsonPost(url, json, new AsyncRESTCallback(msg) {

            @Override
            public void fail(ErrorCode err) {
                System.out.println("回调失败！");
                System.out.println(err.toString());
            }

            @Override
            public void success(HttpEntity<String> responseEntity) {
                System.out.println("回调成功！");
                System.out.println(responseEntity.getBody());
            }
        });
    }



    private void callbackTest(){
        System.out.println("callback method !");
    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
