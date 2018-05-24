package com.syscxp.header.network.l3;

import com.syscxp.core.errorcode.ErrorFacade;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.errorcode.SysErrors;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.utils.Utils;
import com.syscxp.utils.logging.CLogger;
import org.springframework.beans.factory.annotation.Autowired;

public class EcpSyncCall {
    private static final CLogger LOGGER = Utils.getLogger(EcpSyncCall.class);

    @Autowired
    protected RESTFacade restf;
    @Autowired
    protected ErrorFacade errf;

    public APIReply httpSyncCall(final String baseUrl, final APIMessage innerMsg) {

        InnerMessageHelper.setMD5(innerMsg);
        APIReply reply;
        try {
            RestAPIResponse rsp = restf.syncJsonPost(baseUrl, RESTApiDecoder.dump(innerMsg), RestAPIResponse.class, 0);
            reply = (APIReply) RESTApiDecoder.loads(rsp.getResult().replace("org.zstack", "com.syscxp"));
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            reply = new APIReply();
            reply.setError(errf.instantiateErrorCode(SysErrors.HTTP_ERROR, e.getMessage()));
        }
        return reply;
    }

    public APIReply httpSyncCallForEcp(final APIMessage innerMsg) {
        return httpSyncCall(EcpGlobalProperty.ECP_SERVER_API, innerMsg);
    }

}
