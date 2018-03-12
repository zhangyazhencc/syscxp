package com.syscxp.core.identity;

import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.account.APIGetSecretKeyMsg;
import com.syscxp.header.account.APIGetSecretKeyReply;
import com.syscxp.header.account.APILogInBySecretIdMsg;
import com.syscxp.header.account.APILogInBySecretIdReply;
import com.syscxp.header.message.APIReply;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.identity.APIGetSessionPolicyMsg;
import com.syscxp.header.identity.APIGetSessionPolicyReply;
import com.syscxp.header.identity.IdentityErrors;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;

import java.util.List;


public class DefaultIdentityInterceptor extends AbstractIdentityInterceptor {

    @Autowired
    private RESTFacade restf;
    @Override
    public void removeExpiredSession(List<String> sessionUuids) {

    }

    @Override
    public SessionInventory getSessionInventory(String sessionUuid) {

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

    @Override
    public String getSecretKey(String secretId) {
        APIGetSecretKeyMsg aMsg = new APIGetSecretKeyMsg();
        aMsg.setSecretId(secretId);
        InnerMessageHelper.setMD5(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, RESTApiDecoder.dump(aMsg), RestAPIResponse.class);

        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIReply replay = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (replay.isSuccess()) {
                return  ((APIGetSecretKeyReply)replay).getSecretKey();
            }
        }
        return null;
    }

    @Override
    public String getSessionUuid(String secretId, String secretKey) {
        APILogInBySecretIdMsg aMsg = new APILogInBySecretIdMsg();
        aMsg.setSecretId(secretId);
        aMsg.setSecretKey(secretKey);
        InnerMessageHelper.setMD5(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, RESTApiDecoder.dump(aMsg), RestAPIResponse.class);

        if (rsp.getState().equals(RestAPIState.Done.toString())) {
            APIReply replay = (APIReply) RESTApiDecoder.loads(rsp.getResult());
            if (replay.isSuccess()) {
                return  ((APILogInBySecretIdReply)replay).getSessionUuid();
            }
        }
        return null;
    }

}
