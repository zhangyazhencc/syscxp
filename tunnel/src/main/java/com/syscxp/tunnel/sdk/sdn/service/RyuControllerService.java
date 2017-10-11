package com.syscxp.tunnel.sdk.sdn.service;

import com.syscxp.tunnel.sdk.sdn.vo.SdnConfigIssueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.header.AbstractService;
import com.syscxp.header.message.Message;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.utils.gson.JSONObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2017-09-26.
 * @Description: 控制器命令下发.
 */
@Service
public class RyuControllerService extends AbstractService {
    @Autowired
    private DatabaseFacade dbf;

    @Autowired
    private RESTFacade evtf2;

    // 监控通道配置下发
    public void tunnelMonitorIssue(String tunnelUuid, RESTFacade evtf){
        RestTemplate restTemplate = evtf.getRESTTemplate();

        SdnConfigIssueVO issueVO = new SdnConfigIssueVO();
        issueVO.setM_ip("192.168.211.17");
        issueVO.setIn_port("eth-0-31");
        issueVO.setNw_src("192.168.211.25");
        issueVO.setNw_dst("192.168.211.26");
        issueVO.setVlan_id(200);
        issueVO.setUplink("eth-0-3");
        issueVO.setBandwidth(1000);

        List<SdnConfigIssueVO> list = new ArrayList<>();
        list.add(issueVO);

        Map<String, Object> urlVariables = new HashMap<String, Object>();
        urlVariables.put("tunnel_id",tunnelUuid);
        urlVariables.put("tunnel_monitor",list);

        String jsonString = JSONObjectUtil.toJsonString(urlVariables);

        String result = restTemplate.postForEntity("http://localhost:8088/demo/call", jsonString, String.class).getBody();
        // String result = restTemplate.postForEntity("http://192.168.211.224:8080/tunnel/start_monitor", jsonString, String.class).getBody();

        System.out.println(result);
    }


    public void tunnelMonitorIssue111(String tunnelUuid,String tunnelMonitorUuid){
        /*
        tunnelMonitorUuid = "6a696d1fd15f4d46b4ffbc2d62c60b6a";
        String sql = "SELECT e.hostIp AS m_type, f.physicalSwitchPortName AS in_port," +
                "ca.monitorIp AS nw_src,d.vlan as vlan_id,eth-0-3 AS out_port,a.bandwidth " +
                "FROM TunnelVO a,TunnelMonitorVO b,TunnelMonitorInterfaceVO ca," +
                "TunnelInterfaceVO d,HostVO e ,HostSwitchMonitorVO f " +
                "WHERE b.tunnelUuid = a.uuid " +
                "AND b.uuid = :tunnelMonitorUuid " +
                "AND ca.tunnelMonitorUuid = b.uuid " +
                "AND d.tunnelUuid = a.uuid " +
                "AND d.sortTag = ca.interfaceType " +
                "AND e.uuid = ca.hostUuid " +
                "AND e.uuid = f.hostUuid";

        /*TypedQuery<TunnelMonitorIssueDetailVO> vq = dbf.getEntityManager().createQuery(sql, TunnelMonitorIssueDetailVO.class);
        vq.setParameter("tunnelMonitorUuid",tunnelMonitorUuid);
        List<TunnelMonitorIssueDetailVO> detailInventories = vq.getResultList();
        TunnelMonitorIssueDetailVO detailInventoryA=new TunnelMonitorIssueDetailVO();
        TunnelMonitorIssueDetailVO detailInventoryZ=new TunnelMonitorIssueDetailVO();
        if(detailInventories!=null && detailInventories.size() == 2){
            detailInventoryA = detailInventories.get(0);
            detailInventoryZ = detailInventories.get(1);

            detailInventoryA.setNw_dst(detailInventoryZ.getNw_src());
            detailInventoryZ.setNw_dst(detailInventoryA.getNw_src());
        }else
            throw new RuntimeException("获取监控通道配置下发信息失败！");

        List<TunnelMonitorIssueDetailVO> resultDetailInventories = new ArrayList<TunnelMonitorIssueDetailVO>();
        resultDetailInventories.add(detailInventoryA);
        resultDetailInventories.add(detailInventoryZ);

        SdnConfigIssueVO issueInventory = new SdnConfigIssueVO();
        issueInventory.setTunnel_uuid(tunnelUuid);
        issueInventory.setDetailInventories(resultDetailInventories);
        System.out.println(JSONObjectUtil.toJsonString(vq));*/
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
