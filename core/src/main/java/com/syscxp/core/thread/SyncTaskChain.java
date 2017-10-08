package com.syscxp.core.thread;

import com.syscxp.header.core.AsyncBackup;

public interface SyncTaskChain extends AsyncBackup {
    void next();
}
