package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.config.GlobalConfig;
import com.syscxp.core.config.GlobalConfigUpdateExtensionPoint;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.Component;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.monitor.OpenTSDBCommands.*;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.tunnel.identity.TunnelGlobalConfig;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TunnelStatusChecker implements Component {
    private static final CLogger logger = Utils.getLogger(TunnelStatusChecker.class);

    @Autowired
    private RESTFacade restf;
    @Autowired
    private ThreadFacade thdf;

    private Future<Void> checkTunnelStatusThread = null;
    private int checkTunnelStatusInterval;
    private boolean isCheckTunnelStatus;
    public static final String TUNNEL_PACKETS_LOST = "tunnel.packets.lost";
    public static final String OPENTSDB_SERVER_URL = CoreGlobalProperty.OPENTSDB_SERVER_URL + "/api/query";
    public static final double PACKETS_LOST_MIN = 5;
    public static final double PACKETS_LOST_MAX = 20;

    private void startStatusCheck() {
        checkTunnelStatusInterval = TunnelGlobalConfig.CHECK_TUNNEL_STATUS_INTERVAL.value(Integer.class);
        isCheckTunnelStatus = TunnelGlobalConfig.IS_CHECK_TUNNEL_STATUS.value(Boolean.class);
        if (checkTunnelStatusThread != null) {
            checkTunnelStatusThread.cancel(true);
        }

        checkTunnelStatusThread = thdf.submitPeriodicTask(new CheckTunnelStatus(), 300);
        logger.debug(String
                .format("security group checkTunnelStatusThread starts[interval: %s seconds]", checkTunnelStatusInterval));
    }

    private void restartStatusCheck() {

        startStatusCheck();

        TunnelGlobalConfig.CHECK_TUNNEL_STATUS_INTERVAL.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart CheckTunnelStatus thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startStatusCheck();
            }
        });

        TunnelGlobalConfig.IS_CHECK_TUNNEL_STATUS.installUpdateExtension(new GlobalConfigUpdateExtensionPoint() {
            @Override
            public void updateGlobalConfig(GlobalConfig oldConfig, GlobalConfig newConfig) {
                logger.debug(String.format("%s change from %s to %s, restart CheckTunnelStatus thread",
                        oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
                startStatusCheck();
            }
        });
    }


    private class CheckTunnelStatus implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return checkTunnelStatusInterval;
        }

        @Override
        public String getName() {
            return "check-tunnel-status-" + Platform.getManagementServerId();
        }

        private List<TunnelVO> getTunnels() {

            return Q.New(TunnelVO.class)
                    .eq(TunnelVO_.state, TunnelState.Enabled)
                    .eq(TunnelVO_.monitorState, TunnelMonitorState.Enabled)
                    .list();
        }

        @Override
        public void run() {
            if (!isCheckTunnelStatus) {
                return;
            }
            List<TunnelVO> tunnelVOs = new ArrayList<>();
            try {
                tunnelVOs = getTunnels();
                logger.debug("tunnel status check.");
                if (tunnelVOs.isEmpty())
                    return;
                for (TunnelVO vo : tunnelVOs) {
                    Long endTime = Instant.now().getEpochSecond();
                    Long startTime = endTime - 5 * 30;

                    logger.debug(String.format("start check tunnel[UUID: %s] status:", vo.getUuid()));
                    String condition = getOpenTSDBQueryCondition(vo.getUuid(), TUNNEL_PACKETS_LOST, startTime, endTime);
                    String resp = restf.getRESTTemplate().postForObject(OPENTSDB_SERVER_URL, condition, String.class);
                    List<QueryResult> results = JSONObjectUtil.toCollection(resp, ArrayList.class, QueryResult.class);

                    if (!results.isEmpty()) {
                        QueryResult result = results.get(0);
                        Map dps = result.getDps();
                        if (CollectionUtils.isEmpty(dps)) {
                            logger.debug(String.format("THe monitor of the tunnel[UUID: %s] has no data:", vo.getUuid()));
                            continue;
                        }
                        Double max = Collections.max(result.getDps().values());
                        Double min = Collections.min(result.getDps().values());

                        TunnelStatus status;
                        if (min.compareTo(PACKETS_LOST_MAX) > 0)
                            status = TunnelStatus.Disconnected;
                        else if (max.compareTo(PACKETS_LOST_MIN) < 0)
                            status = TunnelStatus.Connected;
                        else
                            status = TunnelStatus.Warning;
                        if (vo.getStatus() != status) {
                            UpdateQuery.New(TunnelVO.class)
                                    .eq(TunnelVO_.uuid, vo.getUuid())
                                    .eq(TunnelVO_.state, TunnelState.Enabled)
                                    .eq(TunnelVO_.monitorState, TunnelMonitorState.Enabled)
                                    .set(TunnelVO_.status, status)
                                    .update();
                        }
                        logger.debug(String.format("change tunnel[UUID: %s] status %s to %s:", vo.getUuid(), vo.getStatus(), status));
                    }
                }
                tunnelVOs.clear();
            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
                tunnelVOs.clear();
            }
        }

        private String getOpenTSDBQueryCondition(String tunnelUuid, String metric, Long startTime, Long endTime) {
            List<Query> queries = new ArrayList<>();

            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();
            for (TunnelSwitchPortVO tunnelPort : tunnel.getTunnelSwitchPortVOS()) {
                if (!tunnelPort.getSortTag().equals("A") && !tunnelPort.getSortTag().equals("Z"))
                    continue;
                PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPort(tunnelPort.getSwitchPortUuid());
                Tags tags = new Tags(physicalSwitch.getmIP(), "Vlanif" + tunnelPort.getVlan(), tunnelUuid);
                Query query = new Query("avg", metric, tags);
                queries.add(query);
                break;
            }

            QueryCondition condition = new QueryCondition(startTime, endTime, queries);
            return JSONObjectUtil.toJsonString(condition);
        }

        private PhysicalSwitchVO getPhysicalSwitchBySwitchPort(String switchPortUuid) {
            String switchUuid = Q.New(SwitchPortVO.class)
                    .eq(SwitchPortVO_.uuid, switchPortUuid)
                    .select(SwitchPortVO_.switchUuid)
                    .findValue();

            String physicalSwitchUuid = Q.New(SwitchVO.class)
                    .eq(SwitchVO_.uuid, switchUuid)
                    .select(SwitchVO_.physicalSwitchUuid)
                    .findValue();

            return Q.New(PhysicalSwitchVO.class)
                    .eq(PhysicalSwitchVO_.uuid, physicalSwitchUuid)
                    .find();
        }

    }

    @Override
    public boolean start() {
        restartStatusCheck();
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

}
