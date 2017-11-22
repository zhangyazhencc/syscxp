package com.syscxp.tunnel.tunnel;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.message.APIReply;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
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
    @Autowired
    private ErrorFacade errf;

    private String baseUrl;

    public TunnelRESTCaller() {
        this.baseUrl = CoreGlobalProperty.BILLING_SERVER_URL;
    }

    public TunnelRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T extends APIReply> T syncJsonPost(APIMessage innerMsg) {
        String url = URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(innerMsg);

        RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(innerMsg), RestAPIResponse.class);
        APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
        if (!reply.isSuccess()){
            logger.debug(String.format("call billing failed: %s", reply.getError()));
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, reply.getError()));
        }else{
            return reply.castReply();
        }
    }

}
