package org.zstack.tunnel.manage;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.identity.InnerMessageHelper;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.header.message.APIMessage;
import org.zstack.header.rest.RESTConstant;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.utils.URLBuilder;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

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
