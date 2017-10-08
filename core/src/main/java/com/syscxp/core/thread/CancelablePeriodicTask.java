package com.syscxp.core.thread;

import com.syscxp.header.HasThreadContext;

import java.util.concurrent.TimeUnit;

public interface CancelablePeriodicTask extends HasThreadContext {
	boolean run();
	
    TimeUnit getTimeUnit();
    
    long getInterval();
    
    String getName();
}
