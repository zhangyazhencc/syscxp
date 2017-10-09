package com.syscxp.tunnel.manage;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;

/**
 * Create by DCY on 2017/9/29
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class TunnelRESTCaller {
    private static final CLogger logger = Utils.getLogger(TunnelRESTCaller.class);
    @Autowired
    private RESTFacade restf;

    private String baseUrl;

    public TunnelRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public TunnelRESTCaller() {
        this(CoreGlobalProperty.VPN_BASE_URL);
    }

    public RestAPIResponse syncJsonPost(APIMessage innerMsg) {
        InnerMessageHelper.setMD5(innerMsg);
        return restf.syncJsonPost(URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_CALL), RESTApiDecoder.dump(innerMsg), RestAPIResponse.class);
    }
}
