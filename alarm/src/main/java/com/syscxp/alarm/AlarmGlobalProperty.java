package com.syscxp.alarm;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class AlarmGlobalProperty {

    @GlobalProperty(name = "tunnelServerUrl",defaultValue = "http://192.168.211.99:8083/tunnel/api")
    public static String TUNNEL_SERVER_RUL;

    @GlobalProperty(name = "falconUrlSave",defaultValue = "http://192.168.211.96:6892/monitoring/strategy/save")
    public static String FALCON_URL_SAVE;

    @GlobalProperty(name = "falconUrlDelete",defaultValue = "http://192.168.211.100:6892/monitoring/strategy/delete")
    public static String FALCON_URL_DELETE;

}
