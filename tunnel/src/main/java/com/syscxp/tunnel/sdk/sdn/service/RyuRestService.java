package com.syscxp.tunnel.sdk.sdn.service;

import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.header.AbstractService;
import com.syscxp.header.errorcode.ErrorCode;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.AsyncRESTCallback;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.tunnel.header.monitor.*;
import com.syscxp.tunnel.header.switchs.PhysicalSwitchAccessType;
import com.syscxp.tunnel.header.tunnel.QinqVO_;
import com.syscxp.tunnel.sdk.sdn.dto.MonitorMplsConfig;
import com.syscxp.tunnel.sdk.sdn.dto.MonitorSdnConfig;
import com.syscxp.tunnel.sdk.sdn.dto.SdnRestResponse;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpEntity;

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
    private static final CLogger logger = Utils.getLogger(RyuRestService.class);

    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade evtf;

    @Autowired
    private CloudBus bus;

    /**
     * 监控通道配置下发
     * @param tunnelUuid
     */
    public void tunnelMonitorStart(String tunnelUuid,Message msg){
        Map configMap = getTunnelMonitorConfigInfo(tunnelUuid);
        String jsonString = JSONObjectUtil.toJsonString(configMap);
        logger.info(jsonString);

        // 同步执行

        //RestTemplate restTemplate = evtf.getRESTTemplate();
        //String responseString = restTemplate.postForEntity(RyuRestConstant.SYNC_TEST_URL, jsonString, String.class).getBody();
        //System.out.println("执行结果："+responseString);

        //SdnRestResponse restR = JSONObjectUtil.fromTypedJsonString(responseString);
        //System.out.println(restR.toString());

        SdnRestResponse restResponse = evtf.syncJsonPost(RyuRestConstant.SYNC_TEST_URL, jsonString,SdnRestResponse.class);
        if(restResponse != null){
            SimpleQuery<TunnelMonitorVO> q = dbf.createQuery(TunnelMonitorVO.class);
            q.add(QinqVO_.tunnelUuid, SimpleQuery.Op.EQ, tunnelUuid);
            TunnelMonitorVO vo = (TunnelMonitorVO)q.list().get(0);

            if("0".equals(restResponse.getCode())){
                // 执行成功
                logger.info("配置下发成功！");

                if(vo != null){
                    vo.setStatus(TunnelMonitorStatus.NORMAL);
                    dbf.updateAndRefresh(vo);
                }
            }else{
                // TODO: 执行失败
                logger.error(restResponse.toString());
                if(vo != null){
                    vo.setMsg(restResponse.toString());
                    dbf.updateAndRefresh(vo);
                }
            }
        }



        // 异步执行
        /*evtf.asyncJsonPost(RyuRestConstant.ASYNC_TEST_URL, jsonString, new AsyncRESTCallback(msg) {
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
        });

        logger.info("监控配置下发中！");*/
    }

    private Map getTunnelMonitorConfigInfo(String tunnelUuid) {
        List<MonitorMplsConfig> mplsList = new ArrayList<>();
        List<MonitorSdnConfig> sdnList = new ArrayList<>();

        // 获取两端监控IP与端口
        Map<String,String> monitorIp = new HashMap<>();
        Map<String,String> monitorPort = new HashMap<>();
        getIpPort(tunnelUuid,monitorIp,monitorPort);

        //根据tunnel获取两端监控主机与物理接口
        String monitorSql = "select b.interfaceType,b.hostUuid,b.monitorIp,b.interfaceUuid\n" +
                "from TunnelMonitorVO a,TunnelMonitorInterfaceVO b\n" +
                "where b.tunnelMonitorUuid = a.uuid\n" +
                "and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> monitorQ = dbf.getEntityManager().createQuery(monitorSql, Tuple.class);
        monitorQ.setParameter("tunnelUuid",tunnelUuid);
        for (Tuple monitor : monitorQ.getResultList()) {
            // 获取交换机信息
            String hostSql = "select e.accessType,e.mIP,e.username,e.password,f.model,f.subModel,d.physicalSwitchPortName,d.physicalSwitchUuid\n" +
                    "from HostVO c,HostSwitchMonitorVO d, PhysicalSwitchVO e,SwitchModelVO f\n" +
                    "where d.hostUuid = c.uuid\n" +
                    "and d.physicalSwitchUuid = e.uuid\n" +
                    "and f.uuid = e.switchModelUuid\n" +
                    "and c.uuid = :hostUuid";
            TypedQuery<Tuple> hostQ = dbf.getEntityManager().createQuery(hostSql, Tuple.class);
            hostQ.setParameter("hostUuid",monitor.get(1).toString());
            Tuple host = hostQ.getResultList().get(0);

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
            Tuple tunnel = tunnelQ.getResultList().get(0);

            MonitorMplsConfig mpls = new MonitorMplsConfig();
            if(PhysicalSwitchAccessType.MPLS.toString().equals(host.get(0).toString())){
                mpls.setM_ip(host.get(1).toString());
                mpls.setUsername(host.get(2).toString());
                mpls.setPassword(host.get(3).toString());
                mpls.setSwitch_type(host.get(4).toString());
                mpls.setSub_type(host.get(5).toString());
                mpls.setVlan_id(Integer.valueOf(tunnel.get(1).toString())+1);
                mpls.setPort_name(tunnel.get(2,String.class));
                mpls.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));

                mplsList.add(mpls);
            }else if(PhysicalSwitchAccessType.SDN.toString().equals(host.get(0).toString())){
                // 获取上联口对应的物理交换机作为mpls数据
                String uplinkSql = "select b.accessType,b.mIP,b.username,b.password,c.model,c.subModel,a.uplinkPhysicalSwitchPortName as physicalSwitchPortName\n" +
                        "from PhysicalSwitchUpLinkRefVO a, PhysicalSwitchVO b,SwitchModelVO c\n" +
                        "where b.uuid = a.uplinkPhysicalSwitchUuid\n" +
                        "and c.uuid = b.switchModelUuid\n" +
                        "and a.physicalSwitchUuid = :physicalSwitchUuid";
                TypedQuery<Tuple> uplinkQ = dbf.getEntityManager().createQuery(uplinkSql, Tuple.class);
                uplinkQ.setParameter("physicalSwitchUuid",host.get(7).toString());
                Tuple uplink = uplinkQ.getResultList().get(0);

                mpls.setM_ip(uplink.get(1).toString());
                mpls.setUsername(uplink.get(2).toString());
                mpls.setPassword(uplink.get(3).toString());
                mpls.setSwitch_type(uplink.get(4).toString());
                mpls.setSub_type(uplink.get(5).toString());
                mpls.setVlan_id(Integer.valueOf(tunnel.get(1).toString())+1);
                mpls.setPort_name(tunnel.get(2,String.class));
                // mpls.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));

                mplsList.add(mpls);

                MonitorSdnConfig sdn = new MonitorSdnConfig();
                sdn.setM_ip(host.get(1,String.class));
                if(monitor.get(0).toString().equals(InterfaceType.A.toString())){
                    sdn.setNw_src(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.A.toString()));
                    sdn.setUplink(uplink.get(6).toString());
                }
                if(monitor.get(0).toString().equals(InterfaceType.Z.toString())){
                    sdn.setNw_src(monitorIp.get(InterfaceType.Z.toString()));
                    sdn.setNw_dst(monitorIp.get(InterfaceType.A.toString()));
                    sdn.setIn_port(monitorPort.get(InterfaceType.Z.toString()));
                    sdn.setUplink(uplink.get(6).toString());
                }
                sdn.setBandwidth(Integer.valueOf(tunnel.get(0).toString()));
                sdn.setVlan_id(Integer.valueOf(tunnel.get(1).toString())+1);

                sdnList.add(sdn);
            }
        }

        Map<String,Object> configMap = new HashMap<String, Object>();
        configMap.put("tunnel_id",tunnelUuid);
        if(mplsList.size()>0){
            configMap.put("mpls_interfaces",mplsList);
        }
        if(sdnList.size()>0){
            configMap.put("sdn_interfaces",sdnList);
        }

        return configMap;
    }

    /**
     * 获取tunnel两端监控ip与监控端口
     * @param monitorIp：监控IP集合
     * @param monitorPort：监控端口集合
     */
    private void getIpPort(String tunnelUuid, Map<String, String> monitorIp, Map<String, String> monitorPort) {
        String monitorHostSql = "select b.interfaceType,b.monitorIp,k.physicalSwitchPortName\n" +
                "  from TunnelMonitorVO a,TunnelMonitorInterfaceVO b,HostSwitchMonitorVO k\n" +
                " where b.tunnelMonitorUuid = a.uuid\n" +
                "   and k.hostUuid = b.hostUuid\n" +
                "   and a.tunnelUuid = :tunnelUuid";
        TypedQuery<Tuple> monitorHostQ = dbf.getEntityManager().createQuery(monitorHostSql, Tuple.class);
        monitorHostQ.setParameter("tunnelUuid",tunnelUuid);

        for (Tuple monitor : monitorHostQ.getResultList()) {
            monitorIp.put(monitor.get(0).toString(),monitor.get(1,String.class).toString());
            monitorPort.put(monitor.get(0).toString(),monitor.get(2,String.class).toString());
        }
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
