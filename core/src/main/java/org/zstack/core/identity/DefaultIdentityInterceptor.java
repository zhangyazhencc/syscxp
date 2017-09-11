package org.zstack.core.identity;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.rest.RESTApiDecoder;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.identity.APIGetSessionPolicyMsg;
import org.zstack.header.identity.APIGetSessionPolicyReply;
import org.zstack.header.identity.IdentityErrors;
import org.zstack.header.identity.SessionInventory;
import org.zstack.header.rest.RESTFacade;
import org.zstack.header.rest.RestAPIResponse;
import org.zstack.header.rest.RestAPIState;

import java.util.List;


public class DefaultIdentityInterceptor extends AbstractIdentityInterceptor {

    @Autowired
    private RESTFacade restf;
    @Override
    protected void removeExpiredSession(List<String> sessionUuids) {

    }

    @Override
    protected SessionInventory getSessionInventory(String sessionUuid) {

        APIGetSessionPolicyMsg aMsg = new APIGetSessionPolicyMsg();
        aMsg.setSessionUuid(sessionUuid);
        InnerMessageHelper.setMD5(aMsg);
        String gstr = RESTApiDecoder.dump(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, gstr, RestAPIResponse.class);
        SessionInventory session = null;
        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIGetSessionPolicyReply replay = (APIGetSessionPolicyReply) RESTApiDecoder.loads(rsp.getResult());
            if (replay.isValidSession()) {
                session = replay.getSessionInventory();
            }
        }

        if (session == null) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
        }

        afterGetSessionInventory(session);

        return session;
    }

    protected void afterGetSessionInventory(SessionInventory session){

    };

    @Override
    protected SessionInventory logOutSessionRemove(String sessionUuid) {
        return null;
    }
}
