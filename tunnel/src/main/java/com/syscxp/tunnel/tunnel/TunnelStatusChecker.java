package com.syscxp.tunnel.tunnel;

import com.alibaba.fastjson.JSON;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.Platform;
import com.syscxp.core.db.Q;
import com.syscxp.core.db.UpdateQuery;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.Component;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.tunnel.monitor.OpenTSDBCommands.*;
import com.syscxp.header.tunnel.switchs.*;
import com.syscxp.header.tunnel.tunnel.*;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
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
    public static final String TUNNEL_PACKETS_LOST = "tunnel.packets.lost";
    public static final String OPENTSDB_SERVER_URL = CoreGlobalProperty.OPENTSDB_SERVER_URL + "/api/query";
    public static final double PACKETS_LOST_MIN = 5;
    public static final double PACKETS_LOST_MAX = 20;

    private void startCleanExpiredProduct() {
        checkTunnelStatusInterval = CoreGlobalProperty.CHECK_TUNNEL_STATUS_INTERVAL;
        if (checkTunnelStatusThread != null) {
            checkTunnelStatusThread.cancel(true);
        }

        checkTunnelStatusThread = thdf.submitPeriodicTask(new CheckTunnelStatus(), TimeUnit.SECONDS.toMillis(10));
        logger.debug(String
                .format("security group cleanExpiredProductThread starts[cleanExpiredProductInterval: %s day]", checkTunnelStatusInterval));
    }


    private class CheckTunnelStatus implements PeriodicTask {

        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.MILLISECONDS;
        }

        @Override
        public long getInterval() {
            return TimeUnit.MINUTES.toMillis(checkTunnelStatusInterval);
        }

        @Override
        public String getName() {
            return "clean-expired-product-" + Platform.getManagementServerId();
        }

        private List<TunnelVO> getTunnels() {

            return Q.New(TunnelVO.class)
                    .eq(TunnelVO_.state, TunnelState.Enabled)
                    .eq(TunnelVO_.monitorState, TunnelMonitorState.Enabled)
                    .list();
        }

        @Override
        public void run() {
            try {
                List<TunnelVO> tunnelVOs = getTunnels();
                logger.debug("tunnel status check.");
                if (tunnelVOs.isEmpty())
                    return;
                for (TunnelVO vo : tunnelVOs) {
                    Long endTime = Instant.now().getEpochSecond();
//                    Long endTime = 1511251350L;
                    Long startTime = endTime - 5 * 30;

                    String condition = getOpenTSDBQueryCondition(vo.getUuid(), TUNNEL_PACKETS_LOST, startTime, endTime);
                    String resp = restf.getRESTTemplate().postForObject(OPENTSDB_SERVER_URL, condition, String.class);
                    List<QueryResult> results = JSONObjectUtil.toCollection(resp, ArrayList.class, QueryResult.class);

                    QueryResult result = results.get(0);
                    Double max = Collections.max(result.getDps().values());
                    Double min = Collections.min(result.getDps().values());

                    TunnelStatus status;
                    if (min > PACKETS_LOST_MAX)
                        status = TunnelStatus.Disconnected;
                    else if (max < PACKETS_LOST_MIN)
                        status = TunnelStatus.Connected;
                    else
                        status = TunnelStatus.Warning;
                    if (vo.getStatus() != status)
                        UpdateQuery.New(TunnelVO.class).eq(TunnelVO_.uuid, vo.getUuid()).set(TunnelVO_.status, status).update();
                }
            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
            }
        }

        private String getOpenTSDBQueryCondition(String tunnelUuid, String metric, Long startTime, Long endTime) {
            List<Query> queries = new ArrayList<>();

            TunnelVO tunnel = Q.New(TunnelVO.class).eq(TunnelVO_.uuid, tunnelUuid).find();
            for (TunnelSwitchPortVO tunnelPort : tunnel.getTunnelSwitchPortVOS()) {
                if (!tunnelPort.getSortTag().equals("A") && !tunnelPort.getSortTag().equals("Z"))
                    continue;
                PhysicalSwitchVO physicalSwitch = getPhysicalSwitchBySwitchPort(tunnelPort.getSwitchPortUuid());
                Tags tags = new Tags(physicalSwitch.getmIP(), "Vlanif" + tunnelPort.getVlan());
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
        startCleanExpiredProduct();

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

}
