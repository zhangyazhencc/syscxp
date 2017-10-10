package com.syscxp.tunnel.sdk.sdn.service;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.AsyncRESTCallback;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.tunnel.header.monitor.*;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchAccessType;
import com.syscxp.tunnel.sdk.sdn.vo.MplsConfigIssueVO;
import com.syscxp.tunnel.sdk.sdn.vo.SdnConfigIssueVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: 控制器命令下发.
 */
@Configurable(preConstruction = true, dependencyCheck = true, autowire = Autowire.BY_TYPE)
public class RyuRestService extends AbstractService {
    private static final CLogger logger = Utils.getLogger(RyuRestService.class);

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade evtf;

    @Autowired
    private CloudBus bus;

    // 监控通道配置下发
    public void tunnelMonitorIssue(String tunnelUuid,Message msg){
        List<MplsConfigIssueVO> mplsList = new ArrayList<>();
        List<SdnConfigIssueVO> sdnList = new ArrayList<>();

        Map<String,Object> result = new HashMap<String, Object>();
        result.put("tunnel_id",tunnelUuid);
        result.put("mpls_interfaces",mplsList);
        result.put("sdn_interfaces",sdnList);

        String monitorHostSql = "select b.interfaceType,b.monitorIp,k.physicalSwitchPortName\n" +
                "  from TunnelMonitorVO a,TunnelMonitorInterfaceVO b,HostSwitchMonitorVO k\n" +
                " where b.tunnelMonitorUuid = a.uuid\n" +
                "   and k.hostUuid = b.hostUuid\n" +
                "   and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> monitorHostQ = dbf.getEntityManager().createQuery(monitorHostSql, Tuple.class);
        monitorHostQ.setParameter("tunnelUuid",tunnelUuid);

        Map<String,String> monitorIp = new HashMap<String, String>();
        Map<String,String> monitorPort = new HashMap<String, String>();
        for (Tuple monitor : monitorHostQ.getResultList()) {
            monitorIp.put(monitor.get(0).toString(),monitor.get(1,String.class).toString());
            monitorPort.put(monitor.get(0).toString(),monitor.get(2,String.class).toString());
        }

        String monitorSql = "select b.interfaceType,b.hostUuid,b.monitorIp,b.interfaceUuid\n" +
                "from TunnelMonitorVO a,TunnelMonitorInterfaceVO b\n" +
                "where b.tunnelMonitorUuid = a.uuid\n" +
                "and a.tunnelUuid = :tunnelUuid";

        TypedQuery<Tuple> monitorQ = dbf.getEntityManager().createQuery(monitorSql, Tuple.class);
        monitorQ.setParameter("tunnelUuid",tunnelUuid);
        for (Tuple monitor : monitorQ.getResultList()) {
            MplsConfigIssueVO mpls = new MplsConfigIssueVO();

            String hostSql = "select e.accessType,e.mIP,e.username,e.password,f.model,f.subModel,d.physicalSwitchPortName\n" +
                    "from HostVO c,HostSwitchMonitorVO d, PhysicalSwitchVO e,SwitchModelVO f\n" +
                    "where d.hostUuid = c.uuid\n" +
                    "and d.physicalSwitchUuid = e.uuid\n" +
                    "and f.uuid = e.switchModelUuid\n" +
                    "and c.uuid = :hostUuid";
            TypedQuery<Tuple> hostQ = dbf.getEntityManager().createQuery(hostSql, Tuple.class);
            hostQ.setParameter("hostUuid",monitor.get(1).toString());
            Tuple host = hostQ.getResultList().get(0);

            mpls.setM_ip(host.get(1).toString());
            mpls.setUsername(host.get(2).toString());
            mpls.setPassword(host.get(3).toString());
            mpls.setSwitch_type(host.get(4).toString());
            mpls.setSub_type(host.get(5).toString());

            String tunnelSql = "select g.bandwidth,h.vlan,j.portName \n" +
                    "from TunnelVO g, TunnelInterfaceVO h, InterfaceVO i, SwitchPortVO j\n" +
                    "where h.tunnelUuid = g.uuid\n" +
                    "and h.sortTag = :sortTag\n" +
                    "and i.uuid = h.interfaceUuid\n" +
                    "and j.uuid = i.switchPortUuid\n" +
                    "and g.uuid = :tunnelUuid";
            TypedQuery<Tuple> tunnelQ = dbf.getEntityManager().createQuery(tunnelSql, Tuple.class);
            tunnelQ.setParameter("tunnelUuid",tunnelUuid);
            tunnelQ.setParameter("sortTag",monitor.get(0).toString());
            Tuple tunnel = tunnelQ.getSingleResult();
            mpls.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));
            mpls.setVlan_id(Integer.valueOf(tunnel.get(1).toString())+1);
            mpls.setPort_name(tunnel.get(2,String.class));

            mplsList.add(mpls);

            if (PhysicalSwitchAccessType.SDN.toString().equals(host.get(0).toString())){
                SdnConfigIssueVO sdn = new SdnConfigIssueVO();
                sdn.setM_ip(host.get(1,String.class));
                if(monitor.get(0).toString().equals(InterfaceType.A.toString())){
                    sdn.setNw_src(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.A.toString()));
                    sdn.setOut_port(monitorPort.get(InterfaceType.Z.toString()));
                }
                if(monitor.get(0).toString().equals(InterfaceType.Z.toString())){
                    sdn.setNw_src(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.Z.toString()));
                    sdn.setOut_port(monitorPort.get(InterfaceType.A.toString()));
                }
                sdn.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));
                sdn.setVlan_id(Integer.valueOf(tunnel.get(1).toString())+1);
                sdnList.add(sdn);
            }
        }

        String jsonString = JSONObjectUtil.toJsonString(result);

        // 同步执行
        RestTemplate restTemplate = evtf.getRESTTemplate();
        // String restResult = restTemplate.postForEntity("http://localhost:8088/demo/call", jsonString, String.class).getBody();
        String restResult = restTemplate.postForEntity("http://192.168.211.224:8080/tunnel/start_monitor", jsonString, String.class).getBody();

        // 异步执行
        /*evtf.asyncJsonPost(RyuRestConstant.TEST_URL, jsonString, new AsyncRESTCallback(msg) {
            @Override
            public void fail(ErrorCode err) {
                logger.error("配置下发失败！");
                throw new RestClientException(err.toString());
            }

            @Override
            public void success(HttpEntity<String> responseEntity) {
                logger.info("配置下发成功！");
                TunnelMonitorVO vo = dbf.findByUuid(tunnelUuid, TunnelMonitorVO.class);
                vo.setStatus(TunnelMonitorStatus.NORMAL);
                dbf.updateAndRefresh(vo);

                logger.info(responseEntity.getBody().toString());
            }
        });*/

        logger.info("监控配置下发中！");
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
