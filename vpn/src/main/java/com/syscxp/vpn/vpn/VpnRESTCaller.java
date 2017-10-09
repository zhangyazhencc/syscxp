package com.syscxp.vpn.vpn;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import com.syscxp.core.CoreGlobalProperty;
import com.syscxp.core.identity.InnerMessageHelper;
import com.syscxp.core.rest.RESTApiDecoder;
import com.syscxp.core.retry.Retry;
import com.syscxp.core.retry.RetryCondition;
import com.syscxp.header.errorcode.OperationFailureException;
import com.syscxp.header.exception.CloudRuntimeException;
import com.syscxp.header.message.APIMessage;
import com.syscxp.header.message.APIReply;
import com.syscxp.header.rest.RESTConstant;
import com.syscxp.header.rest.RESTFacade;
import com.syscxp.header.rest.RestAPIResponse;
import com.syscxp.header.rest.RestAPIState;
import com.syscxp.header.vpn.VpnAgentCommand;
import com.syscxp.header.vpn.VpnAgentResponse;
import com.syscxp.utils.URLBuilder;
import com.syscxp.utils.Utils;
import com.syscxp.utils.gson.JSONObjectUtil;
import com.syscxp.utils.logging.CLogger;

import java.util.concurrent.TimeUnit;


@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class VpnRESTCaller {
    private static final CLogger logger = Utils.getLogger(VpnRESTCaller.class);
    @Autowired
    private RESTFacade restf;

    private String baseUrl;

    public VpnRESTCaller(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public VpnRESTCaller() {
        this(CoreGlobalProperty.VPN_BASE_URL);
    }

    public VpnCommands.CheckStatusResponse asyncCheckState(String path, VpnAgentCommand cmd, long interval, long timeout) {
        long curr = 0;
        VpnCommands.CheckStatusResponse rsp = null;
        boolean flag = true;
        do {
            try {
                TimeUnit.SECONDS.sleep(interval);
            } catch (InterruptedException e) {
                logger.debug(String.format("fail to get result[uuid: %s] from Url[%s]", cmd.getVpnUuid(), path));
            }
            try {
                rsp = checkState(path, cmd);
                flag = rsp.getState() == RestAPIState.Processing;
            } catch (OperationFailureException ignored){
            }

            curr += interval;
        }
        while (flag && curr < timeout);

        if (curr >= timeout) {
            throw new CloudRuntimeException(String.format("timeout after %s ms, error %s", curr, rsp.getResult()));
        }
        return rsp;
    }

    public VpnCommands.CheckStatusResponse asyncCheckState(String path, VpnAgentCommand cmd) {
        return asyncCheckState(path, cmd, 1, TimeUnit.MINUTES.toSeconds(10));
    }


    public VpnCommands.CheckStatusResponse checkState(String path, VpnAgentCommand cmd) {
        return syncPost(path, cmd, VpnCommands.CheckStatusResponse.class);
    }

    public VpnAgentResponse.VpnTaskResult syncPostForResult(String path, VpnAgentCommand cmd) {

        VpnAgentResponse rsp = syncPost(path, cmd, VpnAgentResponse.class);

        return rsp.getResult();
    }

    public <T extends VpnAgentResponse> T syncPost(String path, VpnAgentCommand cmd, Class<T> rspClass) {
        String body = JSONObjectUtil.toJsonString(cmd);
        String url = buildUrl(path);
        return restf.syncJsonPost(url, body, rspClass);
    }

    private String buildUrl(String path) {
        return URLBuilder.buildUrlFromBase(baseUrl, VpnConstant.VPN_ROOT_PATH, path);
    }

    public APIReply syncJsonPost(APIMessage innerMsg) {
        String url = URLBuilder.buildUrlFromBase(baseUrl, RESTConstant.REST_API_CALL);
        InnerMessageHelper.setMD5(innerMsg);

        RestAPIResponse rsp = restf.syncJsonPost(url, RESTApiDecoder.dump(innerMsg), RestAPIResponse.class);
        return (APIReply) RESTApiDecoder.loads(rsp.getResult());
    }

}

