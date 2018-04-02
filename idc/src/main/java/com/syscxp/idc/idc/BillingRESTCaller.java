package com.syscxp.idc.idc;

import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;


@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class BillingRESTCaller {
    private static final CLogger logger = Utils.getLogger(BillingRESTCaller.class);
    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;

    private String baseUrl;

    public BillingRESTCaller() {
        this.baseUrl = CoreGlobalProperty.BILLING_SERVER_URL;
    }

    public BillingRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public <T extends APIReply> T syncJsonPost(APIMessage innerMsg) {
        InnerMessageHelper.setMD5(innerMsg);
        RestAPIResponse rsp = restf.syncJsonPost(URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_CALL),
                RESTApiDecoder.dump(innerMsg), RestAPIResponse.class, 0);
        APIReply reply = (APIReply) RESTApiDecoder.loads(rsp.getResult());
        if (!reply.isSuccess()){
            logger.debug(String.format("call billing failed: %s", reply.getError()));
            throw new OperationFailureException(errf.instantiateErrorCode(SysErrors.BILLING_ERROR, reply.getError().getDetails()));
        }else{
            return reply.castReply();
        }
    }

}
