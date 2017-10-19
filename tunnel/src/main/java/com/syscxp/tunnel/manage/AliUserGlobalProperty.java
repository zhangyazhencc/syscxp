package com.syscxp.tunnel.manage;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

@GlobalPropertyDefinition
public class AliUserGlobalProperty {

    @GlobalProperty(name = "aliAccessKeyID", defaultValue = "LTAIpgzez4CnzZFX")
    public static String ALI_KEY;

    @GlobalProperty(name = "aliAccessKeySecret", defaultValue = "jPwmElULsvAyZpfqRYOeZrlEr4567s")
    public static String ALI_VALUE;
}
