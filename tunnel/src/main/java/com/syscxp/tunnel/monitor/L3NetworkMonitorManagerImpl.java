package com.syscxp.tunnel.monitor;

import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.MessageSafe;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.job.JobQueueFacade;
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
import com.syscxp.header.tunnel.network.L3EndpointVO_;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-03-22.
 * @Description: .
 */
public class L3NetworkMonitorManagerImpl extends AbstractService implements L3NetworkMonitorManager, Component, ApiMessageInterceptor,HostDeleteExtensionPoint {
    private static final CLogger logger = Utils.getLogger(TunnelMonitorManagerImpl.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private L3NetworkMonitorBase l3NetworkMonitor;

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
        L3EndpointVO l3EndpointVO = dbf.findByUuid(msg.getL3EndPointUuid(), L3EndpointVO.class);

        if(l3EndpointVO.getState() == L3EndpointState.Enabled){
            List<L3NetworkMonitorVO> srcL3NetworkMonitorVOS = new ArrayList<L3NetworkMonitorVO>();
            for (String dstL3EndpointUuid : msg.getDstL3EndPointUuids()) {
                L3NetworkMonitorVO vo = new L3NetworkMonitorVO();
                vo.setUuid(Platform.getUuid());
                vo.setL3NetworkUuid(l3EndpointVO.getL3NetworkUuid());
                vo.setSrcL3EndpointUuid(msg.getL3EndPointUuid());
                vo.setDstL3EndpointUuid(dstL3EndpointUuid);

                srcL3NetworkMonitorVOS.add(vo);
            }

            if (StringUtils.isNotEmpty(msg.getMonitorIp())) {
                if (StringUtils.isEmpty(l3EndpointVO.getMonitorIp())) {
                    // 新增监控ip，开启监控
                    l3EndpointVO.setMonitorIp(msg.getMonitorIp());
                    l3NetworkMonitor.startMonitor(l3EndpointVO, srcL3NetworkMonitorVOS);
                } else {
                    if (StringUtils.equals(msg.getMonitorIp(), l3EndpointVO.getMonitorIp())) {
                        // 更新本端出的监控机监控
                        l3NetworkMonitor.updateMonitorVO(l3EndpointVO, srcL3NetworkMonitorVOS);
                    } else {
                        // 更新对端监控机监控
                        l3EndpointVO.setMonitorIp(msg.getMonitorIp());
                        l3NetworkMonitor.updateMonitorIp(l3EndpointVO, srcL3NetworkMonitorVOS);
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(l3EndpointVO.getMonitorIp())) {
                    // 删除监控ip，停止监控
                    l3EndpointVO.setMonitorIp(msg.getMonitorIp());
                    l3NetworkMonitor.stopMonitor(l3EndpointVO);
                }
            }
        }else {
            l3EndpointVO.setMonitorIp(msg.getMonitorIp());
        }

        // 更新监控ip
        dbf.getEntityManager().merge(l3EndpointVO);
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
    private boolean validateMonitorIp(String l3EndPointUuid, String monitorIp) {
        boolean isValid = false;

        return isValid;
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
