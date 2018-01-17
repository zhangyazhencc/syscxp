package com.syscxp.alarm;

import com.syscxp.alarm.header.log.AlarmLogVO;
import com.syscxp.header.billing.ProductType;
import com.syscxp.header.rest.RESTConstant;

/**
 * @Author: sunxuelong.
 * @Cretion Date: 2018-01-17.
 * @Description: .
 */
public class AlarmUtil {

    public static String getProductApiUrl(ProductType productType) {
        String productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL + RESTConstant.REST_API_CALL;
        switch (productType) {
            case TUNNEL:
                productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL + RESTConstant.REST_API_CALL;
                break;
            case VPN:
                productServerUrl = "";
                break;
        }
        return productServerUrl;
    }

    public static String getProductCommandUrl(ProductType productType) {
        String productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL + RESTConstant.COMMAND_CHANNEL_PATH;
        switch (productType) {
            case TUNNEL:
                productServerUrl = AlarmGlobalProperty.TUNNEL_SERVER_RUL + RESTConstant.COMMAND_CHANNEL_PATH;
                break;
            case VPN:
                productServerUrl = "";
                break;
        }
        return productServerUrl;
    }
}
