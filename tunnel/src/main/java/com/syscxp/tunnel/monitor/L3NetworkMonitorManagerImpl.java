package com.syscxp.tunnel.monitor;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.header.AbstractService;
import com.syscxp.header.Component;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.apimediator.ApiMessageInterceptor;
import com.syscxp.header.host.HostDeleteExtensionPoint;
import com.syscxp.header.host.HostException;
import com.syscxp.header.host.HostInventory;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.Message;
import com.syscxp.header.tunnel.MonitorConstant;
import com.syscxp.header.tunnel.monitor.*;
import com.syscxp.header.tunnel.network.L3EndpointState;
import com.syscxp.header.tunnel.network.L3EndpointVO;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import com.syscxp.utils.network.NetworkUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public class L3NetworkMonitorManagerImpl extends AbstractService implements L3NetworkMonitorManager, Component, ApiMessageInterceptor, HostDeleteExtensionPoint {
    private static final CLogger logger = Utils.getLogger(TunnelMonitorManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private L3NetworkMonitorBase monitor;

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        bus.dealWithUnknownMessage(msg);
    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIConfigL3NetworkMonitorMsg) {
            handle((APIConfigL3NetworkMonitorMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    @Transactional
    private void handle(APIConfigL3NetworkMonitorMsg msg) {
        APIConfigL3NetworkMonitorEvent event = new APIConfigL3NetworkMonitorEvent();
        L3EndpointVO endpointVO = dbf.findByUuid(msg.getL3EndPointUuid(), L3EndpointVO.class);

        if (endpointVO.getState() == L3EndpointState.Enabled) {
            List<L3NetworkMonitorVO> monitorVOS = new ArrayList<L3NetworkMonitorVO>();
            for (String dstL3EndpointUuid : msg.getDstL3EndPointUuids()) {
                L3NetworkMonitorVO vo = new L3NetworkMonitorVO();
                vo.setUuid(Platform.getUuid());
                vo.setL3NetworkUuid(endpointVO.getL3NetworkUuid());
                vo.setSrcL3EndpointUuid(msg.getL3EndPointUuid());
                vo.setDstL3EndpointUuid(dstL3EndpointUuid);

                monitorVOS.add(vo);
            }

            if (StringUtils.isNotEmpty(msg.getMonitorIp())) {
                if (StringUtils.isEmpty(endpointVO.getMonitorIp())) {
                    // 新增监控ip，开启监控
                    endpointVO.setMonitorIp(msg.getMonitorIp());
                    monitor.startMonitor(endpointVO, monitorVOS);
                } else {
                    if (StringUtils.equals(msg.getMonitorIp(), endpointVO.getMonitorIp())) {
                        // 更新监控ip，仅更新本端出的监控机监控
                        monitor.updateSrcMonitor(endpointVO, monitorVOS);
                    } else {
                        // 更新监控ip，更新对端监控机监控
                        endpointVO.setMonitorIp(msg.getMonitorIp());
                        monitor.updateMonitorIp(endpointVO, monitorVOS);
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(endpointVO.getMonitorIp())) {
                    // 删除监控ip，停止监控
                    endpointVO.setMonitorIp(msg.getMonitorIp());
                    monitor.stopMonitor(endpointVO);
                }
            }
        } else {
            endpointVO.setMonitorIp(msg.getMonitorIp());
        }

        // 更新监控ip
        endpointVO = dbf.updateAndRefresh(endpointVO);

        List<L3NetworkMonitorVO> monitorVOS = Q.New(L3NetworkMonitorVO.class)
                .eq(L3NetworkMonitorVO_.srcL3EndpointUuid, endpointVO.getUuid())
                .list();
        event.setInventory(L3EndpointMonitorInventory.valueOf(endpointVO, monitorVOS));
        bus.publish(event);
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(MonitorConstant.L3_MONITOR_SERVICE_ID);
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean stop() {
        return false;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof APIConfigL3NetworkMonitorMsg) {
            validate((APIConfigL3NetworkMonitorMsg) msg);
        }

        return msg;
    }

    private void validate(APIConfigL3NetworkMonitorMsg msg) {
        validateMonitorIp(msg.getL3EndPointUuid(), msg.getMonitorIp());

        for (String dstL3EndPointUuid : msg.getDstL3EndPointUuids()) {
            L3EndpointVO vo = dbf.findByUuid(dstL3EndPointUuid, L3EndpointVO.class);
            if (vo == null)
                throw new IllegalArgumentException(String.format("连接点[%s]不存在", dstL3EndPointUuid));
            else if (vo.getState() != L3EndpointState.Enabled)
                throw new IllegalArgumentException(String.format("[%s]状况为 %s，不能开通监控！", vo.getEndpointEO().getName(), vo.getState()));
        }
    }

    /***
     * TODO: 验证监控ip是否合法
     * @param l3EndPointUuid
     * @param monitorIp
     * @return
     */
    private void validateMonitorIp(String l3EndPointUuid, String monitorIp) {

        if (StringUtils.isNotEmpty(monitorIp) && !NetworkUtils.isIpv4Address(monitorIp))
            throw new RuntimeException(String.format("invalid monitor ip %s", monitorIp));

    }

    @Override
    public void preDeleteHost(HostInventory inventory) throws HostException {

    }

    @Override
    public void beforeDeleteHost(HostInventory inventory) {

    }

    @Override
    public void afterDeleteHost(HostInventory inventory) {

    }
}
