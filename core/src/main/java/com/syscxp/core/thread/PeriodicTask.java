package com.syscxp.core.thread;

import com.syscxp.header.HasThreadContext;

import java.util.concurrent.TimeUnit;

public interface PeriodicTask extends Runnable, HasThreadContext {
    TimeUnit getTimeUnit();
    
    long getInterval();
    
    String getName();
}
