package com.syscxp.core.identity;

import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.header.account.APIGetSecretKeyMsg;
import com.syscxp.header.account.APIGetSecretKeyReply;
import com.syscxp.header.account.APILogInBySecretIdMsg;
import com.syscxp.header.account.APILogInBySecretIdReply;
import com.syscxp.header.message.APIReply;
import org.springframework.beans.factory.annotation.Autowired;
import com.syscxp.header.apimediator.ApiMessageInterceptionException;
import com.syscxp.header.identity.IdentityErrors;
import com.syscxp.header.identity.SessionInventory;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import java.util.List;


public class DefaultIdentityInterceptor extends AbstractIdentityInterceptor {

    @Autowired
    private RESTFacade restf;

    @Override
    public SessionInventory getSessionInventory(String sessionUuid) {
        SessionInventory session = sessions.get(sessionUuid);
        if (session == null) {
            throw new ApiMessageInterceptionException(errf.instantiateErrorCode(IdentityErrors.INVALID_SESSION, "Session expired"));
        }

        return session;
    }

    @Override
    protected void localFirstLogin(SessionInventory session) {
    }

    @Override
    protected void logOutSessionRemove(String sessionUuid) {
    }

    @Override
    public String getSecretKey(String secretId, String ip) throws Exception {
        APIGetSecretKeyMsg aMsg = new APIGetSecretKeyMsg();
        aMsg.setSecretId(secretId);
        aMsg.setIP(ip);
        InnerMessageHelper.setMD5(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, RESTApiDecoder.dump(aMsg), RestAPIResponse.class);

        APIReply replay = (APIReply) RESTApiDecoder.loads(rsp.getResult());
        if (replay.isSuccess()) {
            return ((APIGetSecretKeyReply) replay).getSecretKey();
        } else {
            logger.debug(replay.getError().toString());
            throw new Exception(replay.getError().getDetails());
        }
    }

    @Override
    public SessionInventory getSessionUuid(String secretId, String secretKey) throws Exception {
        SessionInventory session = sessions.get(secretId);
        if (session != null) {
            return session;
        }
        APILogInBySecretIdMsg aMsg = new APILogInBySecretIdMsg();
        aMsg.setSecretId(secretId);
        aMsg.setSecretKey(secretKey);
        InnerMessageHelper.setMD5(aMsg);
        RestAPIResponse rsp = restf.syncJsonPost(IdentityGlobalProperty.ACCOUNT_SERVER_URL, RESTApiDecoder.dump(aMsg), RestAPIResponse.class);

        APIReply replay = (APIReply) RESTApiDecoder.loads(rsp.getResult());
        if (replay.isSuccess()) {
            session = ((APILogInBySecretIdReply) replay).getSession();

            return session;
        } else {
            logger.debug(replay.getError().toString());
            throw new Exception(replay.getError().getDetails());
        }
    }

}
