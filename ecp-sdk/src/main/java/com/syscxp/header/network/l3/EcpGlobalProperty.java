package com.syscxp.header.network.l3;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;


@GlobalPropertyDefinition
public class EcpGlobalProperty {

    @GlobalProperty(
            name = "ecpServerApi",
            defaultValue = "http://192.168.211.5:8080/zstack/api"
    )
    public static String ECP_SERVER_API;

}

