package org.zstack.core.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.CoreGlobalProperty;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.identity.APIGetSessionPolicyMsg;
import org.zstack.header.identity.APIGetSessionPolicyReply;
import org.zstack.header.identity.IdentityErrors;
import org.zstack.header.identity.SessionPolicyInventory;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.header.rest.RestAPIState;


public class DefaultIdentityInterceptor extends AbstractIdentityInterceptor {

    @Autowired
    private RESTFacade restf;
    @Autowired
    private ErrorFacade errf;


    @Override
    public SessionPolicyInventory getSessionInventory(String sessionUuid) {
        SessionPolicyInventory session = null;
        APIGetSessionPolicyMsg aMsg = new APIGetSessionPolicyMsg();
        aMsg.setSessionUuid(sessionUuid);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(CoreGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIGetSessionPolicyReply replay = (APIGetSessionPolicyReply) RESTApiDecoder.loads(rsp.getResult());
            if (replay.isValidSession()) {
                session = replay.getSessionPolicyInventory();
            }

        }
        if (session == null) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
        }
        return session;
    }

    @Override
    public void afterSessionCHeck(String accountUuid) {

    }
}
