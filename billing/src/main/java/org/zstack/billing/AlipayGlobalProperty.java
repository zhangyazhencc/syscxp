package org.zstack.billing;

import org.zstack.core.GlobalProperty;
import org.zstack.core.GlobalPropertyDefinition;
import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

/**
 */
@GlobalPropertyDefinition
public class AlipayGlobalProperty {

    @GlobalProperty(name = "app_id")
    public static String APP_ID;

    @GlobalProperty(name = "merchant_private_key")
    public static String MERCHANT_PRIVATE_KEY;

    @GlobalProperty(name = "alipay_public_key")
    public static String ALIPAY_PUBLIC_KEY;

    @GlobalProperty(name = "notify_url")
    public static String NOTIFY_URL;

    @GlobalProperty(name = "return_url")
    public static String RETURN_URL;

    @GlobalProperty(name = "sign_type")
    public static String SIGN_TYPE;

    @GlobalProperty(name = "charset")
    public static String CHARSET;


    @GlobalProperty(name = "gatewayUrl")
    public static String GATEWAYURL;


    @GlobalProperty(name = "log_path")
    public static String LOG_PATH;

    @GlobalProperty(name = "seller_id")
    public static String SELLER_ID;

}

