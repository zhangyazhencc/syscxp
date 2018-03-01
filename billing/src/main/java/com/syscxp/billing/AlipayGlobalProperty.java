package com.syscxp.billing;

import com.syscxp.core.GlobalProperty;
import com.syscxp.core.GlobalPropertyDefinition;

/**
 */
@GlobalPropertyDefinition
public class AlipayGlobalProperty {

    @GlobalProperty(name = "alipay_app_id")
    public static String ALIPAY_APP_ID;

    @GlobalProperty(name = "alipay_merchant_private_key")
    public static String ALIPAY_MERCHANT_PRIVATE_KEY;

    @GlobalProperty(name = "alipay_public_key")
    public static String ALIPAY_PUBLIC_KEY;

    @GlobalProperty(name = "alipay_notify_url")
    public static String ALIPAY_NOTIFY_URL;

    @GlobalProperty(name = "alipay_return_url")
    public static String ALIPAY_RETURN_URL;

    @GlobalProperty(name = "alipay_sign_type")
    public static String ALIPAY_SIGN_TYPE;

    @GlobalProperty(name = "alipay_charset")
    public static String ALIPAY_CHARSET;


    @GlobalProperty(name = "alipay_gatewayUrl")
    public static String ALIPAY_GATEWAYURL;


    @GlobalProperty(name = "alipay_log_path")
    public static String ALIPAY_LOG_PATH;

    @GlobalProperty(name = "alipay_seller_id")
    public static String ALIPAY_SELLER_ID;

    @GlobalProperty(name = "tunnelServerUrl")
    public static String TUNNEL_SERVER_URL;
    @GlobalProperty(name = "vpnServerUrl")
    public static String VPN_SERVER_URL;

    @GlobalProperty(name = "ecpServerUrl")
    public static String ECP_SERVER_URL;

}

