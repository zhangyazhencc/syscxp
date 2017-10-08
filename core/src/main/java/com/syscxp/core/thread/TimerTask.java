package com.syscxp.core.thread;

import com.syscxp.header.HasThreadContext;

/**
 * Created by frank on 8/5/2015.
 */
public interface TimerTask extends HasThreadContext {
    boolean run();
}
