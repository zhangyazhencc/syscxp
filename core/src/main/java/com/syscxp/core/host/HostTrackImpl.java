package com.syscxp.core.host;

import com.syscxp.core.CoreGlobalProperty;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.core.Platform;
import com.syscxp.core.cloudbus.CloudBus;
import com.syscxp.core.cloudbus.CloudBusCallBack;
import com.syscxp.core.cloudbus.CloudBusSteppingCallback;
import com.syscxp.core.cloudbus.ResourceDestinationMaker;
import com.syscxp.core.db.DatabaseFacade;
import com.syscxp.core.db.SimpleQuery;
import com.syscxp.core.thread.PeriodicTask;
import com.syscxp.core.thread.ThreadFacade;
import com.syscxp.header.Component;
import com.syscxp.header.host.*;
import com.syscxp.header.managementnode.ManagementNodeChangeListener;
import com.syscxp.header.message.MessageReply;
import com.syscxp.header.message.NeedReplyMessage;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 */
public class HostTrackImpl implements HostTracker, ManagementNodeChangeListener, Component {
    private final static CLogger logger = Utils.getLogger(HostTrackImpl.class);

    private final List<String> hostUuids = Collections.synchronizedList(new ArrayList<String>());
    private Set<String> hostInTracking = Collections.synchronizedSet(new HashSet<String>());
    private Future<Void> trackerThread = null;
    private final List<String> inReconnectingHost = Collections.synchronizedList(new ArrayList<String>());
    private final Map<String, Integer> reconnectTimes = Collections.synchronizedMap(new HashMap<>());

    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ResourceDestinationMaker destMaker;
    @Autowired
    private CloudBus bus;
    @Autowired
    private ThreadFacade thdf;

    public void count(String hostUuid) {
        if (reconnectTimes.getOrDefault(hostUuid, 0) <= CoreGlobalProperty.MAX_RECONNECT_TIMES) {
            reconnectTimes.computeIfPresent(hostUuid, (v, i) -> i + 1);
        } else {
            untrackHost(hostUuid);
            reconnectTimes.remove(hostUuid);
        }
    }

    private class Tracker implements PeriodicTask {
        @Override
        public TimeUnit getTimeUnit() {
            return TimeUnit.SECONDS;
        }

        @Override
        public long getInterval() {
            return HostGlobalConfig.PING_HOST_INTERVAL.value(Long.class);
        }

        @Override
        public String getName() {
            return "hostTrack-for-managementNode-" + Platform.getManagementServerId();
        }

        private void handleReply(final String hostUuid, MessageReply reply) {
            if (!reply.isSuccess()) {
                logger.warn(String.format("[Host Tracker]: unable track host[uuid:%s], %s", hostUuid, reply.getError()));
                count(hostUuid);
                return;
            }

            final PingHostReply r = reply.castReply();

            if (!r.isNoReconnect()) {
                boolean needReconnect = false;
                if (!r.isConnected() && HostStatus.Connected.toString().equals(r.getCurrentHostStatus())
                        && HostGlobalConfig.AUTO_RECONNECT_ON_ERROR.value(Boolean.class)) {
                    // cannot ping, but host is in Connected status
                    needReconnect = true;
                } else if (r.isConnected() && HostGlobalConfig.AUTO_RECONNECT_ON_ERROR.value(Boolean.class)
                        && HostStatus.Disconnected.toString().equals(r.getCurrentHostStatus())) {
                    // can ping, but host is in Disconnected status
                    needReconnect = true;
                } else if (!r.isConnected()) {
                    logger.debug(String.format("[Host Tracker]: detected host[uuid:%s] connection lost, " +
                            "but connection.autoReconnectOnError is set to false, no reconnect will issue", hostUuid));
                }

                if (needReconnect && reconnectTimes.putIfAbsent(hostUuid, 0) <= CoreGlobalProperty
                        .MAX_RECONNECT_TIMES) {
                    logger.debug(String.format("[Host Tracker]: detected host[uuid:%s] connection lost, " +
                                    "issue a reconnect because %s is set to true",
                            hostUuid, HostGlobalConfig.AUTO_RECONNECT_ON_ERROR.getCanonicalName()));
                    ReconnectHostMsg msg = new ReconnectHostMsg();
                    msg.setHostUuid(hostUuid);
                    msg.setSkipIfHostConnected(true);
                    bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hostUuid);
                    bus.send(msg, new CloudBusCallBack(null) {
                        @Override
                        public void run(MessageReply reply) {
                            reconnectTimes.remove(hostUuid);

                            if (!reply.isSuccess()) {
                                logger.warn(String.format("host[uuid:%s] failed to reconnect, %s",
                                        hostUuid, reply.getError()));
                                count(hostUuid);
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void run() {
            try {
                List<PingHostMsg> msgs;
                synchronized (hostUuids) {
                    msgs = new ArrayList<PingHostMsg>();
                    for (String huuid : hostUuids) {
                        if (hostInTracking.contains(huuid)) {
                            continue;
                        }

                        PingHostMsg msg = new PingHostMsg();
                        msg.setHostUuid(huuid);
                        bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, huuid);
                        msgs.add(msg);
                        hostInTracking.add(huuid);
                    }
                }

                if (msgs.isEmpty()) {
                    return;
                }

                bus.send(msgs, HostGlobalConfig.HOST_TRACK_PARALLELISM_DEGREE.value(Integer.class),
                        new CloudBusSteppingCallback(null) {
                            @Override
                            public void run(NeedReplyMessage msg, MessageReply reply) {
                                PingHostMsg pmsg = (PingHostMsg) msg;
                                handleReply(pmsg.getHostUuid(), reply);
                                hostInTracking.remove(pmsg.getHostUuid());
                            }
                        });
            } catch (Throwable t) {
                logger.warn("unhandled exception", t);
            }
        }
    }

    public void trackHost(String hostUuid) {
        synchronized (hostUuids) {
            if (!hostUuids.contains(hostUuid)) {
                hostUuids.add(hostUuid);
                logger.debug(String.format("start tracking host[uuid:%s]", hostUuid));
            }
        }
    }

    @Override
    public void untrackHost(String hostUuid) {
        synchronized (hostUuids) {
            hostUuids.remove(hostUuid);
            logger.debug(String.format("stop tracking host[uuid:%s]", hostUuid));
        }
    }

    @Override
    public void trackHost(Collection<String> huuids) {
        synchronized (hostUuids) {
            for (String huuid : huuids) {
                if (!hostUuids.contains(huuid)) {
                    hostUuids.add(huuid);
                    logger.debug(String.format("start tracking host[uuid:%s]", huuid));
                }
            }
        }
    }

    @Override
    public void untrackHost(Collection<String> huuids) {
        synchronized (hostUuids) {
            for (String huuid : huuids) {
                hostUuids.remove(huuid);
                logger.debug(String.format("stop tracking host[uuid:%s]", huuid));
            }
        }
    }

    private void reScanHost() {
        synchronized (hostUuids) {
            hostUuids.clear();

            long count = dbf.count(HostVO.class);
            int times = (int) count / 10000 + (count % 10000 == 0 ? 0 : 1);
            int offset = 0;
            for (int i = 0; i < times; i++) {
                SimpleQuery<HostVO> q = dbf.createQuery(HostVO.class);
                q.select(HostVO_.uuid);
                q.setStart(offset);
                q.setLimit(10000);
                List<String> huuids = q.listValue();
                for (String h : huuids) {
                    if (destMaker.isManagedByUs(h)) {
                        hostUuids.add(h);
                    }
                }

                offset += 10000;
            }
        }
    }

    @Override
    public void nodeJoin(String nodeId) {
        reScanHost();
    }

    @Override
    public void nodeLeft(String nodeId) {
        reScanHost();
    }

    @Override
    public void iAmDead(String nodeId) {

    }

    @Override
    public void iJoin(String nodeId) {

    }

    private void startTracker() {
        if (trackerThread != null) {
            trackerThread.cancel(true);
        }

        trackerThread = thdf.submitPeriodicTask(new Tracker());
    }

    private void setupTracker() {
        startTracker();

        HostGlobalConfig.PING_HOST_INTERVAL.installUpdateExtension((oldConfig, newConfig) -> {
            logger.debug(String.format("%s change from %s to %s, restart tracker thread",
                    oldConfig.getCanonicalName(), oldConfig.value(), newConfig.value()));
            startTracker();
        });
    }

    @Override
    public boolean start() {
        setupTracker();
        return true;
    }

    @Override
    public boolean stop() {
        trackerThread.cancel(true);
        return true;
    }
}
