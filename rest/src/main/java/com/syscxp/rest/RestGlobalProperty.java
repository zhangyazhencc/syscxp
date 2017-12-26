package com.syscxp.rest;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 * Created by xing5 on 2016/12/9.
 */
@GlobalPropertyDefinition
public class RestGlobalProperty {
    @GlobalProperty(name="RestServer.maxCachedApiResults", defaultValue = "2000")
    public static int MAX_CACHED_API_RESULTS;
}