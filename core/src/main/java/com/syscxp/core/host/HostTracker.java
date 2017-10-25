package com.syscxp.core.host;

import java.util.Collection;

/**
 */
public interface HostTracker {
    void trackHost(String hostUuid);

    void untrackHost(String hostUuid);

    void trackHost(Collection<String> hostUuids);

    void untrackHost(Collection<String> hostUuids);
}
