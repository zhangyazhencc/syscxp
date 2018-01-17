package com.syscxp.alarm;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class AlarmGlobalProperty {

    @GlobalProperty(name = "tunnelServerUrl",defaultValue = "http://192.168.211.99:8083/tunnel")
    public static String TUNNEL_SERVER_RUL;

    @GlobalProperty(name = "alarmServerUrl",defaultValue = "http://192.168.211.99:8085/alarm/api")
    public static String ALARM_SERVER_RUL;

    @GlobalProperty(name = "falconApiUrl",defaultValue = "http://192.168.211.96:6892")
    public static String FALCON_API_URL;

}
