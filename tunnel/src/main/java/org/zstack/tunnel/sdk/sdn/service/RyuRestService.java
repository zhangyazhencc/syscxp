package org.zstack.tunnel.sdk.sdn.service;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.client.RestTemplate;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.header.AbstractService;
import org.zstack.header.message.Message;
import org.zstack.header.rest.RESTFacade;
import org.zstack.tunnel.header.monitor.InterfaceType;
import org.zstack.tunnel.sdk.sdn.vo.TunnelMonitorIssueVO;
import org.zstack.utils.gson.JSONObjectUtil;

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
        RestTemplate restTemplate = evtf.getRESTTemplate();

        TunnelMonitorIssueVO issueVO = new TunnelMonitorIssueVO();

        String sql1 = "select b.sortTag ,a.bandwidth,b.vlan\n" +
                "from TunnelVO a,TunnelInterfaceVO b \n" +
                "where b.tunnelUuid = a.uuid\n" +
                "and a.uuid = :tunnelUuid";
        TypedQuery<Tuple> q1 = dbf.getEntityManager().createQuery(sql1, Tuple.class);
        q1.setParameter("tunnelUuid",tunnelUuid);
        List<Tuple> listx = q1.getResultList();

        for (Tuple t : q1.getResultList()) {
            if(InterfaceType.A.toString().equals(t.get(0,String.class))){
                issueVO.setVlan_id(t.get(1,Integer.class)+1); // 监控vlan为业务vlan+1
                issueVO.setBandwidth(t.get(2,Integer.class));
            }
        }

        String sql2 = "select b.interfaceType,c.hostIp,b.monitorIp,d.physicalSwitchPortName,'eth-0-31' as uplinkPhysicalSwitchPortName\n" +
                "from TunnelMonitorVO a,TunnelMonitorInterfaceVO b , HostVO c, HostSwitchMonitorVO d\n" +
                "where b.tunnelMonitorUuid = a.uuid\n" +
                "and c.uuid = b.hostUuid\n" +
                "and d.hostUuid = c.uuid\n" +
                "and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> q2 = dbf.getEntityManager().createQuery(sql2, Tuple.class);
        q2.setParameter("tunnelUuid",tunnelUuid);
        for (Tuple t : q2.getResultList()) {
            if(InterfaceType.A.toString().equals(t.get(0,String.class))){
                issueVO.setM_ip(t.get(1,String.class));
                issueVO.setNw_src(t.get(2,String.class));
                issueVO.setIn_port(t.get(3,String.class));
                issueVO.setOut_port(t.get(4,String.class));
            }else if(InterfaceType.Z.toString().equals(t.get(0,String.class))){
                issueVO.setNw_dst(t.get(2,String.class));
            }
        }

        List<TunnelMonitorIssueVO> list = new ArrayList<>();
        list.add(issueVO);

        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("tunnel_id",tunnelUuid);
        urlVariables.put("tunnel_monitor",list);

        String jsonString = JSONObjectUtil.toJsonString(urlVariables);

        String result = restTemplate.postForEntity("http://localhost:8088/demo/call", jsonString, String.class).getBody();
        // String result = restTemplate.postForEntity("http://192.168.211.224:8080/tunnel/start_monitor", jsonString, String.class).getBody();

        System.out.println(result);
    }


    public void restTest(String tunnelUuid,String tunnelMonitorUuid){
        // void asyncJsonPost(String url, String body, AsyncRESTCallback callback);
        // evtf.asyncJsonPost("http://localhost:8088/demo/call","",callbackTest());
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
