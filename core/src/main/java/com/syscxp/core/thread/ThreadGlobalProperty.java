package com.syscxp.core.thread;

import com.syscxp.core.GlobalPropertyDefinition;
import com.syscxp.core.GlobalProperty;

/**
 */
@GlobalPropertyDefinition
public class ThreadGlobalProperty {
    @GlobalProperty(name="ThreadFacade.maxThreadNum", defaultValue = "100")
    public static int MAX_THREAD_NUM;
}
